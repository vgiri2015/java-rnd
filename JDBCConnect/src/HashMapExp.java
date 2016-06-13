package pega.initialload;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by gfn9cho on 11/3/2015.
 */
public class HashMapExp {

    public static void main(String[] args) {
        readPropertiesFile();
    }
    public static HashMap<String, String> readPropertiesFile() {
        Properties prop = new Properties();
        Path prpfilepath = Paths.get("C:\\Users\\gfn9cho\\Documents\\pega\\DataTypeMatch.properties");
        HashMap<String, String> datatypes = new HashMap<>();

        try (InputStream prpfile = Files.newInputStream(prpfilepath);
             BufferedInputStream buffered = new BufferedInputStream(prpfile)){
            prop.load(buffered);
            for(String dbmsdatatype : prop.stringPropertyNames()){
                datatypes.put(dbmsdatatype, prop.getProperty(dbmsdatatype));
            }

        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }

        for(String key : datatypes.keySet() ){
            System.out.println(key + ":" + datatypes.get(key));
        }
        return datatypes;
    }


}
