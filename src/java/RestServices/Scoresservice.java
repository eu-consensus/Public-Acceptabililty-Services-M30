/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestServices;

import Entities.CustomStatus;
import Tools.BaselineAnalysisTools;
import Tools.CrawlersConnector;
import Tools.GlobalVarsStore;
import Tools.TopicAnalysisTools;
import Tools.databaseHandler;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.json.JSONException;

/**
 * REST Web Service
 *
 * @author ViP
 */
@Path("scores/service")
public class Scoresservice {

    @Context
    private UriInfo context;
    private databaseHandler dbh;

    /**
     * Creates a new instance of Scoresservice
     */
    public Scoresservice() {
        dbh = new databaseHandler();
        dbh.checkTables();
    }

    /**
     * Retrieves representation of an instance of RestServices.Scoresservice
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml(@QueryParam("scenario") String scenario, @QueryParam("objective") Integer objective) {
        //Reading source file
        CrawlersConnector ccn=new CrawlersConnector();
        ArrayList<CustomStatus> scenarioTweets = null;
        try {
            scenarioTweets = ccn.readScenario(scenario);
        } catch (IOException ex) {
            return "<xml><result>Error: "+ex.getMessage()+"</result></xml>";
        } catch (JSONException ex) {
            return "<xml><result>Error: "+ex.getMessage()+"</result></xml>";
        }
        System.out.println("Parsed " + scenarioTweets.size() + " documents.");
        ArrayList<String> objectiveNames=new ArrayList<String>();
        objectiveNames.add("Change in Level of Service");
        objectiveNames.add("% change of Accidents cost");
        objectiveNames.add("% change of Air pollution (external) cost");
        objectiveNames.add("% change of Noise (external) cost");
        objectiveNames.add("User convenience in using the RP system");
        objectiveNames.add("Availability of alternative routes and modes");

        //Calculating SOF
        DecimalFormat df = new DecimalFormat("#.####");
        ArrayList<String> keys=null;
        if(scenario.equalsIgnoreCase("transportation")){
            if(objective==1){keys=ccn.transportKeywords1;}
            else if(objective==2){keys=ccn.transportKeywords2;}
            else if(objective==3){keys=ccn.transportKeywords3;}
            else if(objective==4){keys=ccn.transportKeywords4;}
            else if(objective==5){keys=ccn.transportKeywords5;}
            else if(objective==6){keys=ccn.transportKeywords6;}
        }else if(scenario.trim().toLowerCase().equals("biofuel")){
            if(objective==1){keys=ccn.biofuelKeywords1;}
            else if(objective==2){keys=ccn.biofuelKeywords2;}
            else if(objective==3){keys=ccn.biofuelKeywords3;}
            else if(objective==4){keys=ccn.biofuelKeywords4;}
        }else{
            return "Policy not defined. Available scenarios: 'transportation' and 'biofuel'.";
        }
        System.out.println("Calculating Score Of Frequency...");
        TopicAnalysisTools tat = new TopicAnalysisTools();
        ArrayList<Double> sofs = new ArrayList<Double>();
        for (int i = 0; i < keys.size(); i++) {
            sofs.add(tat.countFrequency(scenarioTweets, keys.get(i)));
        }
        Double sof=0.0;
        for (int i = 0; i < sofs.size(); i++) {
            sof+=sofs.get(i);
        }
        if(sofs.size()>0) sof=sof/sofs.size();
        System.out.println("Score of Frequency for objective '" + objectiveNames.get(objective-1) + "' is " + df.format(sof));

        //Calculating SOS
        System.out.println("Calculating Score Of Sentiment...");
        BaselineAnalysisTools bat = new BaselineAnalysisTools();
        bat.prepareTools();
        ArrayList<CustomStatus> tweets=null;
        Double sos=0.0;
        ArrayList<Double> soses = new ArrayList<Double>();
        for (int i = 0; i < keys.size(); i++) {
            try {
                tweets=ccn.readKeyword(keys.get(i));
                soses.add(bat.SentiWordNetMeanAnalysis(tweets,keys.get(i)));
            } catch (IOException ex) { ex.printStackTrace(); soses.add(0.0);
            } catch (JSONException ex) { ex.printStackTrace();soses.add(0.0);
            } catch (ParseException ex) {
                 ex.printStackTrace();soses.add(0.0);
            }
        }
        for (int i = 0; i < soses.size(); i++) {
            sos+=soses.get(i);
        }
        if(soses.size()>0) sos=sos/soses.size();
        System.out.println("Score of Sentiment for objective '" + objectiveNames.get(objective-1) + "' is " + df.format(sos));

        //Storing to database
        System.out.println("Storing to database..");
        int sucCount=0;
        for (int i = 0; i < keys.size(); i++) {
            boolean suc = false;
            try{
                suc = dbh.insert(keys.get(i), soses.get(i), sofs.get(i));
            }catch(Exception ex){}
            System.out.println("Operation Completed for keyword: " + keys.get(i) + ".");
            if (suc) {
                sucCount++;
            }
        }
        return "<xml><result>"+sucCount+" of "+keys.size()+" records successfully stored in database.</result></xml>";
    }
    
    public static void main(String[] args) throws ParseException {
       /* String scenario="transportation";
        GlobalVarsStore.lexicon="wordnet";
        int objective=6;
        
        //Reading source file
        CrawlersConnector ccn=new CrawlersConnector();
        ArrayList<CustomStatus> scenarioTweets = null;
        try {
            scenarioTweets = ccn.readScenario(scenario);
        } catch (IOException ex) {
            System.out.println("<xml><result>Error: "+ex.getMessage()+"</result></xml>");
        } catch (JSONException ex) {
            System.out.println("<xml><result>Error: "+ex.getMessage()+"</result></xml>");
        }
        System.out.println("Parsed " + scenarioTweets.size() + " documents.");
        ArrayList<String> objectiveNames=new ArrayList<String>();
        objectiveNames.add("Change in Level of Service");
        objectiveNames.add("% change of Accidents cost");
        objectiveNames.add("% change of Air pollution (external) cost");
        objectiveNames.add("% change of Noise (external) cost");
        objectiveNames.add("User convenience in using the RP system");
        objectiveNames.add("Availability of alternative routes and modes");

        //Calculating SOF
        DecimalFormat df = new DecimalFormat("#.####");
        ArrayList<String> keys=null;
        if(scenario.equalsIgnoreCase("transportation")){
            if(objective==1){keys=ccn.transportKeywords1;}
            else if(objective==2){keys=ccn.transportKeywords2;}
            else if(objective==3){keys=ccn.transportKeywords3;}
            else if(objective==4){keys=ccn.transportKeywords4;}
            else if(objective==5){keys=ccn.transportKeywords5;}
            else if(objective==6){keys=ccn.transportKeywords6;}
        }else if(scenario.equalsIgnoreCase("biofuel")){
            if(objective==1){keys=ccn.biofuelKeywords1;}
            else if(objective==2){keys=ccn.biofuelKeywords2;}
            else if(objective==3){keys=ccn.biofuelKeywords3;}
            else if(objective==4){keys=ccn.biofuelKeywords4;}
        }
        System.out.println("Calculating Score Of Frequency...");
        TopicAnalysisTools tat = new TopicAnalysisTools();
        ArrayList<Double> sofs = new ArrayList<Double>();
        for (int i = 0; i < keys.size(); i++) {
            sofs.add(tat.countFrequency(scenarioTweets, keys.get(i)));
        }
        Double sof=0.0;
        for (int i = 0; i < sofs.size(); i++) {
            sof+=sofs.get(i);
        }
        if(sofs.size()>0) sof=sof/sofs.size();
        System.out.println("Score of Frequency for objective '" + objectiveNames.get(objective-1) + "' is " + df.format(sof));

        //Calculating SOS
        System.out.println("Calculating Score Of Sentiment...");
        BaselineAnalysisTools bat = new BaselineAnalysisTools();
        bat.prepareTools();
        ArrayList<CustomStatus> tweets=null;
        Double sos=0.0;
        ArrayList<Double> soses = new ArrayList<Double>();
        for (int i = 0; i < keys.size(); i++) {
            try {
                tweets=ccn.readKeyword(keys.get(i));
                soses.add(bat.SentiWordNetMeanAnalysis(tweets,keys.get(i)));
            } catch (IOException ex) { ex.printStackTrace(); soses.add(0.0);
            } catch (JSONException ex) { ex.printStackTrace();soses.add(0.0);
            }
        }
        for (int i = 0; i < soses.size(); i++) {
            sos+=soses.get(i);
        }
        if(soses.size()>0) sos=sos/soses.size();
        System.out.println("Score of Sentiment for objective '" + objectiveNames.get(objective-1) + "' is " + df.format(sos));*/
        
        //String tweetDate="Thu Jul 23 00:00:00 CEST 2015";
        //DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
        //Date result =  df.parse(tweetDate);
        //tweetDate = (result.getYear()+1900)+"-"+(result.getMonth()+1)+"-"+result.getDate();
        //System.out.println(tweetDate);
        
    String modelPath = DependencyParser.DEFAULT_MODEL;
    String taggerPath = "C:\\Users\\ViP\\Copy\\NTUA\\Code\\ConsensusPublicOpinion\\models\\english-left3words-distsim.tagger";

    for (int argIndex = 0; argIndex < args.length; ) {
      switch (args[argIndex]) {
        case "-tagger":
          taggerPath = args[argIndex + 1];
          argIndex += 2;
          break;
        case "-model":
          modelPath = args[argIndex + 1];
          argIndex += 2;
          break;
        default:
          throw new RuntimeException("Unknown argument " + args[argIndex]);
      }
    }

    String text = "I love apples and do not hate oranges";

    MaxentTagger tagger = new MaxentTagger(taggerPath);
    DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);

    DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
    for (List<HasWord> sentence : tokenizer) {
      List<TaggedWord> tagged = tagger.tagSentence(sentence);
      GrammaticalStructure gs = parser.predict(tagged);

      // Print typed dependencies
      System.out.println(gs);
      ArrayList<TypedDependency> cd=(ArrayList<TypedDependency>) gs.typedDependencies();
        for (int i = 0; i < cd.size(); i++) {
            System.out.println(String.format("%1$"+10+ "s", cd.get(i).gov().value())+"\t"+String.format("%1$"+10+ "s", cd.get(i).dep().value())+"\t"+cd.get(i).reln().getShortName()+"\t"+cd.get(i).reln().getLongName());
        }
    }
    }

    /**
     * PUT method for updating or creating an instance of Scoresservice
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
