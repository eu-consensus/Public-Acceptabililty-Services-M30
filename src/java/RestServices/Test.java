/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestServices;

import Entities.CustomStatus;
import Tools.CrawlersConnector;
import Tools.databaseHandler;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import org.json.JSONException;

/**
 * REST Web Service
 *
 * @author ViP
 */
@Path("test")
public class Test {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Test
     */
    public Test() {
    }

    /**
     * Retrieves representation of an instance of RestServices.Test
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    public String getText() {
        /*databaseHandler dbh=new databaseHandler();
        ArrayList<CustomStatus> tweets=dbh.readTweets("congestion", null);
        String res="";
        for (int i = 0; i < tweets.size(); i++) {
            res+=tweets.get(i).getClearText()+"\n"+tweets.get(i).getCreatedAt()+"\n";
        }
        return res;*/
        String res="";
        /*res+=this.scoreIt("transportation",1);
        System.out.println("transportation 1 finished.");
        res+=this.scoreIt("transportation",2);
        System.out.println("transportation 2 finished.");
        res+=this.scoreIt("transportation",3);
        System.out.println("transportation 3 finished.");
        res+=this.scoreIt("transportation",4);
        System.out.println("transportation 4 finished.");
        res+=this.scoreIt("transportation",5);
        System.out.println("transportation 5 finished.");*/
        //res+=this.scoreIt("transportation",6);
       /* System.out.println("transportation 6 finished.");
        res+=this.scoreIt("biofuel",1);
        System.out.println("biofuel 1 finished.");
        res+=this.scoreIt("biofuel",2);
        System.out.println("biofuel 2 finished.");
        res+=this.scoreIt("biofuel",3);
        System.out.println("biofuel 3 finished.");
        res+=this.scoreIt("biofuel",4);
        System.out.println("Score all finished.");*/
        res+=System.getProperty("user.dir");
        return res;
    }

    /**
     * PUT method for updating or creating an instance of Test
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("text/plain")
    public void putText(String content) {
    }
    
    private String scoreIt(String scenario, int objective){
        ArrayList<CustomStatus> scenarioTweets;
        CrawlersConnector ccn=new CrawlersConnector();
        try {
            scenarioTweets = ccn.readScenario(scenario);
        } catch (IOException ex) {
            return "<xml><result>Error: "+ex.getMessage()+"</result></xml>";
        } catch (JSONException ex) {
            return "<xml><result>Error: "+ex.getMessage()+"</result></xml>";
        }
        ArrayList<String> objectiveNames=new ArrayList<String>();
        if(scenario.equals("transportation")){
            objectiveNames.add("Change in Level of Service");
            objectiveNames.add("% change of Accidents cost");
            objectiveNames.add("% change of Air pollution (external) cost");
            objectiveNames.add("% change of Noise (external) cost");
            objectiveNames.add("User convenience in using the RP system");
            objectiveNames.add("Availability of alternative routes and modes");
        }else{
            objectiveNames.add("Biodiversity");
            objectiveNames.add("CO2 Emissions");
            objectiveNames.add("Forest Land");
            objectiveNames.add("Price of Food");
        }
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
        ArrayList<CustomStatus> tweets=null;
        for (int i = 0; i < keys.size(); i++) {
            try {
                tweets=ccn.readKeyword(keys.get(i));
            } catch (IOException ex) { ex.printStackTrace();
            } catch (JSONException ex) { ex.printStackTrace();
            } catch (ParseException ex) { ex.printStackTrace();
            }
        }
        String res="\n\n"+scenario+" - "+objective+"\n\n";
        for (int i = 0; i < tweets.size(); i++) {
            res+=tweets.get(i).getClearText()+"\n"+tweets.get(i).getCreatedAt()+"\n";
        }
        return res;
    }
}
