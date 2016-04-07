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
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import org.json.JSONException;

/**
 * REST Web Service
 *
 * @author ViP
 */
@Path("scores/service/all")
public class Scoresserviceal {

    @Context
    private UriInfo context;
    ArrayList<ArrayList<Integer>> transportKeySums;
    ArrayList<ArrayList<Integer>> biofuelKeySums;


    /**
     * Creates a new instance of Scoresserviceal
     */
    public Scoresserviceal() {
    }

    /**
     * Retrieves representation of an instance of RestServices.Scoresserviceal
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        if(GlobalVarsStore.lock){
            /*if(((new Date()).getTime()-GlobalVarsStore.lockDate.getTime())> 57600000){
                // Recipient's email ID needs to be mentioned.
                String to = "vpsomak@mail.ntua.gr";

                // Sender's email ID needs to be mentioned
                String from = "invenemy@gmail.com";

                // Assuming you are sending email from localhost
                String host = "imap.googlemail.com";

                // Get system properties
                Properties properties = System.getProperties();

                // Setup mail server
                properties.setProperty("mail.smtp.host", host);

                // Get the default Session object.
                Session session = Session.getDefaultInstance(properties);

                try{
                   // Create a default MimeMessage object.
                   MimeMessage message = new MimeMessage(session);

                   // Set From: header field of the header.
                   message.setFrom(new InternetAddress(from));

                   // Set To: header field of the header.
                   message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                   // Set Subject: header field
                   message.setSubject("This is the Subject Line!");

                   // Now set the actual message
                   message.setText("This is actual message");

                   // Send message
                   Transport.send(message);
                   System.out.println("Sent message successfully....");
                }catch (MessagingException mex) {
                   mex.printStackTrace();
                }
            } */
            return "<xml><result>Score all already running...</result></xml>";
        }
        else{
            GlobalVarsStore.lock=true;
            GlobalVarsStore.lockDate=new Date();
            
            crawlerWorker workerTrans=new crawlerWorker("transportation");
            crawlerWorker workerBiof=new crawlerWorker("biofuel");
            workerTrans.start();
            workerBiof.start();
            if(workerTrans != null && workerTrans.isAlive()){
                try {
                    workerTrans.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Scoresserviceal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(workerBiof != null && workerBiof.isAlive()){
                try {
                    workerBiof.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Scoresserviceal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            String res="";
            res+=this.scoreIt("transportation",1);
            System.out.println("transportation 1 finished.");
            res+=this.scoreIt("transportation",2);
            System.out.println("transportation 2 finished.");
            res+=this.scoreIt("transportation",3);
            System.out.println("transportation 3 finished.");
            res+=this.scoreIt("transportation",4);
            System.out.println("transportation 4 finished.");
            res+=this.scoreIt("transportation",5);
            System.out.println("transportation 5 finished.");
            res+=this.scoreIt("transportation",6);
            System.out.println("transportation 6 finished.");
            res+=this.scoreIt("biofuel",1);
            System.out.println("biofuel 1 finished.");
            res+=this.scoreIt("biofuel",2);
            System.out.println("biofuel 2 finished.");
            res+=this.scoreIt("biofuel",3);
            System.out.println("biofuel 3 finished.");
            res+=this.scoreIt("biofuel",4);
            System.out.println(res);
            
            GlobalVarsStore.lock=false;
            if(GlobalVarsStore.lockBiofuel){
                System.out.println("Biofuel not finished.");
            }
            if(GlobalVarsStore.lockTransport){
                System.out.println("Transportation not finished.");
            }
            System.out.println("Service score all finished.");
            return "<xml><result>Service score all finished.</result></xml>";
        }
        
    }

    /**
     * PUT method for updating or creating an instance of Scoresserviceal
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
    
    private class crawlerWorker extends Thread {
        private String scenario;

        public crawlerWorker(String scenario) {
            this.scenario = scenario;
            GlobalVarsStore.publicDBh.checkTables();
        }
        
        public void run() {
            if(this.scenario.equals("transportation")){
                GlobalVarsStore.lockTransport=true;
            }else if(this.scenario.equals("biofuel")){
                GlobalVarsStore.lockBiofuel=true;
            }
            CrawlersConnector cc=new CrawlersConnector();
            if(this.scenario.equals("transportation"))
                transportKeySums=cc.readScenarioTotalsDistributed(this.scenario);
            else if(this.scenario.equals("biofuel"))
                biofuelKeySums=cc.readScenarioTotalsDistributed(this.scenario);
            if(this.scenario.equals("transportation")){
                GlobalVarsStore.lockTransport=false;
            }else if(this.scenario.equals("biofuel")){
                GlobalVarsStore.lockBiofuel=false;
            }
        }
    }
    
    private String scoreIt(String scenario, int objective){
            try{
                //databaseHandler dbh = new databaseHandler();
                GlobalVarsStore.publicDBh.checkTables();
                
                //Reading source file
                CrawlersConnector ccn=new CrawlersConnector();
                ArrayList<ArrayList<Integer>> scenarioTweetsTotals;
                if(scenario.equalsIgnoreCase("transportation")){
                    if(transportKeySums!=null){
                        scenarioTweetsTotals=transportKeySums;
                    }else{
                        ArrayList<ArrayList<String>> allKeys=new ArrayList<ArrayList<String>>();
                        allKeys.add(ccn.transportKeywords1);
                        allKeys.add(ccn.transportKeywords2);
                        allKeys.add(ccn.transportKeywords3);
                        allKeys.add(ccn.transportKeywords4);
                        allKeys.add(ccn.transportKeywords5);
                        allKeys.add(ccn.transportKeywords6);
                        Date now=new Date();
                        Calendar c = Calendar.getInstance(); 
                        c.setTime(now); 
                        c.add(Calendar.DATE, -2);
                        Date twoDays=c.getTime();
                        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
                        scenarioTweetsTotals=GlobalVarsStore.publicDBh.readScenarioTotal(allKeys,sf.format(twoDays),sf.format(now));
                    }
                }else if(scenario.equalsIgnoreCase("biofuel")){
                    if(transportKeySums!=null){
                        scenarioTweetsTotals=biofuelKeySums;
                    }else{
                        ArrayList<ArrayList<String>> allKeys=new ArrayList<ArrayList<String>>();
                        allKeys.add(ccn.biofuelKeywords1);
                        allKeys.add(ccn.biofuelKeywords2);
                        allKeys.add(ccn.biofuelKeywords3);
                        allKeys.add(ccn.biofuelKeywords4);
                        Date now=new Date();
                        Calendar c = Calendar.getInstance(); 
                        c.setTime(now); 
                        c.add(Calendar.DATE, -2);
                        Date twoDays=c.getTime();
                        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
                        scenarioTweetsTotals=GlobalVarsStore.publicDBh.readScenarioTotal(allKeys,sf.format(twoDays),sf.format(now));
                    }
                }else return "Error, wrong scenario '"+scenario+"'.";
                int scenarioGrandTotal=0;
                for (int i = 0; i < scenarioTweetsTotals.size(); i++) {
                    for (int j = 0; j < scenarioTweetsTotals.get(i).size(); j++) {
                        scenarioGrandTotal+=scenarioTweetsTotals.get(i).get(j);
                    }
                }
                System.out.println("Parsed " + scenarioGrandTotal + " documents.");
                ArrayList<String> objectiveNames=new ArrayList<String>();
                if(scenario.equals("transportation")){
                    objectiveNames.add("Change in Level of Service");
                    objectiveNames.add("% change of Accidents cost");
                    objectiveNames.add("% change of Air pollution (external) cost");
                    objectiveNames.add("% change of Noise (external) cost");
                    objectiveNames.add("Relative Operational Cost");
                    objectiveNames.add("Availability of alternative routes and modes");
                }else{
                    objectiveNames.add("Biodiversity");
                    objectiveNames.add("CO2 Emissions");
                    objectiveNames.add("Forest Land");
                    objectiveNames.add("Price of Food");
                }

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
                    sofs.add(tat.countFrequencyFast(scenarioTweetsTotals, objective-1, i));
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
                    } catch (ParseException ex) { ex.printStackTrace();soses.add(0.0);
                    } catch (NullPointerException ex) { ex.printStackTrace();soses.add(0.0);
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
                        suc = GlobalVarsStore.publicDBh.insert(keys.get(i), soses.get(i), sofs.get(i));
                    }catch(Exception ex){}
                    System.out.println("Operation Completed for keyword: " + keys.get(i) + ".");
                    if (suc) {
                        sucCount++;
                    }
                }
                return "<xml><result>"+sucCount+" of "+keys.size()+" records successfully stored in database for objective"+objectiveNames.get(objective-1)+".</result></xml>";
        }catch(Exception ex){
            Logger.getLogger(Scoresserviceall_old.class.getName()).log(Level.SEVERE, null, ex);
            return "<xml><result>Error"+ex.getLocalizedMessage()+"</result></xml>";
        }
    }
}
