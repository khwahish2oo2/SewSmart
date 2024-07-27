package com.example.sewsmart.edit_order;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.*;
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

public class EditOrderController {

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
    private Label msg;

    @FXML
    private TextField oid;

    @FXML
    private ComboBox<String> qty;

    @FXML
    private ComboBox<String> wrkr;

    void dofillworkercombo() {
        wrkr.getItems().clear();
        String selectedDress = dress.getSelectionModel().getSelectedItem();
        if (selectedDress != null) {
            String query = "SELECT wname FROM workers WHERE splz LIKE ?";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, "%" + selectedDress + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        wrkr.getItems().add(rs.getString("wname"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    String filePath="C:Users.DELL.IdeaProjects.sewsmart.src.main.resources.com.example.sewsmart.add_orderr.upload3.jpg";
    public void setOrder(int order)
    {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM orders WHERE orderID = ?");
            stmt.setInt(1, order);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                oid.setText(String.valueOf(rs.getInt("orderID")));
                String dresss=rs.getString("dress");
                String qtyy=String.valueOf(rs.getInt("qty"));
                dress.getSelectionModel().select(dresss);
                dofillworkercombo();
                qty.getSelectionModel().select(qtyy);
                Date dt = rs.getDate("dod");//col name
                dod.setValue(dt.toLocalDate());
                cost.setText(String.valueOf(rs.getInt("cost")));
                bill.setText(String.valueOf(rs.getInt("bill")));
                String wrkrr=rs.getString("wrkr");
                wrkr.getSelectionModel().select(wrkrr);
                measurements.setText(rs.getString("measurements"));
                info.setText(rs.getString("info"));
                String path = rs.getString("design");//col name
                if(!path.equals("C:Users.DELL.IdeaProjects.sewsmart.src.main.resources.com.example.sewsmart.add_orderr.upload3.jpg")) {
                    filePath = path;
                    designPrev.setImage(new Image(new FileInputStream(filePath)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    PreparedStatement stmt;
    @FXML
    void doUpdate(ActionEvent event) {
//        order_id, mobile, dress, qty, cost, bill, dod, wrkr, design, measurements, info
        try
        {
            stmt=con.prepareStatement("update orders set dress=?, qty=?, cost=?, bill=?, dod=?, wrkr=?, design=?, measurements=?, info=?, status=? where orderID=?");
            stmt.setString(1,(dress.getSelectionModel().getSelectedItem()));
            stmt.setInt(2,Integer.parseInt(qty.getSelectionModel().getSelectedItem()));
            stmt.setInt(3,Integer.parseInt(cost.getText()));
            stmt.setInt(4,Integer.parseInt(bill.getText()));
            LocalDate local=dod.getValue();
            java.sql.Date date=java.sql.Date.valueOf(local);
            stmt.setDate(5, date);
            stmt.setString(6,(wrkr.getSelectionModel().getSelectedItem()));
            stmt.setString(7,filePath);
            stmt.setString(8,measurements.getText());
            stmt.setString(9,info.getText());
            stmt.setString(10,"In-Progress");
            stmt.setInt(11,Integer.parseInt(oid.getText()));
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                msg.setText("Order " + oid.getText() + " updated successfully");
                String workerEmail = getWorkerEmail(wrkr.getSelectionModel().getSelectedItem());

                // Send an email to the worker with order details
                String emailBody = "New order details:\n\n" +
                        "Order ID:  " + oid.getText() + "\n" +
                        "Dress:  " + dress.getSelectionModel().getSelectedItem() + "\n" +
                        "Quantity:  " + qty.getSelectionModel().getSelectedItem() + "\n\n" +
                        "Delivery Date:  " + date + "\n\n" +
                        "Measurements: \n" + measurements.getText() + "\n" +
                        "Additional Info:  " + info.getText();

                SendMail.SendMail(workerEmail, "Order " +oid.getText()+ "Updated", emailBody);
            } else {
                msg.setText("Order " + oid.getText() + " not found or not updated");
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

    @FXML
    void doDelete(ActionEvent event)
    {
        try {
            stmt=con.prepareStatement("delete from orders where orderID=?");
            stmt.setInt(1,Integer.parseInt(oid.getText()));
            int count=stmt.executeUpdate();
            if(count==1)
            {
                msg.setText("Record Deleted Successsfully");
                String workerEmail = getWorkerEmail(wrkr.getSelectionModel().getSelectedItem());

                // Send an email to the worker with order details
                String emailBody = "Order with order ID: " + oid.getText() + " is Cancelled";

                SendMail.SendMail(workerEmail, "Order " +oid.getText()+ "Cancelled", emailBody);
            }
            else
                msg.setText("Order with orderID "+oid.getText()+" doesn't exist");
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
    }



    @FXML
    void dofillworkercombo(ActionEvent event)
    {
        wrkr.getItems().clear();
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
    }

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
    String d[]={"Pant", "Shirt", "Blazzer", "Mens Suit", "Ladies Suit", "Blouse"};
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
        qty.getItems().addAll(Arrays.asList(q));
    }

}
