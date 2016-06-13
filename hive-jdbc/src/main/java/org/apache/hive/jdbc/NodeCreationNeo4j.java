/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.hive.jdbc;

/**
 *
 * @author Varatharajan Giri Ramanathan
 */
import java.util.List;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class NodeCreationNeo4j {

    //private static final String Hive_Neo4j_path = "//ent.rt.csaa.com/aaa/Profiles/Citrix/gfp2ram/workspace/hiveNeo4j";
    private static final String Hive_Neo4j_path = "C:\\Users\\gfp2ram\\Documents\\Neo4j\\default.graphdb";

//    private static Node nphNum;
//    private static Node Nplan;
//    //private static Node Ndate;
//    //private static Node Nstatus;
//    //private static Node Nbalance;
//    //private static Node Nimei;
//    private static Node Nregion;
    private static Relationship relation1, relation2;
    private static GraphDatabaseService graphDataService;

    public static enum NodeTypes implements Label {

        Phone, CallPlan, Region
    }

    public static enum RelTypes implements RelationshipType {

        PLN_STATUS, BELONGS_TO
    }

    public static void main(String args[]) {

        NodeCreationNeo4j sample = new NodeCreationNeo4j();
        HiveJdbcClient hive_jdbc = new HiveJdbcClient();
        sample.createDatabase(hive_jdbc.getCustomerDetailsList());

        //sample.removeData();
        //sample.shutdown();
    }

    private static void createDatabase(List<CustomerDetails> customerDetailses) {

        graphDataService = new GraphDatabaseFactory().newEmbeddedDatabase(Hive_Neo4j_path);
        Transaction transaction = graphDataService.beginTx();
        for (CustomerDetails customerDetails : customerDetailses) {
            Node nphNum = graphDataService.createNode(NodeTypes.Phone);
            Node nplan = graphDataService.createNode(NodeTypes.CallPlan);
            Node nregion = graphDataService.createNode(NodeTypes.Region);
            //Ndate = graphDataService.createNode();
            //Nstatus = graphDataService.createNode();
            //Nbalance = graphDataService.createNode();
            //Nimei = graphDataService.createNode();

            nphNum.setProperty("phone#", customerDetails.getPhNum());
            nphNum.setProperty("customername", "Test Customer");
            nphNum.setProperty("Location", "Arizona-Pheonix");

            nplan.setProperty("planname", customerDetails.getPlan());
            nplan.setProperty("plantype", "NighDiscount");
            nplan.setProperty("discountprice", "$0.45");

            nregion.setProperty("region", customerDetails.getRegion());
            nregion.setProperty("state", "AZ");
            nregion.setProperty("country", "USA");

            //Ndate.setProperty("date", customerDetails.getDate());
            //Nstatus.setProperty("status", customerDetails.getDate());
            //Nbalance.setProperty("balance", customerDetails.getDate());
            //Nimei.setProperty("imei", customerDetails.getDate());
            relation1 = nphNum.createRelationshipTo(nplan, RelTypes.PLN_STATUS);
            relation1.setProperty("DB Location", "Hive");

            relation2 = nplan.createRelationshipTo(nregion, RelTypes.BELONGS_TO);
            relation2.setProperty("Sandbox type", "Hortonworks");

            System.out.println(nphNum.getProperty("phone#").toString() + " IS IN THE PLAN " + nplan.getProperty("planname").toString() + " AT THE REGION " + nregion.getProperty("region").toString());
            transaction.success();
        }
    }

    /*void removeData() {
        Transaction transaction = graphDataService.beginTx();
        try {
            //Delete the Outgoing RelationShip first
            NphNum.getSingleRelationship(RelTypes.PLN_STATUS, Direction.OUTGOING).delete();
            NphNum.delete();
            Nplan.delete();
            //third.delete();
            System.out.println("Nodes are Removed Successfully");
            transaction.success();
        } finally {
            //Finish the Transaction
            transaction.finish();
        }
    }*/

   /* void shutdown() {
        graphDataService.shutdown();
        System.out.println("Neo4j DB is shutdown successfully");
    }*/
}
