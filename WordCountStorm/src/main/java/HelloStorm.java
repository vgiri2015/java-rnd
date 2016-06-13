import backtype.storm.Config;
import backtype.storm.topology.TopologyBuilder;

/**
 * Created by gfp2ram on 4/10/2015.
 */
public class HelloStorm {
    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.put("inputfile.txt", args);
        config.setDebug(true);
        config.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("Word-Reader", new WordReader());
        builder.setBolt("Word-Normalizer",new WordNormalizer()).shuffleGrouping("Word-Reader");



        //builder.setSpout("",Spo)
    }
}

