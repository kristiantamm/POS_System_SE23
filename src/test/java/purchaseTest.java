import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.Before;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
public class purchaseTest {
    private ShoppingCart sc;
    private StockItem stockItem;
    private InMemorySalesSystemDAO mockDAO;
    private SoldItem soldItem;
    @Before
    public void setUp(){
        mockDAO  = mock(InMemorySalesSystemDAO.class);
        stockItem = new StockItem(1L, "testItem","testDesc",5,  100);
        mockDAO.saveStockItem(stockItem);
        sc = new ShoppingCart(mockDAO);
    }
    //@Test
    public void testHistoryUpdatesAfterPurchase(){
        soldItem = new SoldItem(stockItem, 10);
        sc.addItemToShoppingCart(soldItem);
        sc.submitCurrentPurchase();
        System.out.println(mockDAO.findPurchases().size());
        assertEquals(mockDAO.findPurchases().get(0).getSoldItemsList().get(0), soldItem);
    }
    //@Test
    public void warehouseUpdatesAfterPurchase(){
        int quantity = mockDAO.findStockItem(1L).getStockItemQuantity();
        System.out.println(quantity);
        soldItem = new SoldItem(stockItem, 10);
        sc.addItemToShoppingCart(soldItem);
        sc.submitCurrentPurchase();
        assertEquals(quantity - 10, mockDAO.findStockItem(1L).getStockItemQuantity());
    }
}
