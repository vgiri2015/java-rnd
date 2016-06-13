package com.aaa.aa.flumepublisher;

/**
 * Created by gpzpati on 5/28/15.
 */

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

public class FlumeEventPublisher {

    public static void main(String[] argv){
        System.out.println("Entering FlumeEventPublisher, changing to complex object");
        if(argv.length !=2 ){
            System.out.println("Must set 2 parameters");
            System.exit(1);
        }

        String hostName = argv[0];
        int portNumber = Integer.parseInt(argv[1]);
        String tableName = "customer";
        String SchemaString = "{ \"type\" : \"record\",  \"name\" : \"CUSTOMER\",  \"doc\" : \"Sqoop import of CUSTOMER\",  \"fields\" : [ {    \"name\" : \"contactid\",    \"type\" : [ \"int\", \"null\" ],    \"columnName\" : \"contactid\",    \"sqlType\" : \"4\"  }, {    \"name\" : \"firstname\",    \"type\" : [ \"string\", \"null\" ],    \"columnName\" : \"firstname\",    \"sqlType\" : \"12\"  }, {    \"name\" : \"lastname\",    \"type\" : [ \"string\", \"null\" ],    \"columnName\" : \"lastname\",    \"sqlType\" : \"12\"  }, {    \"name\" : \"email\",    \"type\" : [ \"string\", \"null\" ],    \"columnName\" : \"email\",    \"sqlType\" : \"12\"  } ],  \"tableName\" : \"CUSTOMER\"}";

        System.out.println("Value of host name " + hostName);
        System.out.println("Value of port number " + portNumber);
        System.out.println("Value of tableName " + tableName);
        System.out.println("Value of SchemaString " + SchemaString);
        FlumeClientFacade flumeClientFacade = new FlumeClientFacade();
        flumeClientFacade.init(hostName,portNumber,tableName,SchemaString);
        
       for(int i =0 ; i < 1000; i++){
        	GenericRecord record = new GenericData.Record(Schema.parse(SchemaString));
        	//record.put("contactid", toAvro(i));
        	//record.put("firstname", toAvro("firstname"+i));
        	//record.put("lastname", toAvro("lastname"+i));
        	//record.put("lastname", toAvro("email"+i));
        	
        	record.put("contactid", i);
        	record.put("firstname", "firstname"+i);
        	record.put("lastname", "lastname"+i);
        	record.put("email", "email"+i);
        	try{
        		flumeClientFacade.sendDataToFlume(record);
        	}catch(IOException e){
        		
        	}
        }
        flumeClientFacade.cleanUp();
        System.out.println("Exiting FlumeEventPublisher");
    }
    
    /**
     * Convert the Avro representation of a Java type (that has already been
     * converted from the SQL equivalent).
     * @param o
     * @return
     */
 /*   public static Object toAvro(Object o) {
      if (o instanceof BigDecimal) {
        if (bigDecimalFormatString) {
          return ((BigDecimal)o).toPlainString();
        } else {
          return o.toString();
        }
      } else if (o instanceof Date) {
        return ((Date) o).getTime();
      } else if (o instanceof Time) {
        return ((Time) o).getTime();
      } else if (o instanceof Timestamp) {
        return ((Timestamp) o).getTime();
      } else if (o instanceof BlobRef) {
        BlobRef br = (BlobRef) o;
        // If blob data is stored in an external .lob file, save the ref file
        // as Avro bytes. If materialized inline, save blob data as Avro bytes.
        byte[] bytes = br.isExternal() ? br.toString().getBytes() : br.getData();
        return ByteBuffer.wrap(bytes);
      } else if (o instanceof ClobRef) {
        throw new UnsupportedOperationException("ClobRef not suported");
      }
      // primitive types (Integer, etc) are left unchanged
      return o;
    }*/
}
