import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class FinalJSONFormat 
{
	String line;

	FinalJSONFormat(String line1, PrintWriter pw1) throws IOException
	{		
		line = line1;
		int lastIndex = -1;
		
		do
		{
			lastIndex = line.indexOf(": {", lastIndex+1);
			line = line.substring(0,lastIndex-1)+line.substring(lastIndex-1,lastIndex+2).replaceAll(": \\{",": [ \\{")+line.substring(lastIndex+2);
			line = closeBrace(lastIndex, line);
		}while(line.indexOf(": {", lastIndex+1) > lastIndex );
		
		
		System.out.println(line.replaceAll(": \\{",": [ \\{"));
		while(line.contains("  "))
			line = line.replace("  "," ");
		PrintWriter pw = pw1;
		pw.write(line.replaceAll(": \\{", ": [ \\{"));
		pw.write(line.replaceAll("  ", " "));
		pw.flush();
		System.out.println("DONE ! :)");
	}
	
	public static String closeBrace(int lastIndex, String line)
	{
		int braceCount = 0;
		int i;
		
		for(i=lastIndex+3;braceCount >= 0;i++)
		{
			if(line.charAt(i)=='{')
				braceCount++;
			else if(line.charAt(i)=='[')
				braceCount++;
			else if(line.charAt(i)==']')
				braceCount--;
			else if(line.charAt(i)=='}')
				braceCount--;
		}
		line = line.substring(0,i-1)+line.substring(i-1,i).replaceAll("}","} ]")+line.substring(i);
		return line;
	}
	
}