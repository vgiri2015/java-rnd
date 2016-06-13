import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by gfp2ram on 6/12/2015.
 */
    public class SQOOP_SIS_initial_PK {
    private static final String JDBC_URL="jdbc:as400://172.29.137.28:8476/sispmodd";
    //private static final String JDBC_URL = "jdbc:as400://192.168.104.130:8476/sisarcmodd";
    private static final String USERNAME = "SVCHDOOP";
    private static final String PASSWORD = "doop4info";
    private static final String input_tableslist = "C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\\\dev\\\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\SIS\\SISpmoddTableslist.txt";
    private static String tablename;

    public static void main(String args[]) throws Exception {
        readTablesList();
    }

    public static void readTablesList() {
        try {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            Connection jdbcconnection = DriverManager.getConnection(JDBC_URL, USERNAME,PASSWORD);
            FileReader tablesList = new FileReader(input_tableslist);
            BufferedReader read_tableslist = new BufferedReader(tablesList);
            String tableName1;
            while ((tableName1 = read_tableslist.readLine()) != null) {
                Statement statement = jdbcconnection.createStatement();
                String sqlStmt;
                sqlStmt = String.format("SELECT * FROM %s", tableName1);
                try {
                    ResultSet execQuery = statement.executeQuery(sqlStmt);
                    DatabaseMetaData tableMetadata = jdbcconnection.getMetaData();
                    ResultSet rs = tableMetadata.getPrimaryKeys(null, null, tableName1);

                    while (rs.next()) {
                        String columnName = rs.getString("COLUMN_NAME");
                        System.out.println("getPrimaryKeys(): columnName=" + columnName);
                    }
                    statement.close();
                    jdbcconnection.close();
                }
                catch (SQLNonTransientConnectionException e)
                {
                    System.out.println("Issues with the Table" + e.getMessage());
                    e.printStackTrace();
                }
            }
            //Close the buffer reader
            read_tableslist.close();
        }
        catch (Exception e)
        {
            System.out.println("Error while reading file line by line:" + e.getMessage());
            e.printStackTrace();
        }
    }
}