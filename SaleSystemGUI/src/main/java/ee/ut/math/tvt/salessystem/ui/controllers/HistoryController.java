package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 */
public class HistoryController implements Initializable {
    private final SalesSystemDAO dao;
    private static final Logger log = LogManager.getLogger(SalesSystemUI.class);
    private SalesSystemUI application;

    @FXML
    private DatePicker historyStartDate;

    @FXML
    private DatePicker historyEndDate;

    @FXML
    private TableView<Purchase> historyTableView;

    @FXML
    private TableView<SoldItem> purchaseHistoryTableView;

    public HistoryController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    public void setMainApp(SalesSystemUI application) {
            this.application = application;
        }

    @FXML
    private void showHistoryBetweenDates() {
        LocalDateTime startLocalDate = historyStartDate.getValue().atStartOfDay();
        LocalDateTime endLocalDate = historyEndDate.getValue().atStartOfDay();
        List<Purchase> purchasesList = dao.findPurchases();
        Collections.sort(purchasesList);
        List<Purchase> inRange = new ArrayList<>();
        for(Purchase p : purchasesList){
            LocalDateTime pDate = p.getDate();
            if(pDate.isAfter(startLocalDate) && pDate.isBefore(endLocalDate)){
                inRange.add(p);
            }
        }
        historyTableView.setItems(FXCollections.observableList(inRange));
        historyTableView.refresh();
    }

    @FXML
    private void showLast10Purchases() {
        // Logic for showing last 10 records
        List<Purchase> purchasesList = dao.findPurchases();
        Collections.sort(purchasesList);
        List<Purchase> inRange = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            inRange.add(purchasesList.get(i));
        }
        historyTableView.setItems(FXCollections.observableList(inRange));
        historyTableView.refresh();
    }

    @FXML
    private void showAllPurchases() {
        // Logic for showing all records
        List<Purchase> purchasesList = dao.findPurchases();
        Collections.sort(purchasesList);
        historyTableView.setItems(FXCollections.observableList(purchasesList));
        historyTableView.refresh();
    }

    @FXML
    private void refreshHistory() {
        historyTableView.setItems(FXCollections.observableList(dao.findPurchases()));
        historyTableView.refresh();
        log.info("History refreshed.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("History tab opened.");
        refreshHistory();
        historyTableView.setRowFactory(tv ->{
            TableRow<Purchase> row = new TableRow<>();
            row.setOnMouseClicked(event ->{
                Purchase purchase = row.getItem();
                log.info("A row was clicked.");
                handleHistoryTableRowClick(purchase);
            });
            return row;
        });
    }
    private void handleHistoryTableRowClick(Purchase purchase){
        purchaseHistoryTableView.setItems(FXCollections.observableList(purchase.getSoldItemsList()));
        purchaseHistoryTableView.refresh();
    }
}
