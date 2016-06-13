import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.esotericsoftware.kryo.util.IdentityMap;

import javax.security.auth.login.Configuration;
import java.io.*;
import java.nio.Buffer;
import java.util.Map;

public class WordReader implements IRichSpout {

    private FileReader fileReader;
    private boolean completed = false;
    public boolean isDistributed() {return  false;}
    private TopologyContext context;
    private SpoutOutputCollector collector;

    /*
    The first method called in any spout is public void open(Map conf, TopologyContext
    context, SpoutOutputCollector collector).
    The parameters it receives are the TopologyContext,which contains all our topology data;
    the conf object, which is created in the topology definition; and the SpoutOutputCollector, which enables us to emit the
    data that will be processed by the bolts.
     */

    @Override
    public void open(Map conf, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        try
        {
            this.context = topologyContext;
            this.fileReader = new FileReader(conf.get("inputfile.txt").toString());

        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("Error Reading File"+conf.get("inputfile.txt"));
        }
    this.collector=collector;
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("line"));

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }


    /*nextTuple() is called periodically from the same loop as the ack() and fail() methods.
    It must release control of the thread when there is no work to do so that the other
    methods have a chance to be called. So the first line of nextTuple checks to see if
    processing has finished. If so, it should sleep for at least one millisecond to reduce load
    on the processor before returning. If there is work to be done, each line in the file is
    read into a value and emitted.
    */

    @Override
    public void nextTuple()
    {
        if (completed)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                //Do nothing
            }
            return;
        }
        String str;
        BufferedReader reader = new BufferedReader(fileReader);
        try
        {
            while((str = reader.readLine()) !=null)
            {
                this.collector.emit(new Values(str),str);
            }

        } catch (Exception e)
        {
            throw new RuntimeException("Error reading tuple",e);
        }
        finally
        {
            completed=true;
        }
    }

    @Override
    public void ack(Object o) {

    }

    @Override
    public void fail(Object o) {

    }
}