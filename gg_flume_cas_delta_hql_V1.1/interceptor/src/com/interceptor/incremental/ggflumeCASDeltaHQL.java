package com.interceptor.incremental;

import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

/**
 * 
 * @author gkpzgon on 07/14/2015
 *
 */
public class ggflumeCASDeltaHQL {
	public static void main(String args[]) {

		// HashSet does not allow any duplicate values.
		HashSet<String> table_name = new HashSet<String>();
		Hashtable<String, String> hashtable = new Hashtable<String, String>();

		// file's time stamp creation
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String date_format = dateFormat.format(cal.getTime());

		// read program's arguments
		String source_argument = null;
		String target_argument = null;
		String file_source = null;
		if (args.length >= 3) {
			source_argument = args[0];
			target_argument = args[1];
			file_source= args[2];
		} else {
			System.out.println(
					"You need 3 arguments to run this jar file.\n"
					+ "1st argument should be the source file (flume interceptor log file)\n"
					+ "2nd argument should be the local path were you want to save your output file\n"
					+ "3rd argument should be the file containing the table attributes (Delimeter is / )"
					);
			// terminate execution
			System.exit(0);
		}
		
		// read the input file
		table_name = ReadSource(source_argument);

		// make a hash table with all the table attributes
		hashtable = MakeHashTable(file_source);

		// make an HQL file
		MakeOutputFile(table_name, hashtable, date_format, target_argument);

	}// END OF MAIN() Method

	/**
	 * This method will read the source file(log) that is created by Flume Interceptor
	 * 
	 * @param source_argument - file's path location
	 * @return hash set with all the table names that changes in the last 24 hours
	 */
	private static HashSet<String> ReadSource(String source_argument) {
		HashSet<String> table_name = new HashSet<String>();
		String strLine;

		// Open the file
		FileInputStream fstream;
		BufferedReader br;
		try {
			fstream = new FileInputStream(source_argument);
			br = new BufferedReader(new InputStreamReader(fstream));
			// Read File Line By Line
			try {
				while ((strLine = br.readLine()) != null) {
					// Push content on the hash set
					table_name.add(strLine);
				}
				// Close the input stream
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return table_name;
		
	}// END OF ReadSource() Method
	

	/**
	 * This method will create the hql file that Oozie will use for the incremental
	 * 
	 * @param table_name - hash set containing the table's name from flume interceptor
	 * @param hashtable - hash table containing the table's name and attributes
	 * @param date_format - to save the file in HDFS with different time stamp
	 * @param target_argument - the local path location were the output file will be save
	 */
	private static void MakeOutputFile(HashSet<String> table_name, Hashtable<String, String> hashtable,
			String date_format, String target_argument) {
		Iterator<String> it = table_name.iterator();
		String value;
		PrintWriter errorlog; //error file log

		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(target_argument), "utf-8"))) {
			
			errorlog = new PrintWriter("/tmp/error_table.log", "UTF-8");

			while (it.hasNext()) {
				value = hashtable.get(it.next());
				writer.write(
						"SET hive.exec.compress.output=true;hive.exec.compress.intermediate=true;SET hive.merge.mapfiles=true;SET hive.exec.dynamic.partition=true;SET hive.vectorized.execution.enabled=true;SET hive.cbo.enable = true;SET hive.exec.parallel=true;SET hive.exec.mode.local.auto=true;SET hive.merge.mapfiles=true;SET hive.merge.mapredfiles=true;SET hive.mapred.supports.subdirectories=true;SET mapred.output.compression.codec=org.apache.hadoop.io.compress.SnappyCodec;SET mapred.input.dir.recursive=true;SET hive.exec.dynamic.partition.mode=nonstrict;SET mapreduce.framework.name=yarn-tez;SET hive.exec.max.dynamic.partitions=10000;SET hive.exec.max.dynamic.partitions.pernode=10000;");
				writer.write('\n');
				writer.write(value);
				writer.write('\n');
			}
			// close files to avoid issue
			writer.close();
			errorlog.close();

			Configuration conf = new Configuration();
			conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
			conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
			conf.addResource(new Path("/etc/hadoop/conf/mapred-site.xml"));

			FileSystem hdfs = FileSystem.get(conf);

			// copy new file from Local to HDFS
			Path newFolderPath = new Path("/OOZIE/goldengate/incremental/scripts/CAS/hive_actions");

			Path localFilePath = new Path(target_argument);
			Path hdfsFilePath = new Path(newFolderPath + "/CASDelta_" + date_format + ".hql");
			hdfs.copyFromLocalFile(localFilePath, hdfsFilePath);

			// trigger file
			Path TriggerFilePath = new Path(
					"/OOZIE/goldengate/incremental/scripts/CAS/hive_actions/CASDelta_HQL_Creation_Success.txt");
			hdfs.delete(TriggerFilePath, true);
			hdfs.createNewFile(TriggerFilePath);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}// END OF MakeOutputFile() Method
	
	/**
	 * This method will make a hash table with all the table's attributes
	 * 
	 * @param filelocation - source path where the file is located
	 * @return hash table with table name as KEY, attributes as VALUES
	 */
	private static Hashtable<String, String> MakeHashTable(String filelocation) {
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		String strLine = null;

		// Open the file
		FileInputStream fstream;
		BufferedReader br;
		try {
			fstream = new FileInputStream(filelocation);
			br = new BufferedReader(new InputStreamReader(fstream));
			try {
				while ((strLine = br.readLine()) != null) {
					Scanner sc = new Scanner(strLine);
					sc.useDelimiter("/");
					while(sc.hasNext()){
						hashtable.put(sc.next(), sc.next());
					}
					// close the scanner 
					sc.close();
				}
				// close the file
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return hashtable;
		
	}// END OF MakeHashTable() Method

}// END OF IncerceptorTable CLASS
