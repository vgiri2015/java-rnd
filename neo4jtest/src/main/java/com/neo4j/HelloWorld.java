//Videos Available
//https://www.youtube.com/watch?v=80ApVocVClc
//https://www.youtube.com/watch?v=Q8QCNcHnVS8
//https://www.youtube.com/watch?v=1pKSWTzn0T4
//https://www.youtube.com/watch?v=mq4Evw0EsQE

//Cypher tools
//http://neo4j.com/blog/cypher-jdbc-tools-testing-results/
//Download -- > https://github.com/neo4j-contrib/neo4j-jdbc

package com.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * Created by Varatharajan Giri Ramanathan on 3/22/2015.
 */
public class HelloWorld {
    //First the Neo4j DB path is specified
    private static final String Neo4j_path="C:\\Users\\gfp2ram\\Documents\\Neo4j\\default.graphdb";

    //Creating Nodes, RelationShip, GraphDBService
    Node first;
    Node second;
    Node third;
    Relationship relation;
    GraphDatabaseService graphDataService;
    ExecutionResult queryresult;
     

    //List of RelationShipts between the Nodes
    private static enum RelTypes implements RelationshipType
    {
        KNOWS,DAUGHTEROF
    }

    public static void main(String[] args) {

        //Creating an Instance to make calls for the functions we have written below.
        HelloWorld hello = new HelloWorld();

        //Function calls
        hello.createDatabase();
        //hello.removeData();
        hello.shutdown();

    }
    //Always create the Database
    void createDatabase() {

        //Step : 1 == > Create GraphDatabaseService
        graphDataService = new GraphDatabaseFactory().newEmbeddedDatabase(Neo4j_path);

        //Step : 2 == > Begin Transaction
        Transaction transaction = graphDataService.beginTx();

        try {
            //Step : 3 == > Creation of Node and Set the Properties
            //createNode(), setProperty are the method

            first = graphDataService.createNode();
            first.setProperty("name","Giri Ramanathan");

            second = graphDataService.createNode();
            second.setProperty("name","Devikala Gilari");

            //Step : 4 == > Create Relationship

            relation = first.createRelationshipTo(first,RelTypes.KNOWS);
            relation.setProperty("relationship-type","knows");

            //Printing out the relationship between first and second nodes
            //System.out.println(first.getProperty("name").toString());
            //System.out.println(relation.getProperty("relationship-type").toString());
            //System.out.println(second.getProperty("name").toString());
            System.out.println(first.getProperty("name").toString()+"-->"+relation.getProperty("relationship-type").toString()+ "-- >" + second.getProperty("name").toString());

//            createNodeUsingCypher();
//
//            third.setProperty("name","Janani");
//            relation = third.createRelationshipTo(first,RelTypes.DAUGHTEROF);
//            relation.setProperty("relationship-type","DAUGHTEROF");
//
//            System.out.println("Is Child of" + third.getProperty("name").toString());

            //Step : 5 -- > Success the transaction
            transaction.success();
        }
        finally {
            //Step 6: ==> Finish Transaction
            transaction.finish();
        }
    }

    //Once the database is created, the data has to be removed
    void removeData() {
        //Step 1 : Again create the transaction
        Transaction transaction = graphDataService.beginTx();
        try {
            //Delete the Outgoing RelationShip first
            first.getSingleRelationship(RelTypes.KNOWS, Direction.OUTGOING).delete();
            first.delete();
            second.delete();
            //third.delete();
            System.out.println("Nodes are Removed Successfully");
            transaction.success();
        } finally {
            //Finish the Transaction
            transaction.finish();
        }
    }

    //The database instance has also be shutdown once created
    void shutdown() {
        //Shutdown the graphDataService
        graphDataService.shutdown();
        System.out.println("Neo4j DB is shutdown successfully");
    }

    void createNodeUsingCypher()
        {
        ExecutionEngine engine = new ExecutionEngine(graphDataService);
        Transaction transaction = graphDataService.beginTx();

        try {

            ExecutionResult queries = engine.execute("CREATE (third) RETURN (third)");
            java.util.List<String> columns = queries.columns();
            System.out.println(columns);
            System.out.println(queries);
            //String dumped = queries.dumpToString();
            //System.out.println(dumped);


            System.out.println("Third Node Created Successfully");
            }
        finally
            {
            transaction.finish();
            }
        }
    }