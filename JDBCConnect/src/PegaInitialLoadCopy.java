package pega.initialload;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * JDBC connector for Oracle databases
 *
 * Notes:
 * Please add the 'ojdbc6.jar' to your path before running this Java program
 *
 */

public class PegaInitialLoadCopy {

    private static final String[] tablecreation = { "CREATE EXTERNAL TABLE ",
            " PARTITIONED BY (LOAD_YEAR INT,LOAD_MONTH INT,LOAD_DATE INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ",
            "  STORED AS ORC", " LOCATION '/data/raw/oltp/" };
    private static final String[] ingestionStmt = {
            "SET tez.queue.nam=default;SET hive.exec.dynamic.partition = true;SET mapreduce.framework.name=yarn-tez;SET hive.vectorized.execution.enabled=true;SET hive.cbo.enable = true;SET hive.exec.parallel=true;SET hive.exec.mode.local.auto=true;SET hive.merge.mapfiles=true;SET hive.merge.mapredfiles=true;SET hive.mapred.supports.subdirectories=true;SET tez.runtime.intermediate-input.is-compressed=true;SET tez.runtime.intermediate-output.should-compress=true;SET mapred.output.compression.codec=org.apache.hadoop.io.compress.SnappyCodec;SET mapred.input.dir.recursive=true;SET hive.exec.dynamic.partition.mode=nonstrict;SET hive.execution.engine=tez;SET mapreduce.framework.name=yarn-tez;SET hive.exec.max.dynamic.partitions=10000;SET hive.exec.max.dynamic.partitions.pernode=100000;",
            "INSERT OVERWRITE TABLE ", " PARTITION (LOAD_YEAR,LOAD_MONTH,LOAD_DATE) ",
            ",LOAD_YEAR,LOAD_MONTH,LOAD_DATE FROM " };

    // DB credentials
    private static final String USERNAME = "pashadoop";
    private static final String PASSWORD = "hadoop4gold";

    // input output properties
    private static final String hiveAVROSchema = "PASLND.";
    private static final String hiveORCSchema = "PASSTG.";
    private static final String JDBC_URL = "jdbc:oracle:thin:@n01dol401:1521:perfrep";
    private static final String input_tableslist = "C:\\Users\\gfn9cho\\Documents\\pega\\PASTableslist.txt";
    private static final String output1 = "C:\\ORCDDL_PAS.hql";
    private static final String output2 = "C:\\ORCINIT_PAS.sh";
    private static final String UnixRptFile = "/opt/md/WIP/jose/PAS_Static_InitialLoadExecRpt.txt";
    private static final String HiveLocation = "pas/staging/";

    // table properties
//    private static ArrayList<String> tablecolumns = null;
    private static ArrayList<String> columnNames = null;
    private static ArrayList<String> getColValTypes = null;
    private static String tablename;
    private static StringBuilder fileStr1 = new StringBuilder();
    private static StringBuilder fileStr2 = new StringBuilder();

    public static void main(String args[]) throws Exception {
        readTablesList();
    }

    public static void readTablesList() {
        try {
            Connection jdbcconnection = null;
            Properties connectionproperties = new Properties();
            connectionproperties.put("user", USERNAME);
            connectionproperties.put("password", PASSWORD);
            jdbcconnection = DriverManager.getConnection(JDBC_URL, connectionproperties);

            Path input_tableList = Paths.get(input_tableslist);
            List<String> tablesList = Files.readAllLines(input_tableList);

            // Instantiate the BufferedReader Class
            HashMap<String, String> dataTypeMap = HashMapExp.readPropertiesFile();

            for(String tableName1 : tablesList){
                Statement statement = jdbcconnection.createStatement();
                String sqlStmt;
                sqlStmt = String.format("SELECT * FROM %s WHERE rownum < 3", tableName1);
                try {
                    ResultSet execQuery = statement.executeQuery(sqlStmt);
                    ResultSetMetaData tableMetadata = execQuery.getMetaData();
                    int columnCount = tableMetadata.getColumnCount();
//                    tablecolumns = new ArrayList<>();
                    columnNames = new ArrayList<>();
                    getColValTypes = new ArrayList<>();
                    tablename = tableName1;
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = tableMetadata.getColumnName(i);
                        String columnType = tableMetadata.getColumnTypeName(i).toLowerCase();
                        if(dataTypeMap.containsKey(columnType)) {
                            getColValTypes.add(columnName + ' ' + dataTypeMap.get(columnType));
                        } else {
                            columnType = "STRING";
                            getColValTypes.add(columnName + ' ' + columnType);
                        }
                        if(columnType == "timestamp" || columnType == "datetime" || columnType == "datetime2") {
                            columnName = "from_unixtime(unix_timestamp(" + columnName + ',' + "'yyyy-MM-dd HH:mm:ss'))";
                        }
                        else {
                            columnName = columnName;
                        }
                        columnNames.add(columnName.replace("@", "2").replace("#", "3").replace("$", "4"));

                       }
                    writeHiveStmts();
                    writeHiveInsERTStmts();
                } catch (SQLSyntaxErrorException e) {
                    System.out.println("Table or view does not exist: " + tableName1);
                }
            }
            useFileWriter();
        } catch (Exception e) {
            System.out.println("Issues in loading tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void writeHiveStmts() throws IOException {
        StringBuilder hive_stmt = new StringBuilder();
        hive_stmt.append(tablecreation[0]);
        hive_stmt.append(hiveORCSchema + tablename);
        hive_stmt.append(" (");
        hive_stmt.append(String.valueOf(getColValTypes).replaceAll("\\[", "").replace("]", ")"));
        hive_stmt.append(tablecreation[1]);
        hive_stmt.append("\'\\001'");
        hive_stmt.append(tablecreation[2]);
        hive_stmt.append(tablecreation[3] + HiveLocation);
        hive_stmt.append(tablename + "'" + ";");
        hive_stmt.append('\n');
        fileStr1.append(hive_stmt);
    }

    private static void writeHiveInsERTStmts() throws IOException {
        StringBuilder hive_stmt = new StringBuilder();
        hive_stmt.append("echo " + '"' + "Execution Starts for Table Name " + hiveORCSchema + tablename + '"');
        hive_stmt.append('\n');
        hive_stmt.append("start_time=$(" + "date \"+%s\")");
        hive_stmt.append('\n');
        hive_stmt.append("hive -e" + ' ' + '"' + ingestionStmt[0]).append(ingestionStmt[1]);
        hive_stmt.append(hiveORCSchema + tablename);
        hive_stmt.append(ingestionStmt[2]);
        hive_stmt.append("SELECT ");
        hive_stmt.append(String.valueOf(columnNames).replaceAll("\\[", "").replace("]", ""));
        hive_stmt.append(ingestionStmt[3] + ' ' + hiveAVROSchema + tablename + ';' + '"');
        hive_stmt.append('\n');
        hive_stmt.append("end_time=$(" + "date \"+%s\")");
        hive_stmt.append('\n');
        hive_stmt.append("diff=$(($end_time-$start_time))");
        hive_stmt.append('\n');
        hive_stmt.append("echo " + '"' + "Time Taken to Create " + hiveORCSchema + tablename
                + " is = $(($diff / 60 )) minutes" + '"' + ">>");
        hive_stmt.append(UnixRptFile);
        hive_stmt.append('\n');
        fileStr2.append(hive_stmt).append("\n");
    }

    public static void useFileWriter() {
        FileWriter writer1 = null;
        FileWriter writer2 = null;
        try {
            writer1 = new FileWriter(output1);
            writer2 = new FileWriter(output2);
            writer1.write(fileStr1.toString());
            writer2.write(fileStr2.toString());
            System.out.println("File Writing is Done");
        } catch (IOException e) {
            System.err.println("Error writing the files : " + output1);
            System.err.println("Error writing the files : " + output2);
            e.printStackTrace();
        } finally {
            if (writer1 != null || writer2 != null) {
                try {
                    writer1.close();
                    writer2.close();
                } catch (IOException e) {
                    System.err.println("Error closing the file : " + output1);
                    System.err.println("Error closing the file : " + output2);
                    e.printStackTrace();
                }
            }
        }
    }
}
