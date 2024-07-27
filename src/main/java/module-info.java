module com.example.sewsmart {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.mail;
    requires activation;
    requires java.desktop;

    opens com.example.sewsmart to javafx.fxml;
    exports com.example.sewsmart;

    exports com.example.sewsmart.enroll_customer;
    opens com.example.sewsmart.enroll_customer to javafx.fxml;

    exports com.example.sewsmart.add_worker;
    opens com.example.sewsmart.add_worker to javafx.fxml;

    exports com.example.sewsmart.add_order;
    opens com.example.sewsmart.add_order to javafx.fxml;

    exports com.example.sewsmart.sqlconnection;
    opens com.example.sewsmart.sqlconnection to javafx.fxml;

    exports com.example.sewsmart.show_order;
    opens com.example.sewsmart.show_order to javafx.fxml;

    exports com.example.sewsmart.edit_order;
    opens com.example.sewsmart.edit_order to javafx.fxml;

    exports com.example.sewsmart.send_mail;
    opens com.example.sewsmart.send_mail to javafx.fxml;

    exports com.example.sewsmart.attendence;
    opens com.example.sewsmart.attendence to javafx.fxml;

    exports com.example.sewsmart.manage_salary;
    opens com.example.sewsmart.manage_salary to javafx.fxml;

    exports com.example.sewsmart.home;
    opens com.example.sewsmart.home to javafx.fxml;

    exports com.example.sewsmart.find_customer;
    opens com.example.sewsmart.find_customer to javafx.fxml;

    exports com.example.sewsmart.jsg;
    opens com.example.sewsmart.jsg to javafx.fxml;

    exports com.example.sewsmart.edit_customer;
    opens com.example.sewsmart.edit_customer to javafx.fxml;

    exports com.example.sewsmart.sales_analysis;
    opens com.example.sewsmart.sales_analysis to javafx.fxml;

    exports com.example.sewsmart.login;
    opens com.example.sewsmart.login to javafx.fxml;
}