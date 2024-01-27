package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Entity
@Table(name = "PURCHASE")
public class Purchase implements Comparable<Purchase>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long PurchaseID;

    @OneToMany(mappedBy = "purchase")
    private List<SoldItem> soldItems;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "total")
    private double total;

    public Purchase() {}

    public Purchase(List<SoldItem> soldItems, LocalDateTime date) {
        this.soldItems = soldItems;
        this.date = date;
    }

    public List<SoldItem> getSoldItemsList() {
        return soldItems;
    }

    public void setSoldItemsList(List<SoldItem> soldItems) {
        this.soldItems = soldItems;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public String getDateFormated(){
        String year = String.valueOf(this.getDate().getYear());
        String month = String.valueOf(this.getDate().getMonth());
        String day = String.valueOf(this.getDate().getDayOfMonth());
        return day+"-"+month+"-"+year;
    }

    public String getTime(){
        return String.valueOf(this.getDate().toLocalTime());
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getTotalItemsPrice() {
        double total = 0;
        for (SoldItem item : soldItems) {
            total += item.getSoldItemPrice()*item.getQuantityOfSoldItem();
        }
        return total;
    }

    public double getTotal() {
        total = 0;
        for (int i = 0; i < soldItems.size(); i++) {
            total += soldItems.get(i).getSumOfTotalCost();
        }
        return total;
    }

    public int compareTo(Purchase o) {
        return this.date.compareTo(o.getDate());
    }
}
