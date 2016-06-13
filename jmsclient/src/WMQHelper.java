/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.ibm.mq.MQC;
import static com.ibm.mq.MQC.MQOO_OUTPUT;
import static com.ibm.mq.MQC.MQPMO_NEW_MSG_ID;
import static com.ibm.mq.MQC.MQPMO_NO_SYNCPOINT;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import static com.ibm.mq.constants.CMQC.MQOO_INPUT_SHARED;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author gd61son
 */
class WMQHelper {

    private String queueManagerName;
    private String queueManagerHostName;
    private int queueManagerPortNumber;
    private String queueManagerChannelName;
    private MQQueueManager queueManager;
    private Properties properties;

    public WMQHelper(String QMGR_NAME, String CHANNEL_NAME, String HOSTNAME,
            int PORT) {

        this.queueManagerHostName = HOSTNAME;
        this.queueManagerName = QMGR_NAME;
        this.queueManagerPortNumber = PORT;
        this.queueManagerChannelName = CHANNEL_NAME;

        properties = new Properties();
        properties.put("HOST_NAME_PROPERTY", this.queueManagerHostName); // required
        properties.put("PORT_PROPERTY", this.queueManagerPortNumber); // required
        properties.put("CHANNEL_PROPERTY", this.queueManagerChannelName); // required
        properties.put("TRANSPORT_PROPERTY", "TRANSPORT_MQSERIES_CLIENT");
    }

//    public void loadWMQProperties() {
//        properties = new Properties();
//        properties.put("HOST_NAME_PROPERTY", this.queueManagerHostName); // required
//        properties.put("PORT_PROPERTY", this.queueManagerPortNumber); // required
//        properties.put("CHANNEL_PROPERTY", this.queueManagerChannelName); // required
//        // properties.put("TRANSPORT_PROPERTY", "TRANSPORT_MQSERIES_CLIENT");
//        properties.put(TRANSPORT_PROPERTY, TRANSPORT_MQSERIES_CLIENT);
//        // properties.put(USER_ID_PROPERTY, "grvkuma@localhost"); 
//    }

    private void initMQ() throws MQException {
        //  LOGGER.debug("Trying to connect to " + queueManagerName);
        try {

            MQEnvironment.hostname = queueManagerHostName;           // Could have put the  
            // hostname & channel  
            MQEnvironment.port=queueManagerPortNumber;
                MQEnvironment.channel = queueManagerChannelName;            // string directly here!  
                MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,MQC.TRANSPORT_MQSERIES);//Connection   
            // MQException.logExclude(MQRC_NO_MSG_AVAILABLE);
           // properties.put("HOST_NAME_PROPERTY", this.queueManagerHostName); // required
           // properties.put("PORT_PROPERTY", this.queueManagerPortNumber); // required
           // properties.put("CHANNEL_PROPERTY", this.queueManagerChannelName); // required
            // properties.put("TRANSPORT_PROPERTY", "TRANSPORT_MQSERIES_CLIENT");
          //  properties.put(TRANSPORT_PROPERTY, TRANSPORT_MQSERIES_CLIENT);
            //queueManager = new MQQueueManager(queueManagerName, properties);
            queueManager = new MQQueueManager(queueManagerName);

            System.out.println("queueManager initialize");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // LOGGER.error("error while connecting to queue");
            System.out.println("MQException ===> ");
            e.printStackTrace();
            throw e;
        }
        // LOGGER.debug("Connected to " + queueManagerName);
    }

    private void disconnectMQ() {
        try {
            if (queueManager != null && queueManager.isConnected()) {
                queueManager.disconnect();
                //LOGGER.debug("QMGR disconnected");
            }
        } catch (Exception ex) {
            //LOGGER.warn("error while disconnecting from queue" + this.queueName.toString() + "error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public String readMessageFromQueue(String queueName) throws MQException, IOException {
        String msgText = "";

        try {
            // this.loadWMQProperties();
            this.initMQ();
            MQMessage msg = new MQMessage();
            MQGetMessageOptions mqGetMsgOpts = new MQGetMessageOptions();
            mqGetMsgOpts.options = CMQC.MQGMO_BROWSE_NEXT;
            MQQueue queue = queueManager.accessQueue(queueName, CMQC.MQOO_BROWSE | MQOO_INPUT_SHARED);
            //LOG.info(queue.getCurrentDepth()+"");
            queue.get(msg, mqGetMsgOpts);
            
            //queue.getCurrentDepth();
            msgText = msg.readStringOfCharLength(msg.getMessageLength());
            msg.clearMessage();

            //queue = queueManager.accessQueue(queueName, CMQC.MQOO_BROWSE | MQOO_INPUT_SHARED);
            mqGetMsgOpts = new MQGetMessageOptions();
            mqGetMsgOpts.options = CMQC.MQGMO_MSG_UNDER_CURSOR;
            queue.get(msg, mqGetMsgOpts);
            queue.close();

        } catch (MQException ex) {
            if (ex.reasonCode == 2033) {
                //LOGGER.debug("No more message in the queue");
            } else {
                ex.printStackTrace();
                //  LOGGER.error(ex.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // LOGGER.error(ex.getMessage());
        } finally {
            //  LOGGER.debug("disconnecting from queue");
            this.disconnectMQ();
        }

        return msgText;
    }

    void writeMessageOnQueue(String queueName,String processedMsg) throws Exception {
        synchronized (processedMsg) {
			this.initMQ();
                        MQMessage message=new MQMessage();
                        message.writeString(processedMsg);
			MQQueue queue = queueManager.accessQueue(queueName, MQOO_OUTPUT);
			MQPutMessageOptions putSpec = new MQPutMessageOptions();
			putSpec.options = putSpec.options | MQPMO_NEW_MSG_ID
					| MQPMO_NO_SYNCPOINT;

			queue.put(message, putSpec);
			queue.close();
			this.disconnectMQ();
		}
    }

}
