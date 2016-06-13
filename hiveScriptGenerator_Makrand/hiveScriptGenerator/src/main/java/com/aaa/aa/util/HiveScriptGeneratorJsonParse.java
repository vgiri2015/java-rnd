/**
 * 
 */
package com.aaa.aa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author gkn7ran
 *
 */
public class HiveScriptGeneratorJsonParse {
	
	private static ConfigProperties prop = null;
	private static String avroQuery = null;
	private static String orcQuery = null;
	private static PrintWriter avroWriter = null;
	private static PrintWriter orcWriter = null;
	private static String avroSchemaFileLocation = null;
	private static ObjectMapper mapper = null;
	private static final Map<String, String> AvroToHiveDataTypeMap = new HashMap<String,String>(){ {
		put("null","void");
		put("boolean","boolean");
		put("int","int");
		put("long","bigint");
		put("float","float");
		put("double","double");
		put("bytes","binary");
		put("string","string");
		put("record","struct");
		put("map","map");
		put("list","array");
		put("union","union");
		put("enum","string");
		put("fixed","binary");
	}};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length < 2)
	    {
	        System.out.println("Usage is: java HiveScriptGeneratorJsonParse <AVSC_SCHEMA_PROPERTIES_FILENAME> <AVSC_SCHEMA_LOCAL_FILE_SYSTEM_LOCATION>");
	        System.exit(1);
	    }
		String[] avscFileArr = args[0].split("\\.");
		
		String avscFile = avscFileArr[0];
		avroSchemaFileLocation = args[1];
		
		try {
			avroWriter = new PrintWriter(avscFile+"_AVRO.hive");
			orcWriter = new PrintWriter(avscFile+"_ORC.hive");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		HiveScriptGeneratorJsonParse hiveScriptGeneratorJsonParse = new HiveScriptGeneratorJsonParse();
		hiveScriptGeneratorJsonParse.processSchemaFile(args[0]);
		
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
					
			System.out.println("Start creating AVRO and ORC tables using properties file: "+avroSchemaPropertieFile);
			File file = new File(avroSchemaPropertieFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String avroSchemaFile;
	
			avroQuery = prop.getValue("AVRO_CREATE_TABLE").replaceAll("DATA_LOCATION", prop.getValue("DATA_LOCATION")).replaceAll("SCHEMA_LOCATION", prop.getValue("SCHEMA_LOCATION"));
			orcQuery = prop.getValue("ORC_CREATE_TABLE").replaceAll("DATA_LOCATION", prop.getValue("DATA_LOCATION"));
			mapper = new ObjectMapper();
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
	 * @param avroSchemaFileName
	 * @return
	 */
	public static boolean createTables(String avroSchemaFileName){
		
		String[] arr = avroSchemaFileName.split("\\.");
		String tableDirectory = arr[0]+"."+arr[1];
		String tableName = arr[1];

		//Creating AVRO table
		String finalAvroQuery = avroQuery.replaceAll("TABLE_NAME", tableName).replaceAll("TABLE_DIRECTORY", tableDirectory).replaceAll("AVSC_FILENAME", avroSchemaFileName);
		System.out.println("********Writing AVRO Query==> "+finalAvroQuery);
		avroWriter.println(finalAvroQuery+";");
		
		//Getting meta data from AVRO schema file
		 String columnList = "";
		try {
			Map<String,String> map = new HashMap<String,String>();
			map = mapper.readValue(new File(avroSchemaFileLocation+"/"+avroSchemaFileName), HashMap.class);
			Object fieldsObj = (Object)map.get("fields");
			ArrayList fieldsArr = (ArrayList)fieldsObj;
			int fieldLength = fieldsArr.size();
			for(int i=0;i<fieldLength;i++){
				Map<String,String> fieldMap= (Map)fieldsArr.get(i);
				String columnName = fieldMap.get("name").toLowerCase();
				Object columnTypeObj = (Object)fieldMap.get("type");
				String columnTypeAvro = "";
				String columnTypeHive = "";
				if(columnTypeObj.toString().startsWith("[") && columnTypeObj.toString().endsWith("]") ){
					ArrayList columnTypeArr = (ArrayList)columnTypeObj;
					for(int j=0;j<columnTypeArr.size();j++){
						if(!columnTypeArr.get(j).toString().equals("null")){
							columnTypeAvro = (String)columnTypeArr.get(j);
							break;
						}
					}
				}else{
					columnTypeAvro = (String)columnTypeObj;
				}

				if (AvroToHiveDataTypeMap.containsKey(columnTypeAvro)){
					columnTypeHive = AvroToHiveDataTypeMap.get(columnTypeAvro);
				}
				else{
					columnTypeHive = columnTypeAvro;
				}
				
				columnList+=columnName+" "+columnTypeHive;
				if (i<fieldLength-1){
					 columnList+=", ";
				 }
			}
			
		} catch (JsonParseException e) {
			e.printStackTrace();
			 return false;
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		//Creating ORC table from the schema file
		 String finalOrcQuery = orcQuery.replaceAll("TABLE_NAME", tableName).replaceAll("COLUMN_LIST_WITH_DATATYPE", columnList).replaceAll("TABLE_DIRECTORY", tableDirectory);
		 System.out.println("********Writing ORC Query==> "+finalOrcQuery);
		 orcWriter.println(finalOrcQuery+";");
		 return true;
	}
}
