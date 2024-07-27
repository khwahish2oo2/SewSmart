package com.example.sewsmart.sales_analysis;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ResourceBundle;

import com.example.sewsmart.sqlconnection.SqlConnectionClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

public class SalesAnalysisController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private PieChart dressPie;

    @FXML
    private PieChart orderStatus;

    @FXML
    private Label cust;

    @FXML
    private Label order;

    @FXML
    private Label earn;

    private void loadData() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try {
            // Replace with your actual database connection logic
            PreparedStatement stmt = con.prepareStatement("SELECT dress, COUNT(*) as count FROM orders GROUP BY dress");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String dressType = rs.getString("dress");
                int count = rs.getInt("count");
                pieChartData.add(new PieChart.Data(dressType + " (" + count + ")", count));
            }
            dressPie.setData(pieChartData);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }





    Connection con;
    @FXML
    void initialize()
    {
        con= SqlConnectionClass.doConnect();
        if(con==null)
            System.out.println("Connection did not Established");
        else
            System.out.println("Connection Established");

        loadData();
        populateLabels();
    }

    private void populateLabels() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        populateNewCustomers(startOfMonth, endOfMonth);
        populateOrdersPlaced(startOfMonth, endOfMonth);
        populateTotalEarnings(startOfMonth, endOfMonth);
    }

    private void populateNewCustomers(LocalDate startOfMonth, LocalDate endOfMonth) {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) AS newCustomers FROM customers WHERE dob BETWEEN ? AND ?");
            stmt.setDate(1, java.sql.Date.valueOf(startOfMonth));
            stmt.setDate(2, java.sql.Date.valueOf(endOfMonth));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int newCustomers = rs.getInt("newCustomers");
                cust.setText(String.valueOf(newCustomers));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateOrdersPlaced(LocalDate startOfMonth, LocalDate endOfMonth) {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) AS ordersPlaced FROM orders WHERE od BETWEEN ? AND ?");
            stmt.setDate(1, java.sql.Date.valueOf(startOfMonth));
            stmt.setDate(2, java.sql.Date.valueOf(endOfMonth));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int ordersPlaced = rs.getInt("ordersPlaced");
                order.setText(String.valueOf(ordersPlaced));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateTotalEarnings(LocalDate startOfMonth, LocalDate endOfMonth) {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT SUM(bill) AS totalEarnings FROM orders WHERE dod BETWEEN ? AND ?");
            stmt.setDate(1, java.sql.Date.valueOf(startOfMonth));
            stmt.setDate(2, java.sql.Date.valueOf(endOfMonth));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double totalEarnings = rs.getDouble("totalEarnings");
                earn.setText(String.format("%.2f", totalEarnings));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
