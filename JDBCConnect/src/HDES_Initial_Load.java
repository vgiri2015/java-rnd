/**
 * Created by gfp2ram on 8/12/2015.
 */
/**
 * Created by gfp2ram on 6/9/2015.
 */
import sun.rmi.server.UnicastServerRef;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class HDES_Initial_Load
{
    private static final String[] tablecreation = {"CREATE EXTERNAL TABLE "," PARTITIONED BY (LOAD_YEAR INT,LOAD_MONTH INT,LOAD_DATE INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ","  STORED AS ORC"," LOCATION '/data/raw/oltp/"};
    private static final String[] ingestionStmt = {"SET tez.queue.nam=default;SET hive.exec.dynamic.partition = true;SET mapreduce.framework.name=yarn-tez;SET hive.vectorized.execution.enabled=true;SET hive.cbo.enable = true;SET hive.exec.parallel=true;SET hive.exec.mode.local.auto=true;SET hive.merge.mapfiles=true;SET hive.merge.mapredfiles=true;SET hive.mapred.supports.subdirectories=true;SET tez.runtime.intermediate-input.is-compressed=true;SET tez.runtime.intermediate-output.should-compress=true;SET mapred.output.compression.codec=org.apache.hadoop.io.compress.SnappyCodec;SET mapred.input.dir.recursive=true;SET hive.exec.dynamic.partition.mode=nonstrict;SET hive.execution.engine=tez;SET mapreduce.framework.name=yarn-tez;SET hive.exec.max.dynamic.partitions=10000;SET hive.exec.max.dynamic.partitions.pernode=100000;","INSERT OVERWRITE TABLE "," PARTITION (LOAD_YEAR,LOAD_MONTH,LOAD_DATE) ",",LOAD_YEAR,LOAD_MONTH,LOAD_DATE FROM "};

/*SIS BDW DEV 1*/
//    private static final String USERNAME="EADS_SIS_HADOOP";
//    private static final String PASSWORD="eadss1sdv12015";
//    private static final String hiveAVROSchema="SISLND.";
//    private static final String hiveORCSchema = "SISSTG.";
//    private static final String JDBC_URL="jdbc:oracle:thin:@bdwnonprod-vh.tent.trt.csaa.pri:1521/BDWDEV1";
//    private static final String input_tableslist = "C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\SIS\\SIS_BDW_Tablelist.txt";
//    private static final String output1="C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\SIS\\initial\\orc\\ORCDDL_SIS_BDW.hql";
//    private static final String output2="C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\SIS\\initial\\orc\\ORCINIT_SIS_BDW.sh";
//    private static final String UnixRptFile="/opt/md/WIP/giri/SIS_BDW_InitialLoadExecRpt.txt";
//    private static final String HiveLocation="sis/staging/";

/*HDES BDW DEV 1*/
    private static final String USERNAME="EADS_HDES_HADOOP";
    private static final String PASSWORD="eadshd3sdv12015";
    private static final String hiveAVROSchema="HDESLND.";
    private static final String hiveORCSchema = "HDESSTG.";
    private static final String JDBC_URL="jdbc:oracle:thin:@bdwnonprod-vh.tent.trt.csaa.pri:1521/BDWDEV1";
    private static final String input_tableslist = "C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\HDES\\HDES_BDW_Tablelist.txt";
    private static final String output1="C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\HDES\\initial\\orc\\ORCDDL_HDES_BDW.hql";
    private static final String output2="C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\HDES\\initial\\orc\\ORCINIT_HDES_BDW.sh";
    private static final String UnixRptFile="/opt/md/WIP/giri/HDES_BDW_InitialLoadExecRpt.txt";
    private static final String HiveLocation="hdes/staging/";


    private static ArrayList<String> tablecolumns = null;
    private static ArrayList<String> columnNames = null;
    private static ArrayList<String> getColValTypes = null;
    private static String tablename;
    private static StringBuilder fileStr1 = new StringBuilder();
    private static StringBuilder fileStr2 = new StringBuilder();


    public static void main(String args[]) throws Exception
    {
        readTablesList();
    }

    public static void readTablesList() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection jdbcconnection = DriverManager.getConnection(JDBC_URL, USERNAME,PASSWORD);
            FileReader tablesList = new FileReader(input_tableslist);

            //Instantiate the BufferedReader Class
            BufferedReader read_tableslist = new BufferedReader(tablesList);
            String tableName1;
            while ((tableName1 = read_tableslist.readLine()) != null) {
                Statement statement = jdbcconnection.createStatement();
                String sqlStmt;
                sqlStmt = String.format("SELECT * FROM %s WHERE ROWNUM < 3", tableName1);
                try {
                    ResultSet execQuery = statement.executeQuery(sqlStmt);
                    ResultSetMetaData tableMetadata = execQuery.getMetaData();
                    int columnCount = tableMetadata.getColumnCount();
                    tablecolumns = new ArrayList<>();
                    columnNames = new ArrayList<>();
                    getColValTypes = new ArrayList<>();
                    String[] nameSplit = tableName1.split("_");
                    tablename = nameSplit[1];
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = tableMetadata.getColumnName(i);
                        String columnType = tableMetadata.getColumnTypeName(i);
                        if (columnType.toUpperCase() == "NUMBER") {
                            columnType = "DOUBLE";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toUpperCase() == "STRING") {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toUpperCase() == "FIXED_CHAR") {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toUpperCase() == "TIMESTAMP") {
                            columnType = "TIMESTAMP";
                            getColValTypes.add(columnName+' '+columnType);
                            columnName = "from_unixtime(unix_timestamp(" + columnName + ',' + "'yyyy-MM-dd HH:mm:ss'))";
                        } else if (columnType.toUpperCase() == "CLOB") {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toUpperCase() == "BLOB") {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toUpperCase() == "VARCHAR") {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toUpperCase() == "CHAR") {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toUpperCase() == "DATE") {
                            columnType = "DATE";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toUpperCase() == "RAW") {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toUpperCase() == "FLOAT") {
                            columnType = "DECIMAL";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toUpperCase() == "LONG") {
                            columnType = "DOUBLE";
                            getColValTypes.add(columnName+' '+columnType);
                        } else {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        }
//                        tablecolumns.add(columnName + " " + columnType);
//                        tablecolumns.add(columnName.replace("@","2").replace("#","3").replace("$", "4") + " " + columnType);
                        columnNames.add(columnName.replace("@","2").replace("#","3").replace("$", "4"));
                    }
                    writeHiveStmts();
                    writeHiveInsERTStmts();
                }
                catch (SQLSyntaxErrorException e)
                {
                    System.out.println("Issues with the Table" + e.getMessage());
                    //e.printStackTrace();
                }
            }
            //Close the buffer reader
            read_tableslist.close();
            useFileWriter();
        }
        catch (Exception e)
        {
            System.out.println("Issues in Loading Tables:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void writeHiveStmts() throws IOException {
        StringBuilder hive_stmt = new StringBuilder();
        hive_stmt.append(tablecreation[0]);
        hive_stmt.append(hiveORCSchema+tablename);
        hive_stmt.append('(');
        hive_stmt.append(String.valueOf(getColValTypes).replaceAll("\\[", "").replace("]", ")"));
        hive_stmt.append(tablecreation[1]);
        hive_stmt.append("\'\\001'");
        hive_stmt.append(tablecreation[2]);
        hive_stmt.append(tablecreation[3]+HiveLocation);
        hive_stmt.append(tablename+ "'" + ";");
        hive_stmt.append('\n');
        fileStr1.append(hive_stmt);
    }

    private static void writeHiveInsERTStmts() throws IOException {
        StringBuilder hive_stmt = new StringBuilder();
        hive_stmt.append("echo "+'"'+"Execution Starts for Table Name "+hiveORCSchema+tablename+'"');
        hive_stmt.append('\n');
        hive_stmt.append("start_time=$("+"date \"+%s\")");
        hive_stmt.append('\n');
        hive_stmt.append("hive -e"+' '+'"'+ingestionStmt[0]).append(ingestionStmt[1]);
        hive_stmt.append(hiveORCSchema+tablename);
        hive_stmt.append(ingestionStmt[2]);
        hive_stmt.append("SELECT ");
        hive_stmt.append(String.valueOf(columnNames).replaceAll("\\[", "").replace("]", ""));
        hive_stmt.append(ingestionStmt[3]+' '+hiveAVROSchema + tablename + ';'+'"');
        hive_stmt.append('\n');
        hive_stmt.append("end_time=$("+"date \"+%s\")");
        hive_stmt.append('\n');
        hive_stmt.append("diff=$(($end_time-$start_time))");
        hive_stmt.append('\n');
        hive_stmt.append("echo "+'"'+"Time Taken to Create "+hiveORCSchema+ tablename+" is = $(($diff / 60 )) minutes"+'"'+">>");
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
            System.err.println("Error writing the files : "+output1);
            System.err.println("Error writing the files : "+output2);
            e.printStackTrace();
        } finally {
            if (writer1 != null || writer2!=null) {
                try {
                    writer1.close();
                    writer2.close();
                } catch (IOException e) {
                    System.err.println("Error closing the file : "+output1);
                    System.err.println("Error closing the file : "+output2);
                    e.printStackTrace();
                }
            }
        }
    }
}