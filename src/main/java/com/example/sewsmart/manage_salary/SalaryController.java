package com.example.sewsmart.manage_salary;


import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.Writer;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ResourceBundle;

import java.time.temporal.ChronoUnit;
import com.example.sewsmart.sqlconnection.SqlConnectionClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class SalaryController
{
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label msg;

    @FXML
    private TableView<ManageSalaryBean> salaryTable;

    @FXML
    private TableColumn<ManageSalaryBean, Integer> absent;

    @FXML
    private TableColumn<ManageSalaryBean, String> date;

    @FXML
    private TableColumn<ManageSalaryBean, String> name;

    @FXML
    private TableColumn<ManageSalaryBean, Integer> netPay;

    @FXML
    private TableColumn<ManageSalaryBean, Integer> present;

    @FXML
    private TableColumn<ManageSalaryBean, Integer> salary;

    @FXML
    private Button payment;

    @FXML
    void doClearDB(ActionEvent event)
    {
        try
        {
            PreparedStatement stmt = con.prepareStatement("truncate table worker_attendance");
            stmt.executeUpdate();
            salaryTable.refresh();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    ObservableList<ManageSalaryBean> workerSalaries = FXCollections.observableArrayList();
    void doShow()
    {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstDay = currentMonth.atDay(1);
        LocalDate lastDay = LocalDate.now();

        try
        {
            PreparedStatement stmt = con.prepareStatement("SELECT wname, doj, salary FROM workers order by salary DESC");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String workerName = rs.getString("wname");
                LocalDate joiningDate = rs.getDate("doj").toLocalDate();
                int monthlySalary = rs.getInt("salary");

                LocalDate effectiveFirstDay = firstDay;

                // Adjust the first day of work if the current month is the month of joining
                if (joiningDate.getMonth() == firstDay.getMonth() && joiningDate.getYear() == firstDay.getYear()) {
                    effectiveFirstDay = joiningDate;
                }

                PreparedStatement stmtAttendance = con.prepareStatement(
                        "SELECT COUNT(*) AS days_absent FROM worker_attendance WHERE wname = ? AND date BETWEEN ? AND ?"
                );
                stmtAttendance.setString(1, workerName);
                stmtAttendance.setDate(2, java.sql.Date.valueOf(effectiveFirstDay));
                stmtAttendance.setDate(3, java.sql.Date.valueOf(lastDay));
                ResultSet rsAttendance = stmtAttendance.executeQuery();

                int daysAbsent = 0;
                if (rsAttendance.next()) {
                    daysAbsent = rsAttendance.getInt("days_absent");
                }

                int totalDays = (int) ChronoUnit.DAYS.between(effectiveFirstDay, lastDay.plusDays(1));
                int daysPresent = totalDays - daysAbsent;
                int dailyWage = monthlySalary / currentMonth.lengthOfMonth();
                int netSalary = dailyWage * daysPresent;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // specify your desired pattern
                String joiningDateString = joiningDate.format(formatter);
                workerSalaries.add(new ManageSalaryBean(workerName, joiningDateString, daysPresent, daysAbsent, monthlySalary, netSalary));
            }
            salaryTable.setItems(workerSalaries);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @FXML
    void e2e(ActionEvent event)
    {
        try {
            writeExcel();
            msg.setText("Successfully Exported");

//            LocalDate today = LocalDate.now();
//            LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
//
//            if (today.equals(lastDayOfMonth)) {
//                payment.setDisable(false);
//            } else {
//                payment.setDisable(true);
//            }
            payment.setDisable(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void writeExcel() throws Exception {
        Writer writer = null;
        try
        {
            YearMonth currentMonth = YearMonth.now();
            File file = new File("Salary"+currentMonth+".csv");
            writer = new BufferedWriter(new FileWriter(file));
            String text="Worker Name,Joining Date,Present,Absent,Monthly Salary, Net Salary\n";
            writer.write(text);
            for (ManageSalaryBean p : workerSalaries)
            {
                text = p.getName()+ "," + p.getDoj()+ "," + p.getPresent()+ "," + p.getAbsent()+ "," + p.getMonthlySalary()+ "," + p.getNetSalary()+"\n";
                writer.write(text);
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            writer.flush();
            writer.close();
        }
    }


    Connection con;
    @FXML
    void initialize() {
        con = SqlConnectionClass.doConnect();
        if (con == null) {
            System.out.println("Connection did not establish");
        } else {
            System.out.println("Connection established");

            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            date.setCellValueFactory(new PropertyValueFactory<>("doj"));
            present.setCellValueFactory(new PropertyValueFactory<>("present"));
            absent.setCellValueFactory(new PropertyValueFactory<>("absent"));
            salary.setCellValueFactory(new PropertyValueFactory<>("monthlySalary"));
            netPay.setCellValueFactory(new PropertyValueFactory<>("netSalary"));
            doShow();
        }
    }


}
