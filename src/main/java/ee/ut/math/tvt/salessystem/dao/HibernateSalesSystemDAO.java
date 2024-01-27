package ee.ut.math.tvt.salessystem.dao;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;



public class HibernateSalesSystemDAO implements SalesSystemDAO {
    private final EntityManagerFactory emf;
    private final EntityManager em;
    public HibernateSalesSystemDAO () {
        // if you get ConnectException / JDBCConnectionException then you
        // probably forgot to start the database before starting the application
        emf = Persistence.createEntityManagerFactory("pos");
        em = emf.createEntityManager();
    }
    // TODO implement missing methods
    public void close () {
        em.close ();
        emf.close ();
    }

    @Override
    public List<StockItem> findStockItems() {
        /*String jpqlQuery = "select entity from StockItem entity";
        Query query = em.createQuery(jpqlQuery);
        List<StockItem> resultList = query.getResultList();
         */
        List<StockItem> resultList = new ArrayList<>();
        return resultList;
    }

    @Override
    public StockItem findStockItem(long id) {
        StockItem stockItem = em.find(StockItem.class, id);
        return stockItem;
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        em.persist(stockItem);
    }

    @Override
    public void increaseStockItem(long id, int amount) {
        StockItem item = findStockItem(id);
        item.setStockItemQuantity(item.getStockItemQuantity()+amount);
        em.merge(item);
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        em.persist(item);
    }

    @Override
    public void beginTransaction () {
        //em.getTransaction (). begin ();
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
    }
    @Override
    public void rollbackTransaction () {
        //em.getTransaction (). rollback ();
        EntityTransaction transaction = em.getTransaction();
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }
    @Override
    public void commitTransaction () {
        //em.getTransaction (). commit ();
        EntityTransaction transaction = em.getTransaction();
        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    @Override
    public void savePurchase(Purchase purchase) {
        em.persist(purchase);
    }

    @Override
    public List<Purchase> findPurchases() {
        /*String jpqlQuery = "select entity from Purchase entity";
        Query query = em.createQuery(jpqlQuery);
        List<Purchase> resultList = query.getResultList();
         */
        List<Purchase> resultList = new ArrayList<>();
        return resultList;
    }


}
