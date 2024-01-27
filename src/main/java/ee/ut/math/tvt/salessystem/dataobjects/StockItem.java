package ee.ut.math.tvt.salessystem.dataobjects;
import javax.persistence.*;
import java.util.Set;

/**
 * Stock item.
 */

@Entity
@Table(name = "STOCK_ITEM")
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    private int quantity;

    @OneToMany(mappedBy = "stockItem")
    private Set<SoldItem> soldItems;

    public StockItem() {
    }

    public StockItem(Long id, String name, String desc, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.description = desc;
        this.price = price;
        this.quantity = quantity;
    }

    public String getStockItemDescription() {
        return description;
    }

    public void setStockItemDescription(String description) {
        this.description = description;
    }

    public String getStockItemName() {
        return name;
    }

    public void setStockItemName(String name) {
        this.name = name;
    }

    public double getStockItemPrice() {
        return price;
    }

    public void setStockItemPrice(double price) {
        this.price = price;
    }

    public Long getStockItemId() {
        return id;
    }

    public void setStockItemId(Long id) {
        this.id = id;
    }

    public int getStockItemQuantity() {
        return quantity;
    }

    public void setStockItemQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("StockItem{id=%d, name='%s'}", id, name);
    }
}
