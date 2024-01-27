package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


public class ShoppingCart {

    //private static final Logger log = LogManager.getLogger(ShoppingCart.class);

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();

    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
    }

    /**
     * Add new SoldItem to table.
     */
    public void addItemToShoppingCart(SoldItem item) {
        for(SoldItem i : items){
            if(i.getSoldItemId().equals(item.getSoldItemId())){
                if(item.getStockItem().getStockItemQuantity() - item.getQuantityOfSoldItem() >= 0){
                    i.setQuantityOfSoldItem(i.getQuantityOfSoldItem()+item.getQuantityOfSoldItem());
                    //log.debug("Increased quantity of " + item.getName() + " by " + item.getQuantity + " units.")
                }else{
                    throw new SalesSystemException("Not enough stock of" + item.getSoldItemName() + "in the warehouse");
                }
                return;
            }
        }
        if(item.getStockItem().getStockItemQuantity() >= item.getQuantityOfSoldItem()) {
            items.add(item);
            //log.debug("Added " + item.getName() + " quantity of " + item.getQuantity());
        }
        else{
            throw new SalesSystemException("Not enough stock of" + item.getSoldItemName() + "in the warehouse");
        }
    }

    public List<SoldItem> getAllItems() {
        return items;
    }

    public void cancelCurrentPurchase() {
        items.clear();
    }

    public void submitCurrentPurchase() {
        // TODO decrease quantities of the warehouse stock
        for(SoldItem soldItem : items){
            soldItem.getStockItem().setStockItemQuantity(soldItem.getStockItem().getStockItemQuantity()-soldItem.getQuantityOfSoldItem());
        }
        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596
        dao.beginTransaction();
        try {
            for (SoldItem item : items) {
                dao.saveSoldItem(item);
            }
            dao.savePurchase(new Purchase(new ArrayList<>(items), LocalDateTime.now()));
            dao.commitTransaction();
            items.clear();
        } catch (Exception e) {
            dao.rollbackTransaction();
            throw e;
        }
    }
}
