package com.example.sewsmart.jsg;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;

import com.example.sewsmart.edit_order.EditOrderController;
import com.example.sewsmart.manage_salary.ManageSalaryBean;
import com.example.sewsmart.show_order.OrderBean;
import com.example.sewsmart.sqlconnection.SqlConnectionClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;

public class JsgController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<OrderBean> tblorder;

    @FXML
    private TableColumn<ManageSalaryBean, String> status;

    @FXML
    private TableColumn<ManageSalaryBean, Integer> workLoad;

    @FXML
    private TableColumn<ManageSalaryBean, String> workerName;

    @FXML
    private TableView<ManageSalaryBean> workload;

    @FXML
    private TableColumn<OrderBean, Void> btnn;

    @FXML
    private TableColumn<OrderBean, String> dodd;

    @FXML
    private TableColumn<OrderBean, String> dresss;

    @FXML
    private TableColumn<OrderBean, Integer> oidd;

    @FXML
    private TableColumn<OrderBean, String> statuss;

    @FXML
    private TableColumn<OrderBean, String> wrkrr;



    ObservableList<ManageSalaryBean> workerSalaries = FXCollections.observableArrayList();
    void doShow() {
        try {
            PreparedStatement workerStmt = con.prepareStatement("SELECT wname FROM workers");
            ResultSet workerRs = workerStmt.executeQuery();
            while(workerRs.next())
            {
                String workerName = workerRs.getString("wname");
                PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) as workload FROM orders WHERE wrkr=? and status = ? ");
                stmt.setString(1, workerName);
                stmt.setString(2, "In-Progress");
                ResultSet rs = stmt.executeQuery();

                LocalDate today = LocalDate.now();

                int workload=0;
                while (rs.next()) {
                    workload = rs.getInt("workload");

                    // Check attendance status for the current date
                    PreparedStatement attendanceStmt = con.prepareStatement("SELECT COUNT(*) as count FROM worker_attendance WHERE wname = ? AND date = ?");
                    attendanceStmt.setString(1, workerName);
                    attendanceStmt.setDate(2, java.sql.Date.valueOf(today));
                    ResultSet attendanceRs = attendanceStmt.executeQuery();

                    String status = "Present"; // Default status
                    if (attendanceRs.next() && attendanceRs.getInt("count") > 0) {
                        status = "Absent";
                    }
                    workerSalaries.add(new ManageSalaryBean(workerName, workload, status));
                }
            }
            Collections.sort(workerSalaries, new Comparator<ManageSalaryBean>() {
                @Override
                public int compare(ManageSalaryBean o1, ManageSalaryBean o2) {
                    return Integer.compare(o2.getWorkload(), o1.getWorkload());
                }
            });
            workload.setItems(workerSalaries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ObservableList<OrderBean> getRecords()
    {
        ObservableList<OrderBean> ary= FXCollections.observableArrayList();

        try {
            stmt = con.prepareStatement("select orderID, dress, dod, wrkr, status from orders where dod>=CURDATE() and status <> ? order by dod");
            stmt.setString(1,"Delivered");
            ResultSet records= stmt.executeQuery();
            while(records.next())
            {
                int oid=records.getInt("orderID");
                String dress=records.getString("dress");//col name
                Date dod=records.getDate("dod");//col name
                String wrkr=records.getString("wrkr");
                String status = records.getString("status");
                ary.add(new OrderBean(oid, dress, wrkr, dod.toString(), status));
            }

            Comparator<OrderBean> statusComparator = (o1, o2) -> {
                String[] statusOrder = {"Completed", "In-Progress"};
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

    void doShowAll() {
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
        });

        tblorder.setItems(getRecords());
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

        workerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        workLoad.setCellValueFactory(new PropertyValueFactory<>("workload"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        oidd.setCellValueFactory(new PropertyValueFactory<>("oid"));
        dresss.setCellValueFactory(new PropertyValueFactory<>("dress"));
        wrkrr.setCellValueFactory(new PropertyValueFactory<>("wrkr"));
        dodd.setCellValueFactory(new PropertyValueFactory<>("dod"));
        statuss.setCellValueFactory(new PropertyValueFactory<>("status"));

        doShowAll();
        doShow();
    }

}

