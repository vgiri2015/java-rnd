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
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class XMLToJSON {

	/**
	* Created by gfp2ram on 11/9/2015.
	*/

	    public static int PRETTY_PRINT_INDENT_FACTOR = 20;
	    public static String TEST_XML_STRING ="";
        public static String inputfilePath ="C:\\Docs\\Projects\\CINFIN\\CLAIM\\CLAIM\\XML";//\\PARTY_29611134.001.xml";
        public static String outputfileName;
        public static String outputfileDir =inputfilePath + "\\OUT" ;
        public static String line;
        public static String jsonPrettyPrintString;
        public static PrintWriter pw = null;
        
	    public static void main(String[] args) {
	    	File folder = new File(inputfilePath);
	    	File[] listOfFiles = folder.listFiles();
	    	
	    	XMLToJSON xtj = new XMLToJSON();

	        String outputfilePath = folder.getPath() +"\\OUT";//\\out" + inputFile.getName() + ".json";//"out" + inputFile.getName() + ".json";//
            File newDir = new File(outputfilePath);
            newDir.mkdir();
	    	for (File file : listOfFiles) {
	    	    if (file.isFile()) {
	    	        System.out.println(file.getName());
	    	        xtj.processJSON(file);
	    	    }
	    	}
	    }
	    public boolean processJSON(File inputFile){
	        try {
				// FileReader reads text files in the default encoding.
	            FileReader fileReader = new FileReader(inputFile);
	            outputfileName = "out"+inputFile.getName()+".json";
	            //System.out.println("OutputFileNamme is " + outputfileName);
	            //System.out.println("OutputFilePath is " + inputFile.getAbsolutePath());
	             pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(outputfileDir+"\\"+outputfileName))));

	            // Always wrap FileReader in BufferedReader.
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	            //Below code is to remove new lines from the input file
	            ArrayList<String> lines = new ArrayList<String>();

	            while ((line = bufferedReader.readLine()) != null) {
	                line = line.replaceAll("\n", "");  
	                line = line.replaceAll("\r", "");  
	                lines.add(line);
	            }
	            //Set input xml string as single line of input
	            TEST_XML_STRING = lines.toString();
	            //Commenting below while loop -- read all the lines from file
	            /*
	            while((line = bufferedReader.readLine()) != null) {

					//System.out.println(line);
		        	TEST_XML_STRING = line;
	            } */  

	            // Always close files.
	            bufferedReader.close();         
	        }
	        catch(FileNotFoundException FNFE) {
	            System.out.println(
	                "Unable to open file '" + 
	                inputFile + "'");                
	        }

	        catch(IOException ex) {
	            System.out.println(
	                "Error reading file '" 
	                + inputFile + "'");                  
	            // Or we could just do this: 
	            // ex.printStackTrace();
	        }	    
	        try {
	            JSONObject xmlJSONObj = XML.toJSONObject(TEST_XML_STRING);
	            jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
	            //System.out.println(jsonPrettyPrintString);
	        } catch (JSONException je) {
	            System.out.println(je.toString());
	        }
	        try {
	            // Assume default encoding.

	            // Always wrap FileWriter in BufferedWriter.

	            // Note that write() does not automatically
	            // append a newline character.
	            new FinalJSONFormat(jsonPrettyPrintString.replaceAll("\n",""), pw); 
	            // Always close files.
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Error reading file '" 
	                + inputFile + "'");                  
	            // Or we could just do this: 
	            // ex.printStackTrace();
	        }
	    	return true;
	    }

}
