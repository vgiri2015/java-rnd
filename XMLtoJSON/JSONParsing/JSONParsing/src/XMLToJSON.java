	import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class XMLToJSON {

	/**
	* Created by gfp2ram on 11/9/2015.
	*/

	    public static int PRETTY_PRINT_INDENT_FACTOR = 20;
	    public static String TEST_XML_STRING ="";
        public static String fileName ="C:/Users/apeerzada/Desktop/cinfin poc/CLAIM/CLAIM/XML/CLAIM_57983267.001.xml";
        public static String line;
        public static String jsonPrettyPrintString;
        public static PrintWriter pw = null;
        
	    public static void main(String[] args) {
	        try {
				// FileReader reads text files in the default encoding.
	            FileReader fileReader = 
	                new FileReader(fileName);
	            
	             pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("D:/output.txt"))));

	            // Always wrap FileReader in BufferedReader.
	            BufferedReader bufferedReader = new BufferedReader(fileReader);

	            while((line = bufferedReader.readLine()) != null) {

					//System.out.println(line);
		        	TEST_XML_STRING = line;
	            }   

	            // Always close files.
	            bufferedReader.close();         
	        }
	        catch(FileNotFoundException FNFE) {
	            System.out.println(
	                "Unable to open file '" + 
	                fileName + "'");                
	        }

	        catch(IOException ex) {
	            System.out.println(
	                "Error reading file '" 
	                + fileName + "'");                  
	            // Or we could just do this: 
	            // ex.printStackTrace();
	        }	    
	        try {
	            JSONObject xmlJSONObj = XML.toJSONObject(TEST_XML_STRING);
	            jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
	            //System.out.println(jsonPrettyPrintString);
	        } catch (JSONException je) {
	           // System.out.println(je.toString());
	        }
	        try {
	            // Assume default encoding.

	            // Always wrap FileWriter in BufferedWriter.

	            // Note that write() does not automatically
	            // append a newline character.
	            System.out.println("************");
	            new FinalJSONFormat(jsonPrettyPrintString.replaceAll("\n",""), pw); 
	            /*bufferedWriter.write(" here is some text.");
	            bufferedWriter.newLine();
	            bufferedWriter.write("We are writing");
	            bufferedWriter.write(" the text to the file.");
				*/
	            // Always close files.
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Error reading file '" 
	                + fileName + "'");                  
	            // Or we could just do this: 
	            // ex.printStackTrace();
	        }
	    }

}
