package org.apache.hive.jdbc;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class HiveJdbcClient {
//Define the Driver 
    private static final String driverName = "org.apache.hive.jdbc.HiveDriver";

    public List<CustomerDetails> getCustomerDetailsList() {
        List<CustomerDetails> customerDetailses = null;
        try {
            Class.forName(driverName);

            //Connection con = DriverManager.getConnection("jdbc:hive2://n01bdl303.aap.csaa.pri:10000/ubiqa", "ggx4ram", "Hadoop123");
            Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/xademo", "root", "hadoop");

            //customerDetails is a reference. Don't create the object here.
            CustomerDetails customerDetails = null;

            //Put the object customerDetails in LinkedList
            System.out.println("After Connection" + con);
            Statement stmt = con.createStatement();
            //String tableName = "pas_ubi_veh_history";
            String tableName = "customer_details";
            //stmt.executeQuery("drop table if exists " + tableName);
            //ResultSet res = stmt.executeQuery("select * from " + " ubiqa."+tableName+" limit 20");
            ResultSet res = stmt.executeQuery("select * from xademo." + tableName + " limit 20");
            //ResultSet res = stmt.executeQuery("create table" + " ubiqa."+tableName+ " (key int, value string)");

            customerDetailses = new ArrayList<>();
            while (res.next()) {
                //Create a Object for CustomerDetails
                customerDetails = new CustomerDetails();
                customerDetails.setPhNum(res.getString(1));
                customerDetails.setPlan(res.getString(2));
                customerDetails.setDate(res.getString(3));
                customerDetails.setStatus(res.getString(4));
                customerDetails.setBalance(res.getString(5));
                customerDetails.setImei(res.getString(6));
                customerDetails.setRegion(res.getString(7));
                //System.out.println(CustomerDetails.phNum);
                customerDetailses.add(customerDetails);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return customerDetailses;
    }
}
