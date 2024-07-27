package com.example.sewsmart.edit_customer;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.example.sewsmart.sqlconnection.SqlConnectionClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditCustomerController {

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
    private Label msg;

    @FXML
    private DatePicker dob;

    @FXML
    private TextField email;

    @FXML
    private ComboBox<String> gender;

    @FXML
    private TextField mobile;

    public void setMobile(String mob)
    {
        mobile.setText(mob);
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM customers WHERE mobile = ?");
            stmt.setString(1, mobile.getText());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                cname.setText(rs.getString("cname"));
                mobile.setText(rs.getString("mobile"));
                address.setText(rs.getString("address"));
                email.setText(rs.getString("email"));
                Date dt = rs.getDate("dob");//col name
                dob.setValue(dt.toLocalDate());
                String cityy=rs.getString("city");
                String genderr=rs.getString("gender");
                city.getSelectionModel().select(cityy);
                gender.getSelectionModel().select(genderr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void doClearAll(ActionEvent event)
    {
        cname.clear();
        address.clear();
        email.clear();
        gender.getSelectionModel().select(0);
        city.getSelectionModel().select(0);
        dob.setValue(null);
        msg.setText("");
    }

    //mobile, email, cname, gender, address, city, dob
    @FXML
    void doUpdate(ActionEvent event)
    {
        try
        {
            stmt=con.prepareStatement("update customers set email=?,cname=?, gender=?, address=?, city=?, dob=? where mobile=?");
            stmt.setString(1,email.getText());
            stmt.setString(2,cname.getText());
            stmt.setString(3,gender.getSelectionModel().getSelectedItem());
            stmt.setString(4,address.getText());
            stmt.setString(5,city.getSelectionModel().getSelectedItem());
            LocalDate local=dob.getValue();
            java.sql.Date date=java.sql.Date.valueOf(local);
            stmt.setDate(6, date);
            stmt.setString(7,mobile.getText());
            stmt.executeUpdate();
            msg.setText("Details updated Successfully.");
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
    }

    PreparedStatement stmt;
    Connection con;
    @FXML
    void initialize()
    {
        con = SqlConnectionClass.doConnect();
        if (con == null)
            System.out.println("Connection did not Established");
        else
            System.out.println("Connection Established");

        gender.getItems().add("Select");
        gender.getItems().add("Female");
        gender.getItems().add("Male");
        gender.getItems().add("Other");
        gender.getSelectionModel().select(0);
        city.getItems().add("Select");
        city.getItems().add("Bathinda");
        city.getItems().add("Bhuchu");
        city.getItems().add("Mansa");
        city.getItems().add("Moga");
        city.getItems().add("Muktsar");
        city.getItems().add("Rampura");
        city.getSelectionModel().select(0);
    }

}

