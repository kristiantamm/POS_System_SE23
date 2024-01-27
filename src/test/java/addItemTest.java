import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class addItemTest {
    private SalesSystemDAO mockDao;
    private ShoppingCart sc;
    private StockItem stockItem;
    private StockItem stockItemQ0;
    private SoldItem soldItem;

    @Before
    public void setUp(){
        mockDao = mock(SalesSystemDAO.class);
        sc = new ShoppingCart(mockDao);
        stockItem = new StockItem(Long.parseLong("1"), "testItem","testDesc",5,  100);
        mockDao.saveStockItem(stockItem);
        stockItemQ0 = new StockItem(Long.parseLong("2"), "testItem","testDesc",5,  0);
        mockDao.saveStockItem(stockItemQ0);
        soldItem = new SoldItem(stockItem, 10);
    }
    @Test
    public void testAddingNewItem() {
        sc.cancelCurrentPurchase();
        sc.addItemToShoppingCart(soldItem);
        assertEquals(sc.getAllItems().get(0), soldItem);
    }
    @Test
    public void testAddingExistingItem() {
        sc.cancelCurrentPurchase();
        sc.addItemToShoppingCart(soldItem);
        long q1 = sc.getAllItems().get(0).getQuantityOfSoldItem();
        sc.addItemToShoppingCart(soldItem);
        long q2 = sc.getAllItems().get(0).getQuantityOfSoldItem();
        assertEquals(q1 * 2, q2);
    }

    @Test(expected = SalesSystemException.class)
    public void testAddingItemWithLowStock() {
        sc.cancelCurrentPurchase();
        sc.addItemToShoppingCart(new SoldItem(stockItemQ0, 10));
    }
}
