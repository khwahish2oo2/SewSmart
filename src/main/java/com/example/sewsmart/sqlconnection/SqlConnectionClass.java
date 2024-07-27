package com.example.sewsmart.sqlconnection;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqlConnectionClass
{
    public static Connection doConnect()
    {
        Connection con=null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/sewsmart", "root", "Khwahish21");
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
        return con;
    }
    public static void main(String ary[])
    {
        if(doConnect()==null)
            System.out.println("Something went wrong");
        else
            System.out.println("Application successfully connected to Database");

    }
}

