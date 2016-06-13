import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

/**
 * Created by gfp2ram on 12/31/2015.
 */
public class DBPediaParsing {

//    public static void main(String[] args) {
//        sparqlTest();
//    }
//
//    public static void sparqlTest() {
//        //String SOURCE = "http://www.opentox.org/api/1.1";
//        //String SOURCE = "http://dbpedia.org/sparql/";
//        //String NS = SOURCE + "#";
//        //create a model using reasoner
//        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
//
//        // read the RDF/XML file
//        //model.read( SOURCE, "RDF/XML" );
//        String queryString = "PREFIX dbo: <http://dbpedia.org/property/> PREFIX dbp: <http://dbpedia.org/resource/>\n" +
//                "\n" +
//                "SELECT * WHERE { ?film dbo:language ?language ; dbo:country dbp:India ; dbo:editing ?editing ; dbo:cinematography ?cinematography ; dbo:distributor ?distributor ; dbo:editing ?editing ; dbo:producer ?producer ; dbo:runtime ?runtime ; dbo:starring ?starring ; dbo:writer ?writer ; dbp:allLyrics ?allLyrics ; dbp:artist ?artist ; dbp:caption ?caption ; dbp:cinematography ?cinematography ; dbp:country ?country ; dbp:director ?director ; dbp:distributor ?distributor ; dbp:editing ?editing ; dbp:extra ?extra ; dbp:extraColumn ?extraColumn ; dbp:genre ?genre ; dbp:hasPhotoCollection ?hasPhotoCollection ; dbp:label ?label ; dbp:language ?language ; dbp:length ?length ; dbp:music ?music ; dbp:name ?name ; dbp:producer ?producer ; dbp:released ?released ; dbp:runtime ?runtime ; dbp:starring ?starring ; dbp:studio ?studio ; dbp:writer ?writer FILTER regex(?language, \"Tamil\") } LIMIT 100";
////        String queryString = "PREFIX dbo: <http://dbpedia.org/property/> PREFIX dbp: <http://dbpedia.org/resource/>\n" +
////                "\n" +
////                "SELECT DISTINCT ?film ?music ?producer ?runtime ?starring WHERE { ?film dbo:director dbp:K._V._Anand ; dbp:music ?music ; dbp:producer ?producer ; dbp:runtime ?runtime ; dbp:starring ?starring . }";
//        Query query = QueryFactory.create(queryString);
//        //QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
//        QueryExecution qexec = QueryExecutionFactory.create(query, model);
//        //((QueryEngineHTTP) qexec).addParam("timeout", "10000");
//        ResultSet results = qexec.execSelect();
//        ResultSetFormatter.out(System.out, results, query);
//        qexec.close();
//    }
public static void main (String args[]) {
    String SOURCE = "http://www.opentox.org/api/1.1";
    String NS = SOURCE + "#";
    //create a model using reasoner
    OntModel model1 = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
    //create a model which doesn't use a reasoner
    OntModel model2 = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM);

    // read the RDF/XML file
    model1.read( SOURCE, "RDF/XML" );
    model2.read( SOURCE, "RDF/XML" );
    //prints out the RDF/XML structure
    System.out.println(" ");

    // Create a new query
    String queryString =
            "PREFIX dbo: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/>\n" +
                    "\n" +
                    "SELECT DISTINCT ?film ?music ?producer ?runtime ?starring WHERE { ?film dbo:director dbpedia:K._V._Anand ; dbpedia:music ?music ; dbpedia:producer ?producer ; dbpedia:runtime ?runtime ; dbpedia:starring ?starring . }";
    Query query = QueryFactory.create(queryString);
    System.out.println("----------------------");
    System.out.println("Query Result Sheet");
    System.out.println("----------------------");
    System.out.println("Direct&Indirect Descendants (model1)");
    System.out.println("-------------------");

    // Execute the query and obtain results
    QueryExecution qe = QueryExecutionFactory.create(query, model1);
    ResultSet results =  qe.execSelect();

    // Output query results
    ResultSetFormatter.out(System.out, results, query);
    qe.close();
    System.out.println("----------------------");
    System.out.println("Only Direct Descendants");
    System.out.println("----------------------");

    // Execute the query and obtain results
    qe = QueryExecutionFactory.create(query, model2);
    results =  qe.execSelect();

    // Output query results
    ResultSetFormatter.out(System.out, results, query);
    qe.close();
}
}