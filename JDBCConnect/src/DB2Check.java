import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by gfp2ram on 7/16/2015.
 */
public class DB2Check {
    private static final String JDBC_URL = "jdbc:db2://205.223.49.5:1531/A01DB2NT";
    private static final String USERNAME = "svchdp1";
    private static final String PASSWORD = "v3(dk%Fw&tFk";

    public static void main(String args[]) throws Exception {
        readTablesList();
    }

    public static void readTablesList() {
        try {
//            Class.forName("com.ibm.db2.jcc.DB2Driver");
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            Connection jdbcconnection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("JDBC URL is " + JDBC_URL);
            System.out.println("After getting Connection " + jdbcconnection);
            //Instantiate the BufferedReader Class
        } catch (Exception e) {
            System.out.println("Issues in Loading Tables:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
