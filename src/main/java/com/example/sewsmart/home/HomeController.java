package com.example.sewsmart.home;

import com.example.sewsmart.HelloApplication;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import com.example.sewsmart.sqlconnection.SqlConnectionClass;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane contentArea;

    private void loadPage(String page)
    {
        try
        {
            Stage stage=new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(page));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Hello!");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void replacePage(String page)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
            AnchorPane newContent = loader.load();
            System.out.println("FXML file loaded successfully.");
            contentArea.getChildren().setAll(newContent);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void FindCustomer(ActionEvent event) {
        replacePage("/com/example/sewsmart/find_customerr/FindCustomerView.fxml");
    }


    @FXML
    void addCustomer(ActionEvent event)
    {
        replacePage("/com/example/sewsmart/enroll_customerr/EnrollCustomerView.fxml");
    }

    @FXML
    void addLeave(ActionEvent event)
    {
        replacePage("/com/example/sewsmart/attendencee/AttendenceView.fxml");
    }

    @FXML
    void addWorker(ActionEvent event)
    {
        replacePage("/com/example/sewsmart/add_workerr/AddWorkerView.fxml");
    }


    @FXML
    void analysis(ActionEvent event)
    {
        replacePage("/com/example/sewsmart/sales_analysiss/SalesAnalysisView.fxml");
    }

    @FXML
    void signOut(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/sewsmart/loginn/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Welcome to SewSmart");
            stage.setScene(new Scene(root));
            stage.show();
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void viewSalary(ActionEvent event)
    {
        replacePage("/com/example/sewsmart/manage_salaryy/ManageSalaryView.fxml");
    }

    @FXML
    void openJsg(MouseEvent event) 
    {
        if(event.getClickCount()==1) {
            replacePage("/com/example/sewsmart/jsgg/JsgView.fxml");
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
            replacePage("/com/example/sewsmart/jsgg/JsgView.fxml");
        }



    }

}
