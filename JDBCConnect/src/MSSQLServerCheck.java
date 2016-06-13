/**
 * Created by gfp2ram on 7/2/2015.
 */

import sun.rmi.server.UnicastServerRef;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class MSSQLServerCheck {
    private static final String JDBC_URL = "jdbc:sqlserver://N01DSW077.tent.trt.csaa.pri:1433;DatabaseName=INS_COGEN";
    private static final String USERNAME = "BDW_CAR_MAIG";
    private static final String PASSWORD = "maiguser1";
    private static  Statement stmt;
    private static Connection conn=null;

    public static void main(String args[]) throws Exception {
        readTablesList();
    }

    public static void readTablesList() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection jdbcconnection = DriverManager.getConnection(JDBC_URL,USERNAME,PASSWORD);
            System.out.println("Connection Successful " + jdbcconnection);
            stmt = jdbcconnection.createStatement();
            ResultSet rs = stmt.executeQuery("select top 1 * from TA200");
            ResultSetMetaData rsmd =rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            System.out.println('\n');
            while(rs.next()) {
                String row = "";
                for (int i = 1; i <= columnCount; i++) {
                    String Columnname = rsmd.getColumnName(i);
                    String Columntype = rsmd.getColumnTypeName(i);
                    row += rs.getString(i) + ", ";
                    System.out.println(Columnname+' '+Columntype+'='+row);
                }
            }
            jdbcconnection.close();
            //Instantiate the BufferedReader Class
        } catch (Exception e) {
            System.out.println("Issues in Loading Tables:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
