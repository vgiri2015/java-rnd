/**
 * 
 */
package com.aaa.aa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author gkn7ran
 *
 */
public class HiveScriptGeneratorJDBC {
	
	private static Connection connection = null;
	private static Statement stmt = null;
	private static ConfigProperties prop = null;
	private static String avroQuery = null;
	private static String orcQuery = null;
	private static PrintWriter avroWriter = null;
	private static PrintWriter orcWriter = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length == 0)
	    {
	        System.out.println("Usage is: java HiveScriptGeneratorJDBC <AVSC_SCHEMA_PROPERTIES_FILENAME>");
	        System.exit(1);
	    }
		String[] avscFileArr = args[0].split("\\.");
		
		String avscFile = avscFileArr[0];
		
		try {
			avroWriter = new PrintWriter(avscFile+"_AVRO.hive");
			orcWriter = new PrintWriter(avscFile+"_ORC.hive");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	
		HiveScriptGeneratorJDBC hiveScriptGeneratorjdbc = new HiveScriptGeneratorJDBC();
		hiveScriptGeneratorjdbc.processSchemaFile(args[0]);
		
		avroWriter.close();
		orcWriter.close();		
	}
	
	/**
	 * @param avroSchemaPropertieFile
	 */
	public void processSchemaFile(String avroSchemaPropertieFile){
		
		prop = ConfigProperties.getInstance();
		if (prop == null){
			System.out.println("Unable to read the config properties file. Exiting");
			System.exit(1);
		}else{
			System.out.println("Successfully loaded config properties file.");
		}
		
		avroWriter.println("set hivevar:HIVE_SCHEMA_NAME="+prop.getValue("AVRO_SCHEMA_NAME")+";");
		orcWriter.println("set hivevar:HIVE_SCHEMA_NAME="+prop.getValue("ORC_SCHEMA_NAME")+";");
		
		long startTime = System.currentTimeMillis();
		int lineCount=0;
		 try {
			System.out.println("Connecting to Hive DB......");
			if(!getConnection()){
				System.out.println("Unable to get the connection for hive DB. Exiting");
				System.exit(1);
			}else{
				System.out.println("Successfully conneted to Hive DB.");
			}
			
			System.out.println("Start creating AVRO and ORC tables using properties file: "+avroSchemaPropertieFile);
			File file = new File(avroSchemaPropertieFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String avroSchemaFile;
			
			avroQuery = prop.getValue("AVRO_CREATE_TABLE").replaceAll("DATA_LOCATION", prop.getValue("DATA_LOCATION")).replaceAll("SCHEMA_LOCATION", prop.getValue("SCHEMA_LOCATION"));
			orcQuery = prop.getValue("ORC_CREATE_TABLE").replaceAll("DATA_LOCATION", prop.getValue("DATA_LOCATION"));
			stmt.executeUpdate("set hivevar:HIVE_SCHEMA_NAME=CAS");
			while ((avroSchemaFile = bufferedReader.readLine()) != null) {
				if(!createTables(avroSchemaFile))
				{
					System.out.println("Unable to create table for schema==> "+avroSchemaFile);
				}else{
					lineCount++;
				}
			}
			fileReader.close();
			System.out.println("Successfully created AVRO and ORC tables for "+lineCount+" schema files.");
			stmt.close();
			connection.close();
			long endTime = System.currentTimeMillis();
			long diff = endTime - startTime;
			int minutes = (int) ((diff / (1000*60)) % 60);
			System.out.println("%%%%%%%%%%%%%%%%%%%%Total Time taken to execute ==> "+ minutes+" Minutes");
			} catch (Exception e) {
				 e.printStackTrace();
				 System.exit(1);
			}
	}
	
	/**
	 * @return boolean
	 */
	public static boolean getConnection(){
		 try {
			Class.forName(prop.getValue("DRIVERNAME"));
			connection= DriverManager.getConnection(prop.getValue("URL"), prop.getValue("USERNAME"), prop.getValue("PASSWORD"));
			stmt = connection.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		 return true;
	}
	
	/**
	 * @param avroSchemaFileName
	 * @return boolean
	 */
	public static boolean createTables(String avroSchemaFileName){
		
		String[] arr = avroSchemaFileName.split("\\.");
		String tableDirectory = arr[0]+"."+arr[1];
		String tableName = arr[1];

		try {
			//Creating AVRO table
			String finalAvroQuery = avroQuery.replaceAll("TABLE_NAME", tableName).replaceAll("TABLE_DIRECTORY", tableDirectory).replaceAll("AVSC_FILENAME", avroSchemaFileName);
			System.out.println("********Ececuting AVRO Query==> "+finalAvroQuery);
			avroWriter.println(finalAvroQuery+";");
			stmt.executeUpdate(finalAvroQuery);
			
			//Getting meta data from AVRO table
			 ResultSetMetaData resultSet = stmt.executeQuery("select * from "+tableName).getMetaData();
			 int count = resultSet.getColumnCount()-3;
			 String columnList = "";
			 for (int i=1;i<=count;i++) {
				 String columnName = resultSet.getColumnName(i).substring(resultSet.getColumnName(i).indexOf(".")+1, resultSet.getColumnName(i).length());
				 columnList+=columnName+" "+resultSet.getColumnTypeName(i);
				 if (i<count){
					 columnList+=", ";
				 }
			 }
			 
			//Creating ORC table from the meta data
			 String finalOrcQuery = orcQuery.replaceAll("TABLE_NAME", tableName).replaceAll("COLUMN_LIST_WITH_DATATYPE", columnList).replaceAll("TABLE_DIRECTORY", tableDirectory);
			 System.out.println("********Executing ORC Query==> "+finalOrcQuery);
			 orcWriter.println(finalOrcQuery+";");
			 //stmt.executeUpdate(finalOrcQuery);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
