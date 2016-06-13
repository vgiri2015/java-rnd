/**
 * Created by gfp2ram on 6/9/2015.
 */
import sun.rmi.server.UnicastServerRef;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class MAIG_Initial_Load
{
    private static final String[] tablecreation = {"CREATE EXTERNAL TABLE "," PARTITIONED BY (LOAD_YEAR INT,LOAD_MONTH INT,LOAD_DATE INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ","  STORED AS ORC"," LOCATION '/data/raw/oltp/"};
    private static final String[] ingestionStmt = {"SET tez.queue.nam=default;SET hive.exec.dynamic.partition = true;SET mapreduce.framework.name=yarn-tez;SET hive.vectorized.execution.enabled=true;SET hive.cbo.enable = true;SET hive.exec.parallel=true;SET hive.exec.mode.local.auto=true;SET hive.merge.mapfiles=true;SET hive.merge.mapredfiles=true;SET hive.mapred.supports.subdirectories=true;SET tez.runtime.intermediate-input.is-compressed=true;SET tez.runtime.intermediate-output.should-compress=true;SET mapred.output.compression.codec=org.apache.hadoop.io.compress.SnappyCodec;SET mapred.input.dir.recursive=true;SET hive.exec.dynamic.partition.mode=nonstrict;SET hive.execution.engine=tez;SET mapreduce.framework.name=yarn-tez;SET hive.exec.max.dynamic.partitions=10000;SET hive.exec.max.dynamic.partitions.pernode=100000;","INSERT OVERWRITE TABLE "," PARTITION (LOAD_YEAR,LOAD_MONTH,LOAD_DATE) ",",LOAD_YEAR,LOAD_MONTH,LOAD_DATE FROM "};
    private static final String USERNAME="BDW_CAR_MAIG";
    private static final String PASSWORD="maiguser1";

//    private static final String hiveAVROSchema="COGENLND.";
//    private static final String hiveORCSchema = "COGENSTG.";
//    private static final String JDBC_URL="jdbc:sqlserver://N01DSW077.tent.trt.csaa.pri:1433;DatabaseName=INS_COGEN_Static";
//    private static final String input_tableslist = "C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\COGEN\\Cogen_Tablelist_Static.txt";
//    private static final String output1="C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\COGEN\\initial\\orc\\ORCDDL_COGEN_Static.hql";
//    private static final String output2="C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\COGEN\\initial\\orc\\ORCINIT_COGEN_Static.sh";
//    private static final String UnixRptFile="/opt/md/WIP/giri/COGEN_Static_InitialLoadExecRpt.txt";
//    private static final String HiveLocation="cogen/staging/";

    private static final String hiveAVROSchema="MAIGLND.";
    private static final String hiveORCSchema = "MAIGSTG.";
    private static final String JDBC_URL="jdbc:sqlserver://N01DSW077.tent.trt.csaa.pri:1433;DatabaseName=INS_AUTO_STATIC";
    private static final String input_tableslist = "C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\MAIG\\MAIG_Tablelist_Static.txt";
    private static final String output1="C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\MAIG\\initial\\orc\\ORCDDL_MAIG_Static.hql";
    private static final String output2="C:\\SVNCodeBase\\LegacyArchival-DataAcquistition\\dev\\coding\\DataAcquisition\\Sqoop_load_till_Avro\\MAIG\\initial\\orc\\ORCINIT_MAIG_Static.sh";
    private static final String UnixRptFile="/opt/md/WIP/giri/MAIG_Static_InitialLoadExecRpt.txt";
    private static final String HiveLocation="maig/staging/";


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
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection jdbcconnection = DriverManager.getConnection(JDBC_URL, USERNAME,PASSWORD);
            FileReader tablesList = new FileReader(input_tableslist);

            //Instantiate the BufferedReader Class
            BufferedReader read_tableslist = new BufferedReader(tablesList);
            String tableName1;
            while ((tableName1 = read_tableslist.readLine()) != null) {
                Statement statement = jdbcconnection.createStatement();
                String sqlStmt;
                sqlStmt = String.format("SELECT TOP 2 * FROM %s", tableName1);
                try {
                    ResultSet execQuery = statement.executeQuery(sqlStmt);
                    ResultSetMetaData tableMetadata = execQuery.getMetaData();
                    int columnCount = tableMetadata.getColumnCount();
                    tablecolumns = new ArrayList<>();
                    columnNames = new ArrayList<>();
                    getColValTypes = new ArrayList<>();
                    tablename = tableName1;
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = tableMetadata.getColumnName(i);
                        String columnType = tableMetadata.getColumnTypeName(i);
                        if (columnType.toLowerCase() == "date") {
                            columnType = "DATE";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toLowerCase() == "int") {
                            columnType = "INT";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toLowerCase() == "decimal") {
                            columnType = "DECIMAL(18,3)";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toLowerCase() == "timestamp") {
                            columnType = "TIMESTMAP";
                            getColValTypes.add(columnName+' '+columnType);
                            columnName = "from_unixtime(unix_timestamp(" + columnName + ',' + "'yyyy-MM-dd HH:mm:ss'))";
                        } else if (columnType.toLowerCase() == "varchar") {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toLowerCase() == "binary") {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toLowerCase() == "datetime") {
                            columnType = "TIMESTAMP";
                            getColValTypes.add(columnName+' '+columnType);
                            columnName = "from_unixtime(unix_timestamp(" + columnName + ',' + "'yyyy-MM-dd HH:mm:ss'))";
                        } else if (columnType.toLowerCase() == "time") {
                            columnType = "TIMESTAMP";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toLowerCase() == "datetime2") {
                            columnType = "TIMESTAMP";
                            getColValTypes.add(columnName+' '+columnType);
                            columnName = "from_unixtime(unix_timestamp(" + columnName + ',' + "'yyyy-MM-dd HH:mm:ss'))";
                        } else if (columnType.toLowerCase() == "date") {
                            columnType = "DATE";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toLowerCase() == "char") {
                            columnType = "STRING";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toLowerCase() == "float") {
                            columnType = "DECIMAL(18,3)";
                            getColValTypes.add(columnName+' '+columnType);
                        } else if (columnType.toLowerCase() == "long") {
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