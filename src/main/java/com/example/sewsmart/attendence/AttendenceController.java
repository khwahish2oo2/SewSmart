package com.example.sewsmart.attendence;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import com.example.sewsmart.edit_order.EditOrderController;
import com.example.sewsmart.send_mail.SendMail;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AttendenceController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private DatePicker doa;

    @FXML
    private Button exit;

    @FXML
    private Label msg;

    @FXML
    private ComboBox<String> workerCombo;

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
    private TableView<OrderBean> tblorder;


    ObservableList<OrderBean> getRecords()
    {
        ObservableList<OrderBean> ary= FXCollections.observableArrayList();
        String workerName = workerCombo.getSelectionModel().getSelectedItem();
        System.out.println("Selected Worker: " + workerName);
        try {
            stmt = con.prepareStatement("select orderID, dress, dod, status from orders where status = ? and wrkr = ? order by dod");
            stmt.setString(1,"In-progress");
            stmt.setString(2,workerName);
            ResultSet records= stmt.executeQuery();
            System.out.println("Query executed successfully");
            while(records.next())
            {
                int oid=records.getInt("orderID");
                String dress=records.getString("dress");//col name
                Date dod=records.getDate("dod");//col name
                String status = records.getString("status");
                ary.add(new OrderBean(oid, dress, dod.toString(), status));
            }
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

    @FXML
    void doShowAll(ActionEvent event)
    {
        updateDatePickerCellFactory();
        System.out.println("Hello");
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
                        }
                    }
                };
                return cell;
            }
        });
        ObservableList<OrderBean> records = getRecords();
        tblorder.setItems(records);

        // Check if the table is empty
        if (records.isEmpty()) {
            exit.setDisable(false); // Assuming exitButton is the ID of your exit button
        }
    }


    @FXML
    void terminate(ActionEvent event)
    {
        String workerName = workerCombo.getSelectionModel().getSelectedItem();
        try {
            stmt=con.prepareStatement("delete from workers where wname=?");
            stmt.setString(1,workerName);
            int count=stmt.executeUpdate();
            if(count==1)
            {
                msg.setText("Worker has been Terminated");
            }
            else
                msg.setText("");
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
    }


    @FXML
    void markAbsence(ActionEvent event)
    {
        String workerName = workerCombo.getSelectionModel().getSelectedItem();
        LocalDate date = doa.getValue();

        if (workerName != null && date != null) {
            try {
                PreparedStatement stmt = con.prepareStatement("INSERT INTO worker_attendance (wname, date) VALUES (?, ?)");
                stmt.setString(1, workerName);
                stmt.setDate(2, java.sql.Date.valueOf(date));
                stmt.executeUpdate();
                msg.setText(workerName + " has been marked absent for " + date);
                // Refresh the date picker to show the updated absent dates
                updateDatePickerCellFactory();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            msg.setText("Please select a worker and date");
        }
    }

    private Set<LocalDate> getAbsentDates(String workerName) {
        Set<LocalDate> absentDates = new HashSet<>();
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT date FROM worker_attendance WHERE wname = ?");
            stmt.setString(1, workerName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                absentDates.add(rs.getDate("date").toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return absentDates;
    }


    private void updateDatePickerCellFactory() {
        String selectedWorker = workerCombo.getSelectionModel().getSelectedItem();
        if (selectedWorker != null) {
            Set<LocalDate> absentDates = getAbsentDates(selectedWorker);
            doa.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (absentDates.contains(date)) {
                        setStyle("-fx-background-color: #FF9999;"); // Light red background for absent dates
                    }
                }
            });
        }
    }


    Connection con;
    @FXML
    void initialize()
    {
        con = SqlConnectionClass.doConnect();
        if (con == null) {
            System.out.println("Connection did not establish");
        }
        else
        {
            System.out.println("Connection established");

            try {
                PreparedStatement stmt = con.prepareStatement("SELECT wname FROM workers");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    workerCombo.getItems().add(rs.getString("wname"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            oidd.setCellValueFactory(new PropertyValueFactory<>("oid"));
            dresss.setCellValueFactory(new PropertyValueFactory<>("dress"));
            dodd.setCellValueFactory(new PropertyValueFactory<>("dod"));
            statuss.setCellValueFactory(new PropertyValueFactory<>("status"));
        }

    }

}
