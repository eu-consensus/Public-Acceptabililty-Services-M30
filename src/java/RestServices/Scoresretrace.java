/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestServices;

import Tools.CrawlersConnector;
import Tools.TopicAnalysisTools;
import Tools.databaseHandler;
import java.util.ArrayList;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author ViP
 */
@Path("scores/retrace")
public class Scoresretrace {

    @Context
    private UriInfo context;
    databaseHandler dbh;

    /**
     * Creates a new instance of Scoresretrace
     */
    public Scoresretrace() {
        dbh=new databaseHandler();
    }

    /**
     * Retrieves representation of an instance of RestServices.Scoresretrace
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        
        String res="";
        
        return res;
    }

    /**
     * PUT method for updating or creating an instance of Scoresretrace
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
    
    private void scoreIt(String scenario, int objective, String date){
        //Filling keywords tables
        CrawlersConnector ccn=new CrawlersConnector();
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
                    System.err.println("Policy not defined. Available scenarios: 'transportation' and 'biofuel'.");
                }
        
        //Calculating SOF
        String dateSince="";
        String dateUntill="";
        ArrayList<ArrayList<String>> allKeys=new ArrayList<ArrayList<String>>();
        if(scenario.equalsIgnoreCase("transportation")){
            allKeys.add(ccn.transportKeywords1);
            allKeys.add(ccn.transportKeywords2);
            allKeys.add(ccn.transportKeywords3);
            allKeys.add(ccn.transportKeywords4);
            allKeys.add(ccn.transportKeywords5);
            allKeys.add(ccn.transportKeywords6);
        }else if(scenario.trim().toLowerCase().equals("biofuel")){
            allKeys.add(ccn.biofuelKeywords1);
            allKeys.add(ccn.biofuelKeywords2);
            allKeys.add(ccn.biofuelKeywords3);
            allKeys.add(ccn.biofuelKeywords4);
        }
        ArrayList<ArrayList<Integer>> scenarioTweetsTotals=dbh.readScenarioTotal(allKeys,dateSince,dateUntill);
        TopicAnalysisTools tat = new TopicAnalysisTools();
                ArrayList<Double> sofs = new ArrayList<Double>();
                for (int i = 0; i < keys.size(); i++) {
                    sofs.add(tat.countFrequencyFast(scenarioTweetsTotals, objective-1, i));
                }
                Double sof=0.0;
                for (int i = 0; i < sofs.size(); i++) {
                    sof+=sofs.get(i);
                }
                if(sofs.size()>0) sof=sof/sofs.size();
    }
}
