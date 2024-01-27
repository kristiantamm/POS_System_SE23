package ee.ut.math.tvt.salessystem.dataobjects;
import javax.persistence.*;

/**
 * Already bought StockItem. SoldItem duplicates name and price for preserving history.
 */

@Entity
@Table(name = "SOLD_ITEM")
public class SoldItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "STOCK_ITEM_ID", nullable = false)
    private StockItem stockItem;

    @Column(name = "name")
    private String name;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "price")
    private double price;

    @Column(name = "sum")
    private double sum;

    @ManyToOne
    @JoinColumn(name = "PURCHASE_ID", nullable = false)
    private Purchase purchase;

    public SoldItem() {
    }

    public SoldItem(StockItem stockItem, int quantity) {
        this.stockItem = stockItem;
        this.name = stockItem.getStockItemName();
        this.price = stockItem.getStockItemPrice();
        this.quantity = quantity;
        this.sum = quantity * price;
    }

    public Long getSoldItemId() {
        return stockItem.getStockItemId();
    }

    public void setSoldItemId(Long id) {
        this.id = id;
    }

    public String getSoldItemName() {
        return name;
    }

    public void setSoldItemName(String name) {
        this.name = name;
    }

    public double getSoldItemPrice() {
        return price;
    }

    public void setSoldItemPrice(double price) {
        this.price = price;
    }

    public Integer getQuantityOfSoldItem() {
        return quantity;
    }

    public void setQuantityOfSoldItem(Integer quantity) {
        this.quantity = quantity;
    }

    public double getSumOfTotalCost() {
        return price * ((double) quantity);
    }

    public StockItem getStockItem() {
        return stockItem;
    }
    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    @Override
    public String toString() {
        return String.format("SoldItem{id=%d, name='%s'}", id, name);
    }
}
