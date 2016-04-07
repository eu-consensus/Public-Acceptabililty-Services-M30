/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import Entities.CustomStatus;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author ViP
 */
public class BaselineAnalysisTools {
    
    databaseHandler dbh;

    public BaselineAnalysisTools() {
        dbh=new databaseHandler();
    }
    
    public void prepareTools(){
        if(GlobalVarsStore.lexicon.equals("wordnet")){
            GlobalVarsStore.trainLexicon("wordnet",GlobalVarsStore.trainDataDir+"SentiWordNet_3.0.0_20130122.txt");
        }else if(GlobalVarsStore.lexicon.equals("afinn")){
            GlobalVarsStore.trainLexicon("afinn",GlobalVarsStore.trainDataDir+"AFINN-111.txt");
        }
    }
    
    public Double SentiWordNetMeanAnalysisSingle(CustomStatus tweet){
           this.prepareTools();
           String[] bag=tweet.getClearText().split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
            //DecimalFormat df = new DecimalFormat("#.####");
            ArrayList<Double> weights=new ArrayList<Double>();
            for (int i = 0; i < bag.length; i++) {
                Double res=GlobalVarsStore.lex.testWord(bag[i]);  
                if(res!=null){
                    weights.add(res);
                }
            }
            Double totalSent=0.0;
            for (int i = 0; i < weights.size(); i++) {
                totalSent+=weights.get(i);
            }
            if(weights.size()>0){
                totalSent=totalSent/weights.size();
            }
            return totalSent;
    }
    
    public Double SentiWordNetMeanAnalysis(ArrayList<CustomStatus> tweets, String keyword){
        int posCount=0;
        int neuCount=0;
        int negCount=0;
        ArrayList<Double> sents=new ArrayList<Double>();
        int step=(int)Math.round((double)tweets.size()/100);
        //int completion=0;
        for (int j = 0; j < tweets.size(); j++) {
            /*if(step>0){
                if(j%step==0) {
                    completion++;
                    System.out.println("Completion - "+completion+"%");
                }
            }*/
            String[] bag=tweets.get(j).getClearText().split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
            //DecimalFormat df = new DecimalFormat("#.####");
            ArrayList<Double> weights=new ArrayList<Double>();
            for (int i = 0; i < bag.length; i++) {
                Double res=GlobalVarsStore.lex.testWord(bag[i]);  
                if(res!=null){
                    weights.add(res);
                }
            }
            Double totalSent=0.0;
            for (int i = 0; i < weights.size(); i++) {
                totalSent+=weights.get(i);
            }
            if(weights.size()>0){
                totalSent=totalSent/weights.size();
                sents.add(totalSent);
            }
            /*try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ratedTweets.csv", true)));
                out.println(totalSent+"\t"+texts.get(j).replaceAll("\\s", " "));
                out.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }*/
            try{
            if(keyword!=null){
                dbh.insertTweet(keyword, tweets.get(j).getText(), totalSent, tweets.get(j).getId(), tweets.get(j).getCreatedAt());
            }
            }catch(Exception ex){ex.printStackTrace();}
        }
        for (int i = 0; i < sents.size(); i++) {
            if(sents.get(i)>GlobalVarsStore.threshold[1]) posCount++;
            else if(sents.get(i)<GlobalVarsStore.threshold[0]) negCount++;
            else neuCount++;
        }
        System.out.println(posCount+" - "+neuCount+" - "+negCount);
        if(posCount+neuCount+negCount > 0)
        return ((double)posCount+(-1*negCount))/((double)posCount+neuCount+negCount); //return aggrevated score
        else return 0.0;
    }
    
    public Double SentiWordNetMeanAnalysisOld(ArrayList<String> texts, String keyword){
        int posCount=0;
        int neuCount=0;
        int negCount=0;
        ArrayList<Double> sents=new ArrayList<Double>();
        int step=(int)Math.round((double)texts.size()/100);
        //int completion=0;
        for (int j = 0; j < texts.size(); j++) {
            /*if(step>0){
                if(j%step==0) {
                    completion++;
                    System.out.println("Completion - "+completion+"%");
                }
            }*/
            String[] bag=texts.get(j).split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
            //DecimalFormat df = new DecimalFormat("#.####");
            ArrayList<Double> weights=new ArrayList<Double>();
            for (int i = 0; i < bag.length; i++) {
                Double res=GlobalVarsStore.lex.testWord(bag[i]);  
                if(res!=null){
                    weights.add(res);
                }
            }
            Double totalSent=0.0;
            for (int i = 0; i < weights.size(); i++) {
                totalSent+=weights.get(i);
            }
            if(weights.size()>0){
                totalSent=totalSent/weights.size();
                sents.add(totalSent);
            }
            /*try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ratedTweets.csv", true)));
                out.println(totalSent+"\t"+texts.get(j).replaceAll("\\s", " "));
                out.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }*/
            try{
            if(keyword!=null){
                dbh.insertTweet(keyword, texts.get(j), totalSent, null, null);
            }
            }catch(Exception ex){ex.printStackTrace();}
        }
        for (int i = 0; i < sents.size(); i++) {
            if(sents.get(i)>GlobalVarsStore.threshold[1]) posCount++;
            else if(sents.get(i)<GlobalVarsStore.threshold[0]) negCount++;
            else neuCount++;
        }
        System.out.println(posCount+" - "+neuCount+" - "+negCount);
        if(posCount+neuCount+negCount > 0)
        return ((double)posCount+(-1*negCount))/((double)posCount+neuCount+negCount); //return aggrevated score
        else return 0.0;
    }
}
