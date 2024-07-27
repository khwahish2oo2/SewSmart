package com.example.sewsmart.login;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.example.sewsmart.edit_order.EditOrderController;
import com.example.sewsmart.sqlconnection.SqlConnectionClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField mobile;

    @FXML
    private PasswordField pwd;

    @FXML
    private Label msg;

    @FXML
    void doLogin(ActionEvent event)
    {
        try
        {
            String mobileNumber = mobile.getText();
            String password = pwd.getText();

            if (mobileNumber.isEmpty() || password.isEmpty()) {
                msg.setText("*Enter mobile and password");
                return;
            }

            PreparedStatement stmt = con.prepareStatement("SELECT pwd FROM login WHERE mobile = ?");
            stmt.setString(1, mobileNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("pwd");
                if (storedPassword.equals(password))
                {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sewsmart/homee/HomeView.fxml"));
                        Parent root = loader.load();
                        Stage stage = new Stage();
                        stage.setTitle("SewSmart: Crafting Efficiency Daily");
                        stage.setScene(new Scene(root));
                        stage.show();
                        mobile.clear();
                        pwd.clear();
                        msg.setText("Login Successful");
                    }
                    catch (Exception exp)
                    {
                        exp.printStackTrace();
                    }
                }
                else
                {
                    msg.setText("*Incorrect password");
                }
            } else {
                msg.setText("*Mobile number is not registered");
            }
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
        con = SqlConnectionClass.doConnect();
        if (con == null)
            System.out.println("Connection did not Established");
        else
        {
            System.out.println("Connection Established");
        }
    }

}

