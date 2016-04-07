/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import Crawlers.TwitterCrawler;
import Entities.CustomStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 *
 * @author ViP
 */
public class CrawlersConnector {
    public ArrayList<String> transportKeywords1;
    public ArrayList<String> transportKeywords2;
    public ArrayList<String> transportKeywords3;
    public ArrayList<String> transportKeywords4;
    public ArrayList<String> transportKeywords5;
    public ArrayList<String> transportKeywords6;
    public ArrayList<String> biofuelKeywords1;
    public ArrayList<String> biofuelKeywords2;
    public ArrayList<String> biofuelKeywords3;
    public ArrayList<String> biofuelKeywords4;
    ArrayList<CustomStatus> trueRes;
    private static final int dateDiff=-2;

    public CrawlersConnector() {
        //Change in Level of Service
        transportKeywords1=new ArrayList<String>();
        transportKeywords1.add("congestion");                               //01
        transportKeywords1.add("traffic jam");                              //02
        transportKeywords1.add("traffic");                                  //03
        transportKeywords1.add("road capacity");                            //04
        transportKeywords1.add("low speed");                                //05
        transportKeywords1.add("perceived safety");                         //06
        transportKeywords1.add("road safety");                              //07
        transportKeywords1.add("driving safety");                           //08
        transportKeywords1.add("traffic comfort");                          //09
        
        //% change of Accidents cost
        transportKeywords2=new ArrayList<String>();
        transportKeywords2.add("road accident");                            //10
        transportKeywords2.add("car accident");                             //11
        transportKeywords2.add("vehicle accident");                         //12
        transportKeywords2.add("road kill");                                //13
        transportKeywords2.add("roadkill");                                 //14
        transportKeywords2.add("road injury");                              //15
        transportKeywords2.add("accident probability");                     //16
        transportKeywords2.add("road safety");                              //17
        transportKeywords2.add("driving safety");                           //18
        transportKeywords2.add("vehicle insurance");                        //19
        transportKeywords2.add("car insurance");                            //20
        
        //% change of Air pollution (external) cost
        transportKeywords3=new ArrayList<String>();
        transportKeywords3.add("air pollution");                            //21
        transportKeywords3.add("car pollution");                            //22
        transportKeywords3.add("vehicle pollution");                        //23
        transportKeywords3.add("car emissions");                            //24
        transportKeywords3.add("vehicle emissions");                        //25
        transportKeywords3.add("traffic environmental impacts");            //26
        transportKeywords3.add("gaseous pollutants");                       //27
        transportKeywords3.add("health congestion");                        //28
        transportKeywords3.add("traffic climate");                          //29
        transportKeywords3.add("traffic health");                           //30
        transportKeywords3.add("greenhouse effect");                        //31

        //% change of Noise (external) cost
        transportKeywords4=new ArrayList<String>();
        transportKeywords4.add("traffic noise");                            //32
        transportKeywords4.add("noise pollution");                          //33
        transportKeywords4.add("sound pollution");                          //34
        transportKeywords4.add("traffic vibrations");                       //35
        transportKeywords4.add("traffic environmental impacts");            //36

        //Relative Operational Cost
        transportKeywords5=new ArrayList<String>();
        transportKeywords5.add("toll road collection cost");                //37
        transportKeywords5.add("toll road operating cost");                 //38
        transportKeywords5.add("toll road management cost");                //39
        transportKeywords5.add("toll road administration cost");            //40
        transportKeywords5.add("toll road oversight cost");                 //41
        
       /* //User convenience in using the RP system
        transportKeywords5=new ArrayList<String>();
        transportKeywords5.add("toll payment");                             //37
        transportKeywords5.add("toll collection");                          //38
        transportKeywords5.add("toll plaza");                               //39
        transportKeywords5.add("toll traffic");                             //40
        transportKeywords5.add("toll congestion");                          //41
        transportKeywords5.add("queue blocking");                           //42
        transportKeywords5.add("automatic tolls");                          //43
        transportKeywords5.add("electronic tolls");                         //44*/

        //Availability of alternative routes and modes
        transportKeywords6=new ArrayList<String>();
        transportKeywords6.add("toll payment");                             //45
        transportKeywords6.add("toll roads monopoly");                      //46
        transportKeywords6.add("toll road alternative");                    //47
        transportKeywords6.add("toll road alternatives");                   //48
        transportKeywords6.add("toll traffic divert");                      //49
        transportKeywords6.add("non toll roads");                           //50
        transportKeywords6.add("national road network");                    //51
        transportKeywords6.add("rural network");                            //52
        transportKeywords6.add("toll exceptions");                          //53
        
        //biodiversity
        biofuelKeywords1=new ArrayList<String>(); 
        biofuelKeywords1.add("biodiversity");                               //54
        biofuelKeywords1.add("riparian areas");                             //55
        biofuelKeywords1.add("degraded land");                              //56
        biofuelKeywords1.add("land degration");                             //57
        biofuelKeywords1.add("soil erosion");                               //58
        biofuelKeywords1.add("yield intensification");                      //59
        biofuelKeywords1.add("agricultural intensification");               //60
        biofuelKeywords1.add("loss species");                               //61 
        biofuelKeywords1.add("non-renewable water resources");              //62
        
        //CO2 Emissions
        biofuelKeywords2=new ArrayList<String>();
        biofuelKeywords2.add("climate change mitigation");                  //63
        biofuelKeywords2.add("environmental pollution fuel");               //64
        biofuelKeywords2.add("GHG emissions");                              //65
        biofuelKeywords2.add("ILUC");                                       //66
        biofuelKeywords2.add("indirect land use change");                   //67
        biofuelKeywords2.add("land use change");                            //68
        biofuelKeywords2.add("land based biofuels");                        //69
        biofuelKeywords2.add("first generation biofuels");                  //70
        biofuelKeywords2.add("advanced biofuels");                          //71
        biofuelKeywords2.add("second generation biofuels");                 //72
        
        //Forest Land
        biofuelKeywords3=new ArrayList<String>();
        biofuelKeywords3.add("conversion forest");                          //73
        biofuelKeywords3.add("soil erosion");                               //74
        biofuelKeywords3.add("deforestation");                              //75
        biofuelKeywords3.add("forest land");                                //76
        biofuelKeywords3.add("forest");                                     //77
        biofuelKeywords3.add("trees");                                      //78
        biofuelKeywords3.add("degraded land");                              //79
        
        //Price of Food
        biofuelKeywords4=new ArrayList<String>();
        biofuelKeywords4.add("food crops");                                 //80
        biofuelKeywords4.add("energy crops");                               //81
        biofuelKeywords4.add("agricultural crops");                         //82
        biofuelKeywords4.add("land rights");                                //83
        biofuelKeywords4.add("land conflicts");                             //84
        biofuelKeywords4.add("land grabbing");                              //85
        biofuelKeywords4.add("food prices");                                //86
        biofuelKeywords4.add("food security");                              //87
        biofuelKeywords4.add("food availability");                          //88
        biofuelKeywords4.add("animal feed co-products");                    //89
    }
    
    public ArrayList<ArrayList<Integer>> readScenarioTotalsDistributed(String scenario){
        Date now=new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(now); 
        c.add(Calendar.DATE, dateDiff); 
        ArrayList<ArrayList<Integer>> sums=new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<String>> allkeys=new ArrayList<ArrayList<String>>();
        if(scenario.trim().toLowerCase().equals("transportation")){
            allkeys.add(transportKeywords1);
            allkeys.add(transportKeywords2);
            allkeys.add(transportKeywords3);
            allkeys.add(transportKeywords4);
            allkeys.add(transportKeywords5);
            allkeys.add(transportKeywords6);
        }else if(scenario.trim().toLowerCase().equals("biofuel")){
            allkeys.add(biofuelKeywords1);
            allkeys.add(biofuelKeywords2);
            allkeys.add(biofuelKeywords3);
            allkeys.add(biofuelKeywords4);
        }
        ExecutorService pool = Executors.newFixedThreadPool(allkeys.size());
        ArrayList<Future<ArrayList<Integer>>> workers=new ArrayList<Future<ArrayList<Integer>>>();
        for (int k = 0; k < allkeys.size(); k++) {
            int keyIndex = k%6;
            workers.add(pool.submit(new crawlCallable(allkeys.get(k),keyIndex)));
        }
        for (int i = 0; i < workers.size(); i++) {
            try{
                sums.add(workers.get(i).get());
            }catch(InterruptedException | ExecutionException ex){
                Logger.getLogger(CrawlersConnector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Scenario totals returned.");
        return sums;
    }

    public ArrayList<CustomStatus> readScenarioDistributed(String scenario) throws MalformedURLException, IOException, JSONException{
        trueRes=new ArrayList<CustomStatus>();
        ArrayList<ArrayList<String>> allkeys=new ArrayList<ArrayList<String>>();
        Date now=new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(now); 
        c.add(Calendar.DATE, dateDiff);  
        if(scenario.trim().toLowerCase().equals("transportation")){
            allkeys.add(transportKeywords1);
            allkeys.add(transportKeywords2);
            allkeys.add(transportKeywords3);
            allkeys.add(transportKeywords4);
            allkeys.add(transportKeywords5);
            allkeys.add(transportKeywords6);
        }else if(scenario.trim().toLowerCase().equals("biofuel")){
            allkeys.add(biofuelKeywords1);
            allkeys.add(biofuelKeywords2);
            allkeys.add(biofuelKeywords3);
            allkeys.add(biofuelKeywords4);
        }
        ArrayList<readWorker> workers=new ArrayList<readWorker>();
        for (int k = 0; k < allkeys.size(); k++) {
            int keyIndex = k%6;
            workers.add(new readWorker(allkeys.get(k),keyIndex));
        }
        for (int i = 0; i < workers.size(); i++) {
            workers.get(i).start();
        }
        for (int i = 0; i < workers.size(); i++) {
            if(workers.get(i)!=null && workers.get(i).isAlive())
                try {
                    workers.get(i).join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlersConnector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Scenario returned.");
        return trueRes;
    }
    
    public ArrayList<CustomStatus> readScenario(String scenario) throws MalformedURLException, IOException, JSONException{
        //databaseHandler dbh=new databaseHandler();
        BaselineAnalysisTools bat=new BaselineAnalysisTools();
        ArrayList<CustomStatus> res;
        ArrayList<CustomStatus> trueRes=new ArrayList<CustomStatus>();
        ArrayList<String> allkeys=new ArrayList<String>();
        Date now=new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(now); 
        c.add(Calendar.DATE, dateDiff);
        Date before6months=c.getTime();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        if(scenario.trim().toLowerCase().equals("transportation")){
            allkeys.addAll(transportKeywords1);
            allkeys.addAll(transportKeywords2);
            allkeys.addAll(transportKeywords3);
            allkeys.addAll(transportKeywords4);
            allkeys.addAll(transportKeywords5);
            allkeys.addAll(transportKeywords6);
        }else if(scenario.trim().toLowerCase().equals("biofuel")){
            allkeys.addAll(biofuelKeywords1);
            allkeys.addAll(biofuelKeywords2);
            allkeys.addAll(biofuelKeywords3);
            allkeys.addAll(biofuelKeywords4);
        }
        for (int i = 0; i < allkeys.size(); i++) {
            try{
                before6months=c.getTime();
                System.out.println(allkeys.get(i)+" started.");
                //res=dbh.readTweets(allkeys.get(i),null);
                res=GlobalVarsStore.publicDBh.readTweets(allkeys.get(i),null);
                boolean cancel=false;
                if(res==null){res=new ArrayList<CustomStatus>();}
                else if(res.size()>0){
                    try{
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        before6months=formatter.parse(res.get(0).getCreatedAt());
                        int days = Days.daysBetween(new DateTime(before6months), new DateTime(now)).getDays();
                        if(days>2){before6months=c.getTime();}
                        else if(days<1){
                            cancel=true;
                            System.out.println(allkeys.get(i)+" canceled.");
                        }
                    }catch(Exception ex){ex.printStackTrace(); before6months=c.getTime();}
                }
                if(!cancel){
                    TwitterCrawler tc=new TwitterCrawler();
                    System.out.println(allkeys.get(i)+" -filter:links since:"+sf.format(before6months));
                    List<CustomStatus> tweetsCrawled=tc.safeSearch((allkeys.get(i)+" -filter:links").replace(" ","%20"),sf.format(before6months));
                    for (int j = 0; j < tweetsCrawled.size(); j++) {
                        if(!tweetsCrawled.get(j).getClearText().contains("URL")) res.add(tweetsCrawled.get(j));
                    }
                    res=clearDuplicateStatuses(res);
                    System.out.println("Set cleared.");
                    for (int j = 0; j < res.size(); j++) {
                        try{
                            CustomStatus cur=res.get(j);
                            cur.setPolarity(bat.SentiWordNetMeanAnalysisSingle(cur));
                            GlobalVarsStore.publicDBh.insertTweet(cur.getKeyword(), cur.getText(), cur.getPolarity(), cur.getId(), cur.getCreatedAt());
                            trueRes.add(cur);
                        }catch(Exception exe){exe.printStackTrace();}
                    }
                }else{
                    trueRes.addAll(res);
                }
            }catch(Exception ex){ex.printStackTrace();}
        }
        System.out.println("Scenario returned.");
        return trueRes;
    }
    
    public ArrayList<String> readScenario_legacy(String scenario) throws MalformedURLException, IOException, JSONException{
        ArrayList<String> res=new ArrayList<String>();
        ArrayList<String> allkeys=new ArrayList<String>();
        if(scenario.trim().toLowerCase().equals("transportation")){
            allkeys.addAll(transportKeywords1);
            allkeys.addAll(transportKeywords2);
            allkeys.addAll(transportKeywords3);
            allkeys.addAll(transportKeywords4);
            allkeys.addAll(transportKeywords5);
            allkeys.addAll(transportKeywords6);
        }else if(scenario.trim().toLowerCase().equals("biofuel")){
            allkeys.addAll(biofuelKeywords1);
            allkeys.addAll(biofuelKeywords2);
            allkeys.addAll(biofuelKeywords3);
            allkeys.addAll(biofuelKeywords4);
        }
        //String keychain="";
        for (int i = 0; i < allkeys.size(); i++) {
            Date now=new Date();
            Calendar c = Calendar.getInstance(); 
            c.setTime(now); 
            c.add(Calendar.DATE, -2);
            Date before6months=c.getTime();
            String url="http://consensus.atc.gr:8080/mongo-handler/rest/search?keywords="+allkeys.get(i)+"-link&time="+before6months.getTime();
            url=url.replace(" ","%20");
            InputStream is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String text="";
            String line="";
            while((line=rd.readLine()) != null){
                text+=line;
            }

            JSONArray as=new JSONArray(text);
            for (int j = 0; j < as.length(); j++) {
                res.add(((JSONObject)as.get(j)).getString("title"));
            }
        }
        //keychain=keychain.substring(0,keychain.length()-1);
        
        
        return clearDuplicates(clearURLS(res));
    }
    
    public ArrayList<CustomStatus> readObjective(String scenario,int objective) throws MalformedURLException, IOException, JSONException, ParseException{
        //databaseHandler dbh=new databaseHandler();
        BaselineAnalysisTools bat=new BaselineAnalysisTools();
        ArrayList<CustomStatus> res;
        TwitterCrawler tc=new TwitterCrawler();
        ArrayList<CustomStatus> trueRes=new ArrayList<CustomStatus>();
        ArrayList<String> allkeys=new ArrayList<String>();
        Date now=new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(now); 
        c.add(Calendar.DATE, dateDiff);
        Date before6months=c.getTime();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        if(scenario.trim().toLowerCase().equals("transportation")){
            if(objective==1){allkeys=transportKeywords1;}
            else if(objective==2){allkeys=transportKeywords2;}
            else if(objective==3){allkeys=transportKeywords3;}
            else if(objective==4){allkeys=transportKeywords4;}
            else if(objective==5){allkeys=transportKeywords5;}
            else if(objective==6){allkeys=transportKeywords6;}
        }else if(scenario.trim().toLowerCase().equals("biofuel")){
            if(objective==1){allkeys=biofuelKeywords1;}
            else if(objective==2){allkeys=biofuelKeywords2;}
            else if(objective==3){allkeys=biofuelKeywords3;}
            else if(objective==4){allkeys=biofuelKeywords4;}
        } 
        for (int i = 0; i < allkeys.size(); i++) {
            before6months=c.getTime();
            res=GlobalVarsStore.publicDBh.readTweets(allkeys.get(i),null);
            boolean cancel=false;
            if(res==null){res=new ArrayList<CustomStatus>();}
            else if(res.size()>0){
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                before6months=formatter.parse(res.get(0).getCreatedAt());
                int days = Days.daysBetween(new DateTime(before6months), new DateTime(now)).getDays();
                if(days>2){before6months=c.getTime();}
                else if(days<1) cancel=true;
            }
            if(!cancel){
                System.out.println(allkeys.get(i)+" -filter:links since:"+sf.format(before6months));
                List<CustomStatus> tweetsCrawled=tc.safeSearch((allkeys.get(i)+" -filter:links").replace(" ","%20"),sf.format(before6months));
                for (int j = 0; j < tweetsCrawled.size(); j++) {
                    if(!tweetsCrawled.get(j).getClearText().contains("URL")) res.add(tweetsCrawled.get(j));
                }
                res=clearDuplicateStatuses(res);
                for (int j = 0; j < res.size(); j++) {
                    CustomStatus cur=res.get(j);
                    cur.setPolarity(bat.SentiWordNetMeanAnalysisSingle(cur));
                    GlobalVarsStore.publicDBh.insertTweet(cur.getKeyword(), cur.getText(), cur.getPolarity(), cur.getId(), cur.getCreatedAt());
                    trueRes.add(cur);
                }
            }else{
                trueRes.addAll(res);
            }
        }
        return clearDuplicateStatuses(trueRes);
    }
    
    public ArrayList<String> readObjective_legacy(String scenario,int objective) throws MalformedURLException, IOException, JSONException{
        ArrayList<String> res=new ArrayList<String>();
        ArrayList<String> allkeys=new ArrayList<String>();
        if(scenario.trim().toLowerCase().equals("transportation")){
            if(objective==1){allkeys=transportKeywords1;}
            else if(objective==2){allkeys=transportKeywords2;}
            else if(objective==3){allkeys=transportKeywords3;}
            else if(objective==4){allkeys=transportKeywords4;}
            else if(objective==5){allkeys=transportKeywords5;}
            else if(objective==6){allkeys=transportKeywords6;}
        }else if(scenario.trim().toLowerCase().equals("biofuel")){
            if(objective==1){allkeys=biofuelKeywords1;}
            else if(objective==2){allkeys=biofuelKeywords2;}
            else if(objective==3){allkeys=biofuelKeywords3;}
            else if(objective==4){allkeys=biofuelKeywords4;}
        } 
        
        String keychain="";
        for (int i = 0; i < allkeys.size(); i++) {
            keychain+=allkeys.get(i)+",";
        }
        keychain=keychain.substring(0,keychain.length()-1);
        Date now=new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(now); 
        c.add(Calendar.DATE, -2);
        Date before6months=c.getTime();
        String url="http://consensus.atc.gr:8080/mongo-handler/rest/search?keywords="+keychain+"&time="+before6months.getTime();
        url=url.replace(" ","%20");
        InputStream is = new URL(url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String text="";
        String line="";
        while((line=rd.readLine()) != null){
            text+=line;
        }
        
        JSONArray as=new JSONArray(text);
        for (int i = 0; i < as.length(); i++) {
            res.add(((JSONObject)as.get(i)).getString("title"));
        }
        
        return clearDuplicates(clearURLS(res));
    }
    
    public ArrayList<String> readKeyword_legacy(String keyword) throws MalformedURLException, IOException, JSONException{
        ArrayList<String> res=new ArrayList<String>();
        Date now=new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(now); 
        c.add(Calendar.DATE, -2);
        Date before6months=c.getTime();
        String url="http://consensus.atc.gr:8080/mongo-handler/rest/search?keywords="+keyword+"&time="+before6months.getTime();
        url=url.replace(" ","%20");
        InputStream is = new URL(url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String text="";
        String line="";
        while((line=rd.readLine()) != null){
            text+=line;
        }
        
        JSONArray as=new JSONArray(text);
        for (int i = 0; i < as.length(); i++) {
            res.add(((JSONObject)as.get(i)).getString("title"));
        }
        
        return clearDuplicates(clearURLS(res));
    }
    
    public ArrayList<CustomStatus> readKeyword(String keyword) throws MalformedURLException, IOException, JSONException, ParseException{
        //databaseHandler dbh=new databaseHandler();
        BaselineAnalysisTools bat=new BaselineAnalysisTools();
        ArrayList<CustomStatus> res;
        TwitterCrawler tc=new TwitterCrawler();
        trueRes=new ArrayList<CustomStatus>();
        //String keychain=allkeys.get(0);
        Date now=new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(now); 
        c.add(Calendar.DATE, dateDiff);
        Date before6months=c.getTime();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        res=GlobalVarsStore.publicDBh.readTweets(keyword,null);
        boolean cancel=false;
        if(res==null){res=new ArrayList<CustomStatus>();}
        else if(res.size()>0){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            before6months=formatter.parse(res.get(0).getCreatedAt());
            int days = Days.daysBetween(new DateTime(before6months), new DateTime(now)).getDays();
            if(days>2){before6months=c.getTime();}
            else if(days<1) cancel=true;
        }
        if(!cancel){
            List<CustomStatus> tweetsCrawled=null;
            System.out.println(keyword+" -filter:links since:"+sf.format(before6months));
            try{
            tweetsCrawled=tc.safeSearch((keyword+" -filter:links").replace(" ","%20"),sf.format(before6months));
            for (int j = 0; j < tweetsCrawled.size(); j++) {
                if(!tweetsCrawled.get(j).getClearText().contains("URL")) res.add(tweetsCrawled.get(j));
            }
            res=clearDuplicateStatuses(res);
            for (int i = 0; i < res.size(); i++) {
                CustomStatus cur=res.get(i);
                cur.setPolarity(bat.SentiWordNetMeanAnalysisSingle(cur));
                GlobalVarsStore.publicDBh.insertTweet(cur.getKeyword(), cur.getText(), cur.getPolarity(), cur.getId(), cur.getCreatedAt());
                trueRes.add(cur);
            }
            return trueRes;
            }catch(NullPointerException exe){
                int tries=0;
                System.out.println("Communication error with database or crawler. Retrying in 60 seconds.");
                while(tweetsCrawled==null){
                    tries++;
                    System.out.println("Try: "+tries);
                    tweetsCrawled=tc.safeSearch((keyword+" -filter:links").replace(" ","%20"),sf.format(before6months));
                }
                for (int j = 0; j < tweetsCrawled.size(); j++) {
                    if(!tweetsCrawled.get(j).getClearText().contains("URL")) res.add(tweetsCrawled.get(j));
                }
                res=clearDuplicateStatuses(res);
                for (int i = 0; i < res.size(); i++) {
                    CustomStatus cur=res.get(i);
                    cur.setPolarity(bat.SentiWordNetMeanAnalysisSingle(cur));
                    GlobalVarsStore.publicDBh.insertTweet(cur.getKeyword(), cur.getText(), cur.getPolarity(), cur.getId(), cur.getCreatedAt());
                    trueRes.add(cur);
                }
                return trueRes;
            }
        }else{
            return res;
        }
    }
    
    private ArrayList<String> clearURLS(ArrayList<String> data){
        for (int i = 0; i < data.size(); i++) {
            String cur=data.get(i);
            if(cur.indexOf("http://")>=0 || cur.indexOf("https://")>=0){
                data.remove(i);
                i--;
            }
        }
        return data;
    }
    
    private ArrayList<String> clearDuplicates(ArrayList<String> data){
        ArrayList<String> unique=new ArrayList<String>();
        boolean matchFound=false;
        if(data.size()>0){
            unique.add(data.get(0));
            for (int i = 1; i < data.size(); i++) {
                String cur=data.get(i);
                matchFound=false;
                for (int j = 0; j < unique.size() && !matchFound; j++) {
                    matchFound=cur.trim().equalsIgnoreCase(unique.get(j).trim());
                    if(!matchFound) matchFound=testSimilar(cur,unique.get(j));
                }
                if(!matchFound) unique.add(cur);
            }
        }
        return unique;
    }
    
    private ArrayList<CustomStatus> clearDuplicateStatuses(ArrayList<CustomStatus> data){
        ArrayList<CustomStatus> unique=new ArrayList<CustomStatus>();
        boolean matchFound=false;
        if(data.size()>0){
            unique.add(data.get(0));
            for (int i = 1; i < data.size(); i++) {
                //System.out.println(data.get(i).getClearText());
                CustomStatus cur=data.get(i);
                matchFound=false;
                for (int j = 0; j < unique.size() && !matchFound; j++) {
                    if(cur.getHash()==unique.get(j).getHash()){
                        matchFound=cur.getClearText().trim().equals(unique.get(j).getClearText().trim());
                    }
                    if(!matchFound) matchFound=cur.getId()==unique.get(j).getId();
                    //if(!matchFound) matchFound=testSimilar(cur.getClearText(),unique.get(j).getClearText());
                }
                if(!matchFound){ /*System.out.println("Added");*/ unique.add(cur);}
                //else System.out.println("Matched");
            }
        }
        return unique;
    }

    private boolean testSimilar(String str1, String str2) {
        return StringUtils.getLevenshteinDistance(str1.trim().toLowerCase(), str2.trim().toLowerCase())<40;
    }
    
    private class crawlCallable implements  Callable{
        ArrayList<String> keys;
        int keyIndex;

        public crawlCallable(ArrayList<String> keys, int keyIndex) {
            this.keys = keys;
            this.keyIndex = keyIndex;
        }
        
        @Override
        public ArrayList<Integer> call() throws Exception {
            ArrayList<Integer> keyTotals=new ArrayList<Integer>();
            //databaseHandler dbh=new databaseHandler();
            Calendar c = Calendar.getInstance();
            BaselineAnalysisTools bat=new BaselineAnalysisTools();
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
            Date now=new Date();
            for (int i = 0; i < this.keys.size(); i++) {
                try{
                    keyTotals.add(0);
                    Date before6months=c.getTime();
                    System.out.println(this.keys.get(i)+" started.");
                    ArrayList<CustomStatus> res=GlobalVarsStore.publicDBh.readTweets(this.keys.get(i),null);
                    boolean cancel=false;
                    if(res==null){res=new ArrayList<CustomStatus>();}
                    else if(res.size()>0){
                        try{
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            before6months=formatter.parse(res.get(0).getCreatedAt());
                            int days = Days.daysBetween(new DateTime(before6months), new DateTime(now)).getDays();
                            if(days>2){before6months=c.getTime();}
                            else if(days<1){
                                cancel=true;
                                System.out.println(this.keys.get(i)+" canceled.");
                            }
                        }catch(Exception ex){ex.printStackTrace(); before6months=c.getTime();}
                    }
                    if(!cancel){
                        int key;
                        if(this.keyIndex<5){
                            key=this.keyIndex;
                        }else{
                            key=5;
                        }
                        TwitterCrawler tc=new TwitterCrawler(GlobalVarsStore.tweeterPKeys[key],GlobalVarsStore.tweeterSKeys[key],GlobalVarsStore.tweeterPTokens[key],GlobalVarsStore.tweeterSTokens[key]);
                        System.out.println(this.keys.get(i)+" -filter:links since:"+sf.format(before6months));
                        List<CustomStatus> tweetsCrawled=tc.safeSearch((this.keys.get(i)+" -filter:links").replace(" ","%20"),sf.format(before6months));
                        for (int j = 0; j < tweetsCrawled.size(); j++) {
                            if(!tweetsCrawled.get(j).getClearText().contains("URL")) res.add(tweetsCrawled.get(j));
                        }
                        res=clearDuplicateStatuses(res);
                        System.out.println("Set cleared.");
                        int tweetsInserted=0;
                        for (int j = 0; j < res.size(); j++) {
                            try{
                                CustomStatus cur=res.get(j);
                                cur.setPolarity(bat.SentiWordNetMeanAnalysisSingle(cur));
                                GlobalVarsStore.publicDBh.insertTweet(cur.getKeyword(), cur.getText(), cur.getPolarity(), cur.getId(), cur.getCreatedAt());
                                tweetsInserted++;
                            }catch(Exception exe){exe.printStackTrace();}
                        }
                        System.out.println(tweetsInserted+" tweets inserted to database for keyword "+this.keys.get(i)+".");
                        keyTotals.set(i,res.size());
                    }else{
                        keyTotals.set(i,res.size());
                    }
                }catch(Exception ex){ex.printStackTrace();}
            }
            return keyTotals;
        }
    }
    
    private class readWorker extends Thread {
        ArrayList<String> keys;
        int keyIndex;

        public readWorker(ArrayList<String> keys, int keyIndex) {
            this.keys = keys;
            this.keyIndex = keyIndex;
        }
        
        public void run() {
            //databaseHandler dbh=new databaseHandler();
            Calendar c = Calendar.getInstance();
            BaselineAnalysisTools bat=new BaselineAnalysisTools();
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
            Date now=new Date();
            for (int i = 0; i < this.keys.size(); i++) {
                try{
                    Date before6months=c.getTime();
                    System.out.println(this.keys.get(i)+" started.");
                    ArrayList<CustomStatus> res=GlobalVarsStore.publicDBh.readTweets(this.keys.get(i),null);
                    boolean cancel=false;
                    if(res==null){res=new ArrayList<CustomStatus>();}
                    else if(res.size()>0){
                        try{
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            before6months=formatter.parse(res.get(0).getCreatedAt());
                            int days = Days.daysBetween(new DateTime(before6months), new DateTime(now)).getDays();
                            if(days>2){before6months=c.getTime();}
                            else if(days<1){
                                cancel=true;
                                System.out.println(this.keys.get(i)+" canceled.");
                            }
                        }catch(Exception ex){ex.printStackTrace(); before6months=c.getTime();}
                    }
                    if(!cancel){
                        int key;
                        if(this.keyIndex<5){
                            key=this.keyIndex;
                        }else{
                            key=5;
                        }
                        TwitterCrawler tc=new TwitterCrawler(GlobalVarsStore.tweeterPKeys[key],GlobalVarsStore.tweeterSKeys[key],GlobalVarsStore.tweeterPTokens[key],GlobalVarsStore.tweeterSTokens[key]);
                        System.out.println(this.keys.get(i)+" -filter:links since:"+sf.format(before6months));
                        List<CustomStatus> tweetsCrawled=tc.safeSearch((this.keys.get(i)+" -filter:links").replace(" ","%20"),sf.format(before6months));
                        for (int j = 0; j < tweetsCrawled.size(); j++) {
                            if(!tweetsCrawled.get(j).getClearText().contains("URL")) res.add(tweetsCrawled.get(j));
                        }
                        res=clearDuplicateStatuses(res);
                        System.out.println("Set cleared.");
                        for (int j = 0; j < res.size(); j++) {
                            try{
                                CustomStatus cur=res.get(j);
                                cur.setPolarity(bat.SentiWordNetMeanAnalysisSingle(cur));
                                GlobalVarsStore.publicDBh.insertTweet(cur.getKeyword(), cur.getText(), cur.getPolarity(), cur.getId(), cur.getCreatedAt());
                                trueRes.add(cur);
                            }catch(Exception exe){exe.printStackTrace();}
                        }
                    }else{
                        trueRes.addAll(res);
                    }
                }catch(Exception ex){ex.printStackTrace();}
            }
        }
    }
}
