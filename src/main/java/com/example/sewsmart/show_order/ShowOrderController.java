package com.example.sewsmart.show_order;

import com.example.sewsmart.edit_order.EditOrderController;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.ResourceBundle;

import com.example.sewsmart.sqlconnection.SqlConnectionClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ShowOrderController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<OrderBean, Integer> billl;

    @FXML
    private TableColumn<OrderBean, Void> btnn;

    @FXML
    private Button deliver;

    @FXML
    private TableColumn<OrderBean, String> dodd;

    @FXML
    private TableColumn<OrderBean, String> dresss;

    @FXML
    private TextField mobile;

    @FXML
    private TableColumn<OrderBean, Integer> oidd;

    @FXML
    private Button payment;

    @FXML
    private TableColumn<OrderBean, Integer> qtyy;

    @FXML
    private TableColumn<OrderBean, String> statuss;

    @FXML
    private TableView<OrderBean> tblorder;

    @FXML
    private TextField totBill;

    @FXML
    private TableColumn<OrderBean, String> wrkrr;

    public void setMobile(String mob)
    {
        mobile.setText(mob);
        doShowAll();
    }

    @FXML
    void doDeliver(ActionEvent event)
    {
        try {
            LocalDate currentDate = LocalDate.now();
            java.sql.Date sqlCurrentDate = java.sql.Date.valueOf(currentDate);

            stmt = con.prepareStatement("UPDATE orders SET status = 'Delivered', dod = ? WHERE status = 'Completed'");
            stmt.setDate(1, sqlCurrentDate);
            int rowsUpdated = stmt.executeUpdate();

            // Update the OrderBean objects in the TableView
            if (rowsUpdated > 0)
            {
                for (OrderBean order : tblorder.getItems()) {
                    if ("Completed".equals(order.getStatus())) {
                        order.setStatus("Delivered");
                        order.setDod(currentDate.toString());
                    }
                }
                // Refresh the TableView
                tblorder.refresh();
                calculateTotalBill();
            }
        }
        catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @FXML
    void doEnableDeliver(ActionEvent event)
    {
        calculateTotalBill(); // Update the total bill if needed
        payment.setDisable(true);;
        deliver.setDisable(false);
    }

    private void updateStatusInDatabase() {
        try {
            String updateQuery = "UPDATE orders SET status = CASE WHEN CURDATE() >= dod THEN 'Completed' ELSE status END where status <> ?";
            stmt = con.prepareStatement(updateQuery);
            stmt.setString(1,"Delivered");
            stmt.executeUpdate();
        }
        catch (SQLException exp)
        {
            exp.printStackTrace();
        }
    }

    void doShowAll()
    {
        updateStatusInDatabase();
        tblorder.getItems().clear();
        btnn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<OrderBean, Void> call(final TableColumn<OrderBean, Void> param) {
                final TableCell<OrderBean, Void> cell = new TableCell<>() {
                    private final Button btnEdit = new Button("Edit");
                    {
                        btnEdit.setOnAction((ActionEvent event) -> {
                            OrderBean order = getTableView().getItems().get(getIndex());
                            openEditPage(order);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        }
                        else
                        {
                            HBox hbox = new HBox(btnEdit);
                            hbox.setAlignment(Pos.CENTER);
                            HBox.setHgrow(btnEdit, Priority.ALWAYS);
                            btnEdit.setMaxWidth(Double.MAX_VALUE);
                            setGraphic(hbox);
                            //setGraphic(btnEdit);
                        }
                    }
                };
                return cell;
            }
//        buttonColumn.setCellFactory(new Callback<>() {
//            @Override
//            public TableCell<OrderBean, Void> call(final TableColumn<OrderBean, Void> param) {
//                final TableCell<OrderBean, Void> cell = new TableCell<>()
//                {
////                    private final Button btn = new Button("Deliver");
//                    private final Button btnEdit = new Button("Edit");
//                    {
////                        btn.setOnAction((ActionEvent event) -> {
////                            OrderBean order = getTableView().getItems().get(getIndex());
////                            updateStatus(order);
////                        });
//
//                        btnEdit.setOnAction((ActionEvent event) -> {
//                            OrderBean order = getTableView().getItems().get(getIndex());
//                            openEditPage(order);
//                        });
//                    }
//
//                    @Override
//                    public void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setGraphic(null);
//                        }
//                        else
//                        {
////                            OrderBean order = getTableView().getItems().get(getIndex());
////                            if ("Delivered".equals(order.getStatus())) {
////                                btn.setDisable(true);
////                            }
////                            else
////                            {
////                                btn.setDisable(false);
////                            }
////                            HBox buttons = new HBox(10, btnEdit, btn);
////                            setGraphic(buttons);
//                            setGraphic(btnEdit);
//                        }
//                    }
//                };
//                return cell;
//            }
        });

        tblorder.setItems(getRecords());
        deliver.setDisable(true);
        calculateTotalBill();
    }

    private void calculateTotalBill() {
        int totalBill = 0;
        for (OrderBean order : tblorder.getItems()) {
            if ("Completed".equals(order.getStatus())) {
                totalBill += order.getBill();
            }
        }
        totBill.setText(String.valueOf(totalBill));
        if(totalBill>0)
        {
            payment.setDisable(false);
        }
    }

    private void openEditPage(OrderBean order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sewsmart/edit_orderr/EditOrderView.fxml"));
            Parent root = loader.load();

            EditOrderController editOrderController = loader.getController();
            editOrderController.setOrder(order.getOid());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }

    PreparedStatement stmt;
    ObservableList<OrderBean> getRecords()
    {
        ObservableList<OrderBean> ary= FXCollections.observableArrayList();

        try {
            stmt = con.prepareStatement("select orderID, dress, qty, bill, dod, wrkr, status from orders where mobile= ?");
            stmt.setString(1,mobile.getText());
            ResultSet records= stmt.executeQuery();
            while(records.next())
            {
                int oid=records.getInt("orderID");
                String dress=records.getString("dress");//col name
                int qty=records.getInt("qty");//col name
                int bill=records.getInt("bill");//col name
                Date dod=records.getDate("dod");//col name
                String wrkr=records.getString("wrkr");
                String status = records.getString("status");
                ary.add(new OrderBean(oid, dress, wrkr, qty, bill, dod.toString(), status));
            }
            // Define the custom order for status
            Comparator<OrderBean> statusComparator = (o1, o2) -> {
                String[] statusOrder = {"Completed", "In-Progress", "Delivered"};
                int index1 = java.util.Arrays.asList(statusOrder).indexOf(o1.getStatus());
                int index2 = java.util.Arrays.asList(statusOrder).indexOf(o2.getStatus());
                return Integer.compare(index1, index2);
            };

            // Sort the list using the comparator
            FXCollections.sort(ary, statusComparator);

        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
        return ary;
    }

//    private void updateStatus(OrderBean order) {
//        try {
//            LocalDate currentDate = LocalDate.now();
//            java.sql.Date sqlCurrentDate = java.sql.Date.valueOf(currentDate);
//
//            stmt = con.prepareStatement("UPDATE orders SET status = 'Delivered', dod = ? WHERE orderID = ?");
//            stmt.setDate(1, sqlCurrentDate);
//            stmt.setInt(2, order.getOid());
//            stmt.executeUpdate();
//
//            // Update the OrderBean object
//            order.setStatus("Delivered");
//            order.setDod(currentDate.toString());
//            tblorder.refresh();
//        } catch (Exception exp) {
//            exp.printStackTrace();
//        }
//    }

    @FXML
    void refresh(ActionEvent event)
    {
        doShowAll();
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

        oidd.setCellValueFactory(new PropertyValueFactory<>("oid"));
        dresss.setCellValueFactory(new PropertyValueFactory<>("dress"));
        wrkrr.setCellValueFactory(new PropertyValueFactory<>("wrkr"));
        qtyy.setCellValueFactory(new PropertyValueFactory<>("qty"));
        billl.setCellValueFactory(new PropertyValueFactory<>("bill"));
        dodd.setCellValueFactory(new PropertyValueFactory<>("dod"));
        statuss.setCellValueFactory(new PropertyValueFactory<>("status"));

//        setMobile("7986127036");
//        doShowAll();

    }

}
