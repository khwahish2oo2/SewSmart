package com.example.sewsmart.find_customer;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.example.sewsmart.add_order.NewOrderController;
import com.example.sewsmart.edit_customer.EditCustomerController;
import com.example.sewsmart.show_order.ShowOrderController;
import com.example.sewsmart.sqlconnection.SqlConnectionClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class FindCustomerController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField cname;

    @FXML
    private TextField mobile;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Button addorder;

    @FXML
    private Button editinfo;

    @FXML
    private Button showorder;

    @FXML
    void doSearch(ActionEvent event)
    {

        try {
            PreparedStatement stmt = con.prepareStatement("SELECT cname FROM customers WHERE mobile=?");
            stmt.setString(1, mobile.getText());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                cname.setText(rs.getString("cname"));
                contentArea.getChildren().setAll();
                addorder.setDisable(false);
                showorder.setDisable(false);
                editinfo.setDisable(false);
            }
            else
            {
                cname.setText("Oops, NO CUSTOMER EXIST.");
                contentArea.getChildren().setAll();
                addorder.setDisable(true);
                showorder.setDisable(true);
                editinfo.setDisable(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    void addOrder(ActionEvent event)
    {
        //replacePage("/com/example/sewsmart/add_orderr/NewOrderView.fxml");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sewsmart/add_orderr/NewOrderView.fxml"));
            AnchorPane newContent = loader.load();
            NewOrderController noc = loader.getController();
            noc.setMobile(mobile.getText());
            System.out.println("FXML file loaded successfully.");
            contentArea.getChildren().setAll(newContent);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    void doEdit(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sewsmart/edit_customerr/EditCustomerView.fxml"));
            AnchorPane newContent = loader.load();
            EditCustomerController ecc = loader.getController();
            ecc.setMobile(mobile.getText());
            System.out.println("FXML file loaded successfully.");
            contentArea.getChildren().setAll(newContent);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    void showOrders(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sewsmart/show_orderr/ShowOrderView.fxml"));
            AnchorPane newContent = loader.load();
            ShowOrderController soc = loader.getController();
            soc.setMobile(mobile.getText());
            System.out.println("FXML file loaded successfully.");
            contentArea.getChildren().setAll(newContent);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    Connection con;
    @FXML
    void initialize()
    {
        con = SqlConnectionClass.doConnect();
        if (con == null)
            System.out.println("Connection did not established");
        else
            System.out.println("Connection established");

    }

}

