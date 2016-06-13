/**
 * Created by gfp2ram on 6/9/2015.
 */
import sun.rmi.server.UnicastServerRef;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class OptimCheck {
    private static final String JDBC_URL = "jdbc:attconnect://192.168.27.50:2551;DefTdpName=REFERENCE;";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

    public static void main(String args[]) throws Exception {
        readTablesList();
    }

    public static void readTablesList() {
        try {
//            Class.forName("com.ibm.db2.jcc.DB2Driver");
            Class.forName("com.attunity.jdbc.NvDriver");
            Connection jdbcconnection = DriverManager.getConnection(JDBC_URL,USERNAME,PASSWORD);
            System.out.println("JDBC URL is " + JDBC_URL);
            System.out.println("After getting Connection " + jdbcconnection);
            //Instantiate the BufferedReader Class
        } catch (Exception e) {
            System.out.println("Issues in Loading Tables:" + e.getMessage());
            e.printStackTrace();
        }
    }
}