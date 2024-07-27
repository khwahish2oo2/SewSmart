package com.example.sewsmart.add_order;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.example.sewsmart.send_mail.SendMail;
import com.example.sewsmart.sqlconnection.SqlConnectionClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class NewOrderController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField bill;

    @FXML
    private TextField cost;

    @FXML
    private ImageView designPrev;

    @FXML
    private DatePicker dod;

    @FXML
    private ComboBox<String> dress;

    @FXML
    private TextArea info;

    @FXML
    private TextArea measurements;

    @FXML
    private TextField mobile;

    @FXML
    private Label msg;

    @FXML
    private ComboBox<String> qty;

    @FXML
    private ComboBox<String> wrkr;


    public void setMobile(String mob)
    {
        mobile.setText(mob);
    }


    @FXML
    void doClearAll(ActionEvent event)
    {
        mobile.clear(); // Clear text fields
        dress.getSelectionModel().select(0);
        qty.getSelectionModel().clearSelection();
        dod.setValue(null);
        cost.clear();
        bill.clear();
        measurements.clear();
        info.clear();
        try {
            designPrev.setImage(new Image(new FileInputStream(filePath)));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void dofillworkercombo(ActionEvent event)
    {
        wrkr.getItems().clear();
        wrkr.getItems().addAll(Arrays.asList(w));
        wrkr.getSelectionModel().select(0);
        String selectedDress = dress.getSelectionModel().getSelectedItem();
        if(selectedDress!=null)
        {
            String query = "SELECT wname FROM workers WHERE splz LIKE ?";
            try(PreparedStatement stmt=con.prepareStatement(query))
            {
                stmt.setString(1, "%"+selectedDress+"%");
                try (ResultSet rs= stmt.executeQuery())
                {
                    while(rs.next())
                    {
                        wrkr.getItems().add(rs.getString("wname"));
                    }
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        String query = "SELECT measurements FROM orders WHERE mobile = ? AND dress = ? ORDER BY od DESC LIMIT 1";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, mobile.getText());
            stmt.setString(2, dress.getSelectionModel().getSelectedItem());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                String latestMeasurement = rs.getString("measurements");
                measurements.setText(latestMeasurement);
            }
            else
            {
                String query1 = "SELECT measurements FROM measure WHERE dress = ?";
                try (PreparedStatement stmtt = con.prepareStatement(query1)) {
                    stmtt.setString(1, dress.getSelectionModel().getSelectedItem());
                    ResultSet rss = stmtt.executeQuery();
                    if (rss.next())
                    {
                        String latestMeasurement = rss.getString("measurements");
                        measurements.setText(latestMeasurement);
                    }
                    else
                    {
                        measurements.setText("");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void doCalcBill(MouseEvent event)
    {
        if(event.getClickCount()==1)
        {
            int B=0;
            int Q=0,C=0;
            try {
                Q = Integer.parseInt(qty.getSelectionModel().getSelectedItem());
                C = Integer.parseInt(cost.getText());
            }
            catch(Exception exp)
            {
                System.out.print("First enter Quantity and Cost");
            }
            B=Q*C;
            bill.setText(String.valueOf(B));
        }
    }

    PreparedStatement stmt;
    @FXML
    void doSave(ActionEvent event)
    {
//        order_id, mobile, dress, qty, cost, bill, dod, wrkr, design, measurements, info
        try
        {
            stmt=con.prepareStatement("insert into orders (mobile, dress, qty, cost, bill, od, dod, wrkr, design, measurements, info, status) values(?,?,?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1,mobile.getText());
            stmt.setString(2,(dress.getSelectionModel().getSelectedItem()));
            stmt.setInt(3,Integer.parseInt(qty.getSelectionModel().getSelectedItem()));
            stmt.setInt(4,Integer.parseInt(cost.getText()));
            stmt.setInt(5,Integer.parseInt(bill.getText()));
            LocalDate local=dod.getValue();
            java.sql.Date date=java.sql.Date.valueOf(local);
            stmt.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setDate(7, date);
            stmt.setString(8,(wrkr.getSelectionModel().getSelectedItem()));
            stmt.setString(9,filePath);
            stmt.setString(10,measurements.getText());
            stmt.setString(11,info.getText());
            stmt.setString(12,"In-Progress");
            int affectedRows=stmt.executeUpdate();
            if(affectedRows>0)
            {
                try(ResultSet generatedKeys=stmt.getGeneratedKeys())
                {
                    if(generatedKeys.next())
                    {
                        int orderID=generatedKeys.getInt(1);
                        msg.setText("Order saved with Order ID: "+orderID);

                        // Fetch the email of the assigned worker
                        String workerEmail = getWorkerEmail(wrkr.getSelectionModel().getSelectedItem());

                        // Send an email to the worker with order details
                        String emailBody = "New order details:\n\n" +
                                "Order ID:  " + orderID + "\n" +
                                "Dress:  " + dress.getSelectionModel().getSelectedItem() + "\n" +
                                "Quantity:  " + qty.getSelectionModel().getSelectedItem() + "\n\n" +
                                "Delivery Date:  " + date + "\n\n" +
                                "Measurements: \n" + measurements.getText() + "\n" +
                                "Additional Info:  " + info.getText();

                        SendMail.SendMail(workerEmail, "New Order Assigned", emailBody);

                    }
                }
            }
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
    }

    private String getWorkerEmail(String workerName) {
        String email = "";
        String query = "SELECT email FROM workers WHERE wname = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, workerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("email");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }

    String filePath="C:Users.DELL.IdeaProjects.sewsmart.src.main.resources.com.example.sewsmart.add_orderr.upload3.jpg";
    @FXML
    void doUploadPic(ActionEvent event)
    {
        FileChooser chooser=new FileChooser();
        chooser.setTitle("Select Profile Pic:");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("*.*", "*.*")
        );
        File file=chooser.showOpenDialog(null);
        filePath=file.getAbsolutePath();
        try {
            designPrev.setImage(new Image(new FileInputStream(file)));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    String w[]={"Select"};
    String d[]={"Select", "Pant", "Shirt", "Blazzer", "Mens Suit", "Ladies Suit", "Blouse"};
    String q[]={"1", "2", "3", "4", "5"};
    Connection con;
    @FXML
    void initialize()
    {
        con= SqlConnectionClass.doConnect();
        if(con==null)
            System.out.println("Connection did not Established");
        else
            System.out.println("Connection Established");


        dress.getItems().addAll(Arrays.asList(d));
        dress.getSelectionModel().select(0);
        wrkr.getItems().addAll(Arrays.asList(w));
        wrkr.getSelectionModel().select(0);
        qty.getItems().addAll(Arrays.asList(q));


        assert bill != null : "fx:id=\"bill\" was not injected: check your FXML file 'NewOrderView.fxml'.";
        assert cost != null : "fx:id=\"cost\" was not injected: check your FXML file 'NewOrderView.fxml'.";
        assert designPrev != null : "fx:id=\"designPrev\" was not injected: check your FXML file 'NewOrderView.fxml'.";
        assert dod != null : "fx:id=\"dod\" was not injected: check your FXML file 'NewOrderView.fxml'.";
        assert dress != null : "fx:id=\"dress\" was not injected: check your FXML file 'NewOrderView.fxml'.";
        assert info != null : "fx:id=\"info\" was not injected: check your FXML file 'NewOrderView.fxml'.";
        assert measurements != null : "fx:id=\"measurements\" was not injected: check your FXML file 'NewOrderView.fxml'.";
        assert mobile != null : "fx:id=\"mobile\" was not injected: check your FXML file 'NewOrderView.fxml'.";
        assert msg != null : "fx:id=\"msg\" was not injected: check your FXML file 'NewOrderView.fxml'.";
        assert qty != null : "fx:id=\"qty\" was not injected: check your FXML file 'NewOrderView.fxml'.";
        assert wrkr != null : "fx:id=\"wrkr\" was not injected: check your FXML file 'NewOrderView.fxml'.";

    }

}
