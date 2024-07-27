package com.example.sewsmart.enroll_customer;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.example.sewsmart.sqlconnection.SqlConnectionClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

public class EnrollCustomerController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField address;

    @FXML
    private ComboBox<String> city;

    @FXML
    private TextField cname;

    @FXML
    private Label custid;

    @FXML
    private DatePicker dob;

    @FXML
    private TextField email;

    @FXML
    private ComboBox<String> gender;

    @FXML
    private TextField mobile;

    @FXML
    void doClearAll(ActionEvent event)
    {
        mobile.clear(); // Clear text fields
        email.clear();
        cname.clear();
        gender.getSelectionModel().clearSelection(); // Clear selection in ComboBox
        address.clear();
        city.getSelectionModel().clearSelection(); // Clear selection in ComboBox
        dob.setValue(null);
        custid.setText("");
    }

//  cid, mobile, email, cname, gender, address, city, dob
    PreparedStatement stmt;
    @FXML
    void doSave(ActionEvent event)
    {
        try {
            stmt=con.prepareStatement("insert into customers values(?,?,?,?,?,?,?)");
            stmt.setString(1,mobile.getText());
            stmt.setString(2,email.getText());
            stmt.setString(3,cname.getText());
            stmt.setString(4,(gender.getSelectionModel().getSelectedItem()));
            stmt.setString(5,address.getText());
            stmt.setString(6,(city.getSelectionModel().getSelectedItem()));
            LocalDate local=dob.getValue();
            java.sql.Date date=java.sql.Date.valueOf(local);
            stmt.setDate(7, date);
            stmt.executeUpdate();
            custid.setText("Customer got enrolled successfully.");
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
    }

    Connection con;
    @FXML
    void initialize() {
        con= SqlConnectionClass.doConnect();
        if(con==null)
            System.out.println("Connection did not Established");
        else
            System.out.println("Connection Established");

        gender.getItems().add("Select");
        gender.getItems().add("Female");
        gender.getItems().add("Male");
        gender.getItems().add("Other");
        gender.getSelectionModel().select(0);

        city.getItems().add("Bathinda");
        city.getItems().add("Bhuchu");
        city.getItems().add("Mansa");
        city.getItems().add("Moga");
        city.getItems().add("Muktsar");
        city.getItems().add("Rampura");
    }
}
