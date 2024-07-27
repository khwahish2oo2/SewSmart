package com.example.sewsmart.add_worker;
import com.example.sewsmart.sqlconnection.SqlConnectionClass;

import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class AddWorkerController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField address;

    @FXML
    private CheckBox blazzer;

    @FXML
    private CheckBox blouse;

    @FXML
    private TextField email;

    @FXML
    private DatePicker doj;

    @FXML
    private CheckBox lsuit;

    @FXML
    private TextField mobile;

    @FXML
    private Label msg;

    @FXML
    private CheckBox msuit;

    @FXML
    private CheckBox pant;

    @FXML
    private TextField salary;

    @FXML
    private CheckBox shirt;

    @FXML
    private TextField splz;

    @FXML
    private TextField wname;

    @FXML
    void addInSplz(ActionEvent event)
    {
        StringBuilder selectedSpecializations = new StringBuilder();

        // Check each checkbox and append its text to the StringBuilder if selected
        if (pant.isSelected()) {
            selectedSpecializations.append("Pant, ");
        }
        if (shirt.isSelected()) {
            selectedSpecializations.append("Shirt, ");
        }
        if (blazzer.isSelected()) {
            selectedSpecializations.append("Blazzer, ");
        }
        if (msuit.isSelected()) {
            selectedSpecializations.append("Mens Suit, ");
        }
        if (lsuit.isSelected()) {
            selectedSpecializations.append("Ladies Suit, ");
        }
        if (blouse.isSelected()) {
            selectedSpecializations.append("Blouse, ");
        }
        // Remove the trailing ", " if there are selected specializations
        if (selectedSpecializations.length() > 0) {
            selectedSpecializations.setLength(selectedSpecializations.length() - 2);
        }

        // Update the label text to display selected specializations
        splz.setText(selectedSpecializations.toString());
    }

    @FXML
    void doClearAll(ActionEvent event)
    {
        mobile.clear(); // Clear text fields
        wname.clear();
        address.clear();
        email.clear();
        splz.clear();
        pant.setSelected(false);
        shirt.setSelected(false);
        blazzer.setSelected(false);
        msuit.setSelected(false);
        lsuit.setSelected(false);
        blouse.setSelected(false);
        doj.setValue(null);
        msg.setText("");
        salary.clear();
    }
    PreparedStatement stmt;
    @FXML
    void doSave(ActionEvent event)
    {
//        wname, mobile, address, email, splz, doj, salary
        try
        {
            stmt=con.prepareStatement("insert into workers values(?,?,?,?,?,?,?)");
            stmt.setString(1,wname.getText());
            stmt.setString(2,mobile.getText());
            stmt.setString(3,address.getText());
            stmt.setString(4,email.getText());
            stmt.setString(5,splz.getText());
            LocalDate local=doj.getValue();
            java.sql.Date date=java.sql.Date.valueOf(local);
            stmt.setDate(6, date);
            stmt.setInt(7,Integer.parseInt(salary.getText()));
            stmt.executeUpdate();
            msg.setText("Worker got added Successfully.");
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
    }

    @FXML
    void doSearch(ActionEvent event)
    {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM workers WHERE wname = ?");
            stmt.setString(1, wname.getText());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                wname.setText(rs.getString("wname"));
                mobile.setText(rs.getString("mobile"));
                address.setText(rs.getString("address"));
                email.setText(rs.getString("email"));
                Date dt = rs.getDate("doj");//col name
                doj.setValue(dt.toLocalDate());
                salary.setText(String.valueOf(rs.getInt("salary")));
                splz.setText(rs.getString("splz"));
                String s=rs.getString("splz");

                // Clear all checkboxes before setting them
                pant.setSelected(false);
                shirt.setSelected(false);
                blazzer.setSelected(false);
                msuit.setSelected(false);
                lsuit.setSelected(false);
                blouse.setSelected(false);

                if (s != null && !s.isEmpty()) {
                    String[] specialties = s.split(",");
                    for (String specialty : specialties) {
                        specialty = specialty.trim();
                        if (specialty.equals("Pant")) {
                            pant.setSelected(true);
                        } else if (specialty.equals("Shirt")) {
                            shirt.setSelected(true);
                        } else if (specialty.equals("Blazzer")) {
                            blazzer.setSelected(true);
                        } else if (specialty.equals("Mens Suit")) {
                            msuit.setSelected(true);
                        } else if (specialty.equals("Ladies Suit")) {
                            lsuit.setSelected(true);
                        } else if (specialty.equals("Blouse")) {
                            blouse.setSelected(true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void doUpdate(ActionEvent event)
    {
        try
        {
            stmt=con.prepareStatement("update workers set mobile=?, address=?, email=?,splz=?,doj=?,salary=? where wname=?");
            stmt.setString(1,mobile.getText());
            stmt.setString(2,address.getText());
            stmt.setString(3,email.getText());
            stmt.setString(4,splz.getText());
            LocalDate local=doj.getValue();
            java.sql.Date date=java.sql.Date.valueOf(local);
            stmt.setDate(5, date);
            stmt.setInt(6,Integer.parseInt(salary.getText()));
            stmt.setString(7,wname.getText());
            stmt.executeUpdate();
            msg.setText("Details updated Successfully.");
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
    }

    Connection con;
    @FXML
    void initialize()
    {
        con = SqlConnectionClass.doConnect();
        if (con == null)
            System.out.println("Connection did not Established");
        else
            System.out.println("Connection Established");
    }

}

