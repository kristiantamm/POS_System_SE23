package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class StockController implements Initializable {
    private static final Logger log = LogManager.getLogger(SalesSystemUI.class);

    private final SalesSystemDAO dao;

    @FXML
    private TextField newBarcode;
    @FXML
    private TextField newAmount;
    @FXML
    private TextField newName;
    @FXML
    private TextField newPrice;
    @FXML
    private Button addItem;
    @FXML
    private TableView<StockItem> warehouseTableView;

    public StockController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        // TODO refresh view after adding new items
    }

    @FXML
    public void addItemClickedLogger(){
        log.info("Add item button clicked");
        addStockItem();
    }
    public void addStockItem() {
        if (getStockItemByBarcode() == null) {
            long barcode;
            if(newBarcode.getText().isEmpty()) {
                barcode = generateBarcode();
            }
            else{
                barcode = Long.parseLong(newBarcode.getText());
            }
            dao.beginTransaction();
            try {
                StockItem stockItem = new StockItem(barcode, newName.getText(), "", Double.parseDouble(newPrice.getText()), Integer.parseInt(newAmount.getText()));
                dao.saveStockItem(stockItem);
                dao.commitTransaction();
                log.info(stockItem.getStockItemName() + " added and saved");
            }
            catch (Exception e){
                log.error(e);
                dao.rollbackTransaction();
            }
        }
        else{
            dao.beginTransaction();
            try {
                dao.increaseStockItem(Long.parseLong(newBarcode.getText()), Integer.parseInt(newAmount.getText()));
                refreshStockItems();
                dao.commitTransaction();
                log.info(getStockItemByBarcode().toString() + " stock increased by " + newAmount.getText());
            } catch (Exception e) {
                dao.rollbackTransaction();
                throw e;
            }
        }
    }

    private boolean barcodeExists(long barcode){
        List<StockItem> stockItemList = dao.findStockItems();
        for(StockItem item : stockItemList){
            if(item.getStockItemId() == barcode){
                return true;
            }
        }
        return false;
    }
    private long generateBarcode() {
        long newBarcode = 1;
        while(barcodeExists(newBarcode)){
            newBarcode++;
        }
        return newBarcode;
    }

    private StockItem getStockItemByBarcode() {
        try {
            long code = Long.parseLong(newBarcode.getText());
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @FXML
    public void stockRefreshButtonClicked() {
        log.info("Stock updated");
        refreshStockItems();
    }

    private void refreshStockItems() {
        List<StockItem> stockItems = dao.findStockItems();

        // Sort the stock items based on quantity
        Collections.sort(stockItems, Comparator.comparingInt(StockItem::getStockItemQuantity));
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        warehouseTableView.refresh();
    }

}
