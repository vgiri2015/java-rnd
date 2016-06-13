/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gd61son
 */
public class JmsClient {

    /**
     * @param args the command line arguments
     */
    WMQHelper helper2 = null;

    public String readQueueMessage() {
        Properties qmgr1 = new Properties();
        qmgr1.put("QMGR_NAME", "QHDPPROD1");
        qmgr1.put("CHANNEL_NAME", "HDP.PROD.QUOTE");
        qmgr1.put("HOSTNAME", "172.26.142.71");
        qmgr1.put("PORT", "1420");

//        Properties qmgr2 = new Properties();
//        qmgr2.put("QMGR_NAME", "QHDPPERF2");
//        qmgr2.put("CHANNEL_NAME", "HDP.PERF.QUOTE");
//        qmgr2.put("HOSTNAME", "192.168.50.46");
//        qmgr2.put("PORT", "1426");
        //LOGGER.info(qmgr2.getProperty("QMGR_NAME") + qmgr2.getProperty("CHANNEL_NAME") + qmgr2.getProperty("HOSTNAME") + Integer.parseInt(qmgr2.getProperty("PORT")));
        // WMQHelper helper1 = new WMQHelper(qmgr2.getProperty("QMGR_NAME"), qmgr2.getProperty("CHANNEL_NAME"), qmgr2.getProperty("HOSTNAME"), Integer.parseInt(qmgr2.getProperty("PORT")));
        this.helper2 = new WMQHelper(qmgr1.getProperty("QMGR_NAME"), qmgr1.getProperty("CHANNEL_NAME"), qmgr1.getProperty("HOSTNAME"), Integer.parseInt(qmgr1.getProperty("PORT")));

        String msg1 = "";
        try {
            msg1 = helper2.readMessageFromQueue("LQ.HDP.ERROR.REPLAY.QUOTE.Q");
        } catch (MQException ex) {
            Logger.getLogger(JmsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JmsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        // String msg2 = helper2.readMessageFromQueue(recordQueue);
        return msg1;
    }

    private String processInputMessage(String msg) {
        String processedMsg = "";
        if (msg != null) {
            processedMsg = msg.replaceAll("\\s+", "").trim();
            CharSequence errorMsg = "[::ERROR::METADATA::BEGIN]";
            if (processedMsg.contains(errorMsg)) {
                processedMsg = processedMsg.substring(0, processedMsg.indexOf("[::ERROR::METADATA::BEGIN]"));
            }
        }
        return processedMsg;
    }

    private void writeQueueMessage(String processedMsg) {
//        Properties qmgr1 = new Properties();
//        qmgr1.put("QMGR_NAME", "QHDPPERF1");
//        qmgr1.put("CHANNEL_NAME", "HDP.PERF.QUOTE");
//        qmgr1.put("HOSTNAME", "192.168.50.47");
//        qmgr1.put("PORT", "1426");
        try {
            //helper2 = new WMQHelper(qmgr1.getProperty("QMGR_NAME"), qmgr1.getProperty("CHANNEL_NAME"), qmgr1.getProperty("HOSTNAME"), Integer.parseInt(qmgr1.getProperty("PORT")));
            helper2.writeMessageOnQueue("LQ.HDP.RECORD.QUOTE.Q", processedMsg);
        } catch (Exception ex) {
            Logger.getLogger(JmsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        // TODO code application logic here
        for (int i=0;i<5;i++){
        JmsClient client = new JmsClient();
        String msg = client.readQueueMessage();
        System.out.println("Queue Message before processing ===> " + msg);
        String processedMsg = client.processInputMessage(msg);
        System.out.println("Queue Message After processing ===> " + processedMsg);
        client.writeQueueMessage(processedMsg);
        System.out.println("Message inserted into :: LQ.HDP.RECORD.QUOTE.Q(RequestQueue) Queue");
        }
    }

}
