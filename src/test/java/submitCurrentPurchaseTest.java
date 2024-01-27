import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class submitCurrentPurchaseTest {
    private SalesSystemDAO mockDao;
    private ShoppingCart sc;
    private StockItem stockItem;
    private SoldItem soldItem;

    @Before
    public void setUp(){
        mockDao = mock(SalesSystemDAO.class);
        stockItem = new StockItem(Long.parseLong("1"), "testItem","testDesc",5,  10);
        mockDao.saveStockItem(stockItem);
        soldItem = new SoldItem(stockItem, 1);
        sc = new ShoppingCart(mockDao);
        sc.addItemToShoppingCart(soldItem);
    }
    @Test
    public void testSubmittingCurrentPurchaseDecreasesStockItemQuantity(){
        sc.submitCurrentPurchase();
        assertEquals(9, stockItem.getStockItemQuantity());
    }

    @Test
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction(){
        sc.submitCurrentPurchase();
        InOrder inOrder = inOrder(mockDao);
        inOrder.verify(mockDao).beginTransaction();
        inOrder.verify(mockDao).commitTransaction();
    }
    @Test
    public void testSubmittingCurrentOrderCreatesHistoryItem(){
        sc.submitCurrentPurchase();
        assertEquals(mockDao.findPurchases().get(0).getSoldItemsList().get(0), soldItem);
    }
    @Test
    public void testSubmittingCurrentOrderSavesCorrectTime(){
        sc.submitCurrentPurchase();
        LocalDateTime submitTime = LocalDateTime.now();
        Purchase purchase = mockDao.findPurchases().get(0);
        assertNotNull("Timestamp should not be null", purchase.getDate());
        LocalDateTime purchaseItemTime = purchase.getDate();
        Duration duration = Duration.between(purchase.getDate(), submitTime);
        assertTrue("Timestamp difference is too large", duration.getSeconds() < 1);
    }
    @Test
    public void testCancellingOrder(){
        sc.cancelCurrentPurchase();
        assertTrue("Shopping cart isn't empty after canceling the purchase", sc.getAllItems().isEmpty());
    }
    @Test
    public void testCancellingOrderQuanititesUnchanged(){
        int startQuantity = stockItem.getStockItemQuantity();
        sc.cancelCurrentPurchase();
        assertEquals(startQuantity, stockItem.getStockItemQuantity());
    }
}
