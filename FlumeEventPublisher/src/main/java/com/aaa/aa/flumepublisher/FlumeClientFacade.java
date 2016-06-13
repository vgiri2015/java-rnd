package com.aaa.aa.flumepublisher;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientConfigurationConstants;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by gpzpati on 5/28/15.
 */
public class FlumeClientFacade {
    Logger logger = Logger.getLogger(FlumeClientFacade.class.getName());

    private RpcClient client;
    private String hostName;
    private int port;
    private Properties rpcProperties;
    private Map<String, String> flumeHeadersMap = new HashMap<String, String>();
    private Schema schema;
    private int rec_count;
    List<Event> events= null;
    int batchSize = 0;

    @SuppressWarnings("deprecation")
	public void init(String hostName, int port, String tableName, String schemaString){
       logger.fine("Inside FlumeClientFacade.init() " + hostName + " " + port +" "+ tableName + " " + schemaString);
        this.hostName = hostName;
        this.port = port;
        setProperties();
        //client = RpcClientFactory.getDefaultInstance(hostName, port);
        client = RpcClientFactory.getInstance(rpcProperties);
        flumeHeadersMap.put("table",tableName);
        flumeHeadersMap.put("flume.avro.schema.literal",schemaString);
        schema = Schema.parse(schemaString);
        rec_count=0;
        System.out.println("******************** Batch size==> "+client.getBatchSize());
        events = new ArrayList<Event>();
        batchSize = client.getBatchSize();
    }

   public void sendDataToFlume(GenericRecord genericRecord) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer =
                new GenericDatumWriter<GenericRecord>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(genericRecord, encoder);
        encoder.flush();

        byte[] eventPayLoad = out.toByteArray();

       Event event= EventBuilder.withBody(eventPayLoad, flumeHeadersMap);
       
       events.add(event);
       rec_count++;
       
       if(rec_count==batchSize){
 	        try {
	        	//logger.info("@@@@@@@@@@@@@ Writing message to flume ");
	        	//client.append(event);
 	        	client.appendBatch(events);
 	        	events = new ArrayList<Event>();
 	        	rec_count=0;
	        }catch(EventDeliveryException eventDeliveryException){
	            System.out.println("Error occured in sending data to flume " + eventDeliveryException.getMessage());
	            eventDeliveryException.printStackTrace();
	            client.close();
	            client = null;
	            client = RpcClientFactory.getInstance(rpcProperties);
	        }
       }
   }
   
   /**
    * set the properties for the RPC connection. There are many
    * more properties than are reflected here. See 
    * RpcClientConfiguratonConstants for more info.
    */
   private void setProperties() {
       /* this is the default for NiOClientSocketChannelFactory, which is
        * called by NettyAvroRpcClient which is called from the 
        * RpcClientFactory. Setting it explicitly here to avoid a warning
        * message that would otherwise be printed. We shouldn't really
        * have to do this. :-)
        */  
       //int maxIoWorkers = Runtime.getRuntime().availableProcessors() * 2;
       //System.out.println("******************** maxIoWorkers==> "+maxIoWorkers);
       
       rpcProperties = new Properties();
       rpcProperties.setProperty(RpcClientConfigurationConstants.CONFIG_HOSTS, "h1");
       rpcProperties.setProperty(RpcClientConfigurationConstants.CONFIG_HOSTS_PREFIX + "h1",
                hostName + ":" + port);
       rpcProperties.setProperty(RpcClientConfigurationConstants.CONFIG_BATCH_SIZE, 
    		   String.valueOf(RpcClientConfigurationConstants.DEFAULT_BATCH_SIZE));
       //rpcProperties.setProperty(RpcClientConfigurationConstants.MAX_IO_WORKERS, 
         //                       String.valueOf(maxIoWorkers));
   }

    public void cleanUp(){
    	//logger.info("$$$$$$$$$$$$$$$$ Inside FlumeClientFacade.cleanUp()$$$$$$$$$$$$$$$$$$$$$4 record counter==> "+ rec_count);
    	if (rec_count>0){
    		try{
	        	client.appendBatch(events);
	        }catch(EventDeliveryException eventDeliveryException){
	        	System.out.println("Error occured in sending data to flume " + eventDeliveryException.getMessage());
	        	eventDeliveryException.printStackTrace();
            }
    	}
    	if(client!=null){
    		client.close();
    		client=null;
    	}
    }
}