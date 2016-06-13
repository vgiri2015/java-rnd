/**
 * 
 */
package com.aaa.aa.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @author gkn7ran
 *
 */
public class ConfigProperties {
	
	   private static ConfigProperties instance = null;
	   private Properties properties;

	    /**
	     * @throws IOException
	     */
	    protected ConfigProperties() throws IOException, NullPointerException{
	        properties = new Properties();
	        properties.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
	    }

	    /**
	     * @return
	     */
	    public static ConfigProperties getInstance() {
	        if(instance == null) {
	            try {
	                instance = new ConfigProperties();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return instance;
	    }

	    /**
	     * @param key
	     * @return
	     */
	    public String getValue(String key) {
	        return properties.getProperty(key);
	    }

}
