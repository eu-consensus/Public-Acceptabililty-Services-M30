/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import Entities.CustomStatus;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ViP
 */
public class TopicAnalysisTools {
    public String[][][] extractSentiKeywordDuets(ArrayList<String> pos, ArrayList<String> neg, int keywordsNum){
        String[][][] res=null;
        int totalPos=0;
        int totalNeg=0;
        ArrayList<String> posWords=new ArrayList<String>();
        ArrayList<Double> posWordsFreq=new ArrayList<Double>();
        ArrayList<Integer> posWordsSum=new ArrayList<Integer>();
        ArrayList<String> negWords=new ArrayList<String>();
        ArrayList<Double> negWordsFreq=new ArrayList<Double>();
        ArrayList<Integer> negWordsSum=new ArrayList<Integer>();
        for (int i = 0; i < pos.size(); i++) {
            String[] bag=pos.get(i).split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
            for (int j = 1; j < bag.length; j+=2) {
                if(!TopicAnalysisTools.isStopword(bag[j])){
                    String duet1="";
                    String duet2="";
                    if(!TopicAnalysisTools.isStopword(bag[j-1])){
                        totalPos++;
                        duet1+=bag[j-1]+" "+bag[j];
                        int index=posWords.indexOf(duet1);
                        if(index<0){
                            posWords.add(duet1);
                            posWordsFreq.add(1.0);
                        }else{
                            posWordsFreq.set(index, posWordsFreq.get(index)+1);
                        }
                    }
                    if(j+1<bag.length){
                        if(!TopicAnalysisTools.isStopword(bag[j+1])){
                            totalPos++;
                            duet2+=bag[j]+" "+bag[j+1];
                            int index=posWords.indexOf(duet2);
                            if(index<0){
                                posWords.add(duet2);
                                posWordsFreq.add(1.0);
                            }else{
                                posWordsFreq.set(index, posWordsFreq.get(index)+1);
                            }
                        }
                    }
                }
            }
            //System.out.println("Completion: "+((double)i/(pos.size()+neg.size()))*100+"%");
        }
        for (int i = 0; i < neg.size(); i++) {
            String[] bag=neg.get(i).split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
            for (int j = 1; j < bag.length; j+=2) {
                if(!TopicAnalysisTools.isStopword(bag[j])){
                    String duet1="";
                    String duet2="";
                    if(!TopicAnalysisTools.isStopword(bag[j-1])){
                        totalNeg++;
                        duet1+=bag[j-1]+" "+bag[j];
                        int index=negWords.indexOf(duet1);
                        if(index<0){
                            negWords.add(duet1);
                            negWordsFreq.add(1.0);
                        }else{
                            negWordsFreq.set(index, negWordsFreq.get(index)+1);
                        }
                    }
                    if(j+1<bag.length){
                        if(!TopicAnalysisTools.isStopword(bag[j+1])){
                            totalNeg++;
                            duet2+=bag[j]+" "+bag[j+1];
                            int index=negWords.indexOf(duet2);
                            if(index<0){
                                negWords.add(duet2);
                                negWordsFreq.add(1.0);
                            }else{
                                negWordsFreq.set(index, negWordsFreq.get(index)+1);
                            }
                        }
                    }
                }
            }
            //System.out.println("Completion: "+((double)(i+pos.size())/(pos.size()+neg.size()))*100+"%");
        }
        for (int i = 0; i < posWordsFreq.size(); i++) {
            posWordsSum.add((int)Math.round(posWordsFreq.get(i)));
            posWordsFreq.set(i,posWordsFreq.get(i)/totalPos);
        }
        for (int i = 0; i < negWordsFreq.size(); i++) {
            negWordsSum.add((int)Math.round(negWordsFreq.get(i)));
            negWordsFreq.set(i,negWordsFreq.get(i)/totalNeg);
        }
        System.out.println("IDFing...");
        for (int i = 0; i < negWords.size(); i++) {
            int index=posWords.indexOf(negWords.get(i));
            if(negWords.get(i).equals("film")){
                int isdf=0;
            }
            if(index>=0){
                double newSum=posWordsFreq.get(index)-negWordsFreq.get(i);
                negWordsFreq.set(i,-1*newSum);
                if(newSum<=0){
                    posWordsFreq.remove(index);
                    posWords.remove(index);
                }else{
                    posWordsFreq.set(index, newSum);
                }
            }
        }
        for (int i = 0; i < negWords.size(); i++) {
            if(negWordsFreq.get(i)<0){
                negWordsFreq.remove(i);
                    negWords.remove(i);
            }
        }
        System.out.println("Sorting...");
        String[][] posKeys=new String[posWords.size()][3];
        String[][] negKeys=new String[negWords.size()][3];
        for (int i = 0; i < posWords.size(); i++) {
            double cur=posWordsFreq.get(i);
            int index=0;
            for (int j = 0; j < posWords.size(); j++) {
                if(posWordsFreq.get(j)>cur) index++;
            }
                while(posKeys[index][0]!=null && posKeys[index][0].length()>=1 && index<posWords.size()){
                    index++;
                }
                posKeys[index][0]=posWords.get(i);
                posKeys[index][1]=posWordsFreq.get(i).toString();
                posKeys[index][2]=posWordsSum.get(i).toString();
        }
        for (int i = 0; i < negWords.size(); i++) {
            double cur=negWordsFreq.get(i);
            int index=0;
            for (int j = 0; j < negWords.size(); j++) {
                if(negWordsFreq.get(j)>cur) index++;
            }
            //if(negKeys[index][0]==null || negKeys[index][0].length()<1){
            //    negKeys[index][0]=negWords.get(i);
            //    negKeys[index][1]=negWordsFreq.get(i).toString();
            //}else{
                while(negKeys[index][0]!=null && negKeys[index][0].length()>=1 && index<negWords.size()){
                    index++;
                }
                negKeys[index][0]=negWords.get(i);
                negKeys[index][1]=negWordsFreq.get(i).toString();
                negKeys[index][2]=negWordsSum.get(i).toString();
            //}
        }
        System.out.println("Complete.");
        res=new String[2][keywordsNum][3];
        for (int i = 0; i < keywordsNum; i++) {
            res[0][i][0]=posKeys[i][0];
            res[0][i][1]=posKeys[i][1];
            res[0][i][2]=posKeys[i][2];
        }
        for (int i = 0; i < keywordsNum; i++) {
            res[1][i][0]=negKeys[i][0];
            res[1][i][1]=negKeys[i][1];
            res[1][i][2]=negKeys[i][2];
        }
        return res;
    }
    
    public String[][][] extractSentiKeywords(ArrayList<String> pos, ArrayList<String> neg, int keywordsNum){
        String[][][] res=null;
        int totalPos=0;
        int totalNeg=0;
        ArrayList<String> posWords=new ArrayList<String>();
        ArrayList<Double> posWordsFreq=new ArrayList<Double>();
        ArrayList<Integer> posWordsSum=new ArrayList<Integer>();
        ArrayList<String> negWords=new ArrayList<String>();
        ArrayList<Double> negWordsFreq=new ArrayList<Double>();
        ArrayList<Integer> negWordsSum=new ArrayList<Integer>();
        for (int i = 0; i < pos.size(); i++) {
            String[] bag=pos.get(i).split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
            for (int j = 0; j < bag.length; j++) {
                if(!TopicAnalysisTools.isStopword(bag[j])){
                    totalPos++;
                    int index=posWords.indexOf(bag[j]);
                    if(index<0){
                        posWords.add(bag[j]);
                        posWordsFreq.add(1.0);
                    }else{
                        posWordsFreq.set(index, posWordsFreq.get(index)+1);
                    }
                }
            }
            //System.out.println("Completion: "+((double)i/(pos.size()+neg.size()))*100+"%");
        }
        for (int i = 0; i < neg.size(); i++) {
            String[] bag=neg.get(i).split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
            for (int j = 0; j < bag.length; j++) {
                if(!TopicAnalysisTools.isStopword(bag[j])){
                    totalNeg++;
                    int index=negWords.indexOf(bag[j]);
                    if(index<0){
                        negWords.add(bag[j]);
                        negWordsFreq.add(1.0);
                    }else{
                        negWordsFreq.set(index, negWordsFreq.get(index)+1);
                    }
                }
            }
            //System.out.println("Completion: "+((double)(i+pos.size())/(pos.size()+neg.size()))*100+"%");
        }
        for (int i = 0; i < posWordsFreq.size(); i++) {
            posWordsFreq.set(i,posWordsFreq.get(i)/totalPos);
        }
        for (int i = 0; i < negWordsFreq.size(); i++) {
            negWordsFreq.set(i,negWordsFreq.get(i)/totalNeg);
        }
        System.out.println("IDFing...");
        for (int i = 0; i < negWords.size(); i++) {
            int index=posWords.indexOf(negWords.get(i));
            if(negWords.get(i).equals("film")){
                int isdf=0;
            }
            if(index>=0){
                double newSum=posWordsFreq.get(index)-negWordsFreq.get(i);
                negWordsFreq.set(i,-1*newSum);
                if(newSum<=0){
                    posWordsFreq.remove(index);
                    posWords.remove(index);
                }else{
                    posWordsFreq.set(index, newSum);
                }
            }
        }
        for (int i = 0; i < negWords.size(); i++) {
            if(negWordsFreq.get(i)<0){
                negWordsFreq.remove(i);
                    negWords.remove(i);
            }
        }
        System.out.println("Sorting...");
        String[][] posKeys=new String[posWords.size()][3];
        String[][] negKeys=new String[negWords.size()][3];
        for (int i = 0; i < posWords.size(); i++) {
            double cur=posWordsFreq.get(i);
            int index=0;
            for (int j = 0; j < posWords.size(); j++) {
                if(posWordsFreq.get(j)>cur) index++;
            }
                while(posKeys[index][0]!=null && posKeys[index][0].length()>=1 && index<posWords.size()){
                    index++;
                }
                posKeys[index][0]=posWords.get(i);
                posKeys[index][1]=posWordsFreq.get(i).toString();
                posKeys[index][2]=posWordsSum.get(i).toString();
        }
        for (int i = 0; i < negWords.size(); i++) {
            double cur=negWordsFreq.get(i);
            int index=0;
            for (int j = 0; j < negWords.size(); j++) {
                if(negWordsFreq.get(j)>cur) index++;
            }
            //if(negKeys[index][0]==null || negKeys[index][0].length()<1){
            //    negKeys[index][0]=negWords.get(i);
            //    negKeys[index][1]=negWordsFreq.get(i).toString();
            //}else{
                while(negKeys[index][0]!=null && negKeys[index][0].length()>=1 && index<negWords.size()){
                    index++;
                }
                negKeys[index][0]=negWords.get(i);
                negKeys[index][1]=negWordsFreq.get(i).toString();
                negKeys[index][2]=negWordsSum.get(i).toString();
            //}
        }
        System.out.println("Complete.");
        res=new String[2][keywordsNum][3];
        for (int i = 0; i < keywordsNum; i++) {
            res[0][i][0]=posKeys[i][0];
            res[0][i][1]=posKeys[i][1];
            res[0][i][2]=posKeys[i][2];
        }
        for (int i = 0; i < keywordsNum; i++) {
            res[1][i][0]=negKeys[i][0];
            res[1][i][1]=negKeys[i][1];
            res[1][i][2]=negKeys[i][2];
        }
        return res;
    }
     
    public String[][] extractKeywordKples(ArrayList<String> texts, int keywordsNum, int knum, ArrayList<String> seeds){
        int total=0;
        ArrayList<String> Words=new ArrayList<String>();
        ArrayList<Double> WordsFreq=new ArrayList<Double>();
        for (int i = 0; i < texts.size(); i++) {
            String[] bag=texts.get(i).split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
            for (int j = knum-1; j < bag.length; j++) {
                boolean isSeed=false;
                boolean isStopword=false;
                String kple="";
                for (int k = knum-1; k >= 0 && !isSeed && !isStopword; k--) {
                    if(seeds!=null){
                        String test=bag[j-k].trim().toLowerCase();
                        isSeed=seeds.contains(bag[j-k].trim().toLowerCase());
                    }
                    isStopword=TopicAnalysisTools.isStopword(bag[j-k]);
                    kple+=bag[j-k]+" ";
                }
                if(!isStopword && !isSeed){
                    total++;
                    int index=Words.indexOf(kple);
                    if(index<0){
                        Words.add(kple);
                        WordsFreq.add(1.0);
                    }else{
                        WordsFreq.set(index, WordsFreq.get(index)+1);
                    }
                }
            }
            //System.out.println("Completion: "+((double)i/(texts.size()))*100+"%");
        }
        System.out.println("Sorting...");
        String[][] Keys=new String[Words.size()][3];
        for (int i = 0; i < Words.size(); i++) {
            double cur=WordsFreq.get(i);
            int index=0;
            for (int j = 0; j < Words.size(); j++) {
                if(WordsFreq.get(j)>cur) index++;
            }
                while(Keys[index][0]!=null && Keys[index][0].length()>=1 && index<Words.size()){
                    index++;
                }
                Keys[index][0]=Words.get(i);
                Keys[index][1]=(new Double(WordsFreq.get(i)/total)).toString();
                Keys[index][2]=WordsFreq.get(i).toString();
        } 
        System.out.println("Complete.");
        String[][] res=new String[keywordsNum][3];
        for (int i = 0; i < keywordsNum; i++) {
            res[i][0]=Keys[i][0];
            res[i][1]=Keys[i][1];
            res[i][2]=Keys[i][2];
        }
        return res;
    }
    
    public String[][] mergeSortKeywords(String[][] keys1,String[][] keys2){
        String[][] res=new String[keys1.length+keys2.length][3];
        int k1=0;
        int k2=0;
        for(int i=0;i<res.length;i++){
            if(k1<keys1.length && k2<keys2.length){
                if(Double.parseDouble(keys1[k1][1])>=Double.parseDouble(keys2[k2][1])){
                    res[i]=keys1[k1];
                    k1++;
                }else{
                    res[i]=keys2[k2];
                    k2++;
                }
            }else if(k1<keys1.length){
                res[i]=keys1[k1];
                k1++;
            }else if(k2<keys2.length){
                res[i]=keys2[k2];
                k2++;
            }
        }
        return res;
    }
    
    public Double countFrequencyFast(ArrayList<ArrayList<Integer>> scenarioTotals, int objective, int keyphrase){
        int scenarioGrandTotal=0;
        for (int i = 0; i < scenarioTotals.size(); i++) {
            for (int j = 0; j < scenarioTotals.get(i).size(); j++) {
                scenarioGrandTotal+=scenarioTotals.get(i).get(j);
            }
        }
        int keyphraseTotal=scenarioTotals.get(objective).get(keyphrase);
        if(keyphraseTotal!=0 && scenarioGrandTotal!=0){
            return (double)keyphraseTotal/(double)scenarioGrandTotal;
        }else{
            return 0.0;
        }
    }
    
    public Double countFrequency(ArrayList<CustomStatus> tweets, String keyphrase){
        Double res=0.0;
        keyphrase=keyphrase.toLowerCase().trim();
        String[] keys=keyphrase.split(" ");
        int found=0;
        for (int i = 0; i < tweets.size(); i++) {
            found=0;
            try {
                for (int j = 0; j < keys.length; j++) {
                    try {
                        if(tweets.get(i).getText().toLowerCase().contains(keys[j])){
                            found++;
                        }
                    } catch (Exception exe) {
                        Logger.getLogger(CrawlersConnector.class.getName()).log(Level.SEVERE, null, exe);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(CrawlersConnector.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(found==keys.length){res++;}
        }
        if(tweets.size()>0){
            res=res/tweets.size();
            return res;
        }else{
            return 0.0;
        }
    }
    
    public Double countFrequencyOld(ArrayList<String> texts, String keyphrase){
        Double res=0.0;
        keyphrase=keyphrase.toLowerCase().trim();
        String[] keys=keyphrase.split(" ");
        int found=0;
        for (int i = 0; i < texts.size(); i++) {
            found=0;
            for (int j = 0; j < keys.length; j++) {
                if(texts.get(i).toLowerCase().contains(keys[j])){
                    found++;
                }
            }
            if(found==keys.length){res++;}
        }
        if(texts.size()>0){
            res=res/texts.size();
            return res;
        }else{
            return 0.0;
        }
    }
     
    public static boolean isStopword(String word){
        word=word.trim().toLowerCase();
        if(word.equals("")) return true;
        else if(word.equals("\n")) return true;
        else if(word.equals("\t")) return true;
        else if(word.equals(" ")) return true;
        else if(word.equals("a")) return true;
        else if(word.equals("able")) return true;
        else if(word.equals("about")) return true;
        else if(word.equals("above")) return true;
        else if(word.equals("according")) return true;
        else if(word.equals("accordingly")) return true;
        else if(word.equals("across")) return true;
        else if(word.equals("actually")) return true;
        else if(word.equals("after")) return true;
        else if(word.equals("afterwards")) return true;
        else if(word.equals("again")) return true;
        else if(word.equals("against")) return true;
        else if(word.equals("all")) return true;
        else if(word.equals("allow")) return true;
        else if(word.equals("allows")) return true;
        else if(word.equals("almost")) return true;
        else if(word.equals("alone")) return true;
        else if(word.equals("along")) return true;
        else if(word.equals("already")) return true;
        else if(word.equals("also")) return true;
        else if(word.equals("although")) return true;
        else if(word.equals("always")) return true;
        else if(word.equals("am")) return true;
        else if(word.equals("among")) return true;
        else if(word.equals("amongst")) return true;
        else if(word.equals("an")) return true;
        else if(word.equals("and")) return true;
        else if(word.equals("another")) return true;
        else if(word.equals("any")) return true;
        else if(word.equals("anybody")) return true;
        else if(word.equals("anyhow")) return true;
        else if(word.equals("anyone")) return true;
        else if(word.equals("anything")) return true;
        else if(word.equals("anyway")) return true;
        else if(word.equals("anyways")) return true;
        else if(word.equals("anywhere")) return true;
        else if(word.equals("apart")) return true;
        else if(word.equals("appear")) return true;
        else if(word.equals("appreciate")) return true;
        else if(word.equals("appropriate")) return true;
        else if(word.equals("are")) return true;
        else if(word.equals("around")) return true;
        else if(word.equals("as")) return true;
        else if(word.equals("aside")) return true;
        else if(word.equals("ask")) return true;
        else if(word.equals("asking")) return true;
        else if(word.equals("associated")) return true;
        else if(word.equals("at")) return true;
        else if(word.equals("available")) return true;
        else if(word.equals("away")) return true;
        else if(word.equals("awfully")) return true;
        else if(word.equals("b")) return true;
        else if(word.equals("be")) return true;
        else if(word.equals("became")) return true;
        else if(word.equals("because")) return true;
        else if(word.equals("become")) return true;
        else if(word.equals("becomes")) return true;
        else if(word.equals("becoming")) return true;
        else if(word.equals("been")) return true;
        else if(word.equals("before")) return true;
        else if(word.equals("beforehand")) return true;
        else if(word.equals("behind")) return true;
        else if(word.equals("being")) return true;
        else if(word.equals("believe")) return true;
        else if(word.equals("below")) return true;
        else if(word.equals("beside")) return true;
        else if(word.equals("besides")) return true;
        else if(word.equals("best")) return true;
        else if(word.equals("better")) return true;
        else if(word.equals("between")) return true;
        else if(word.equals("beyond")) return true;
        else if(word.equals("both")) return true;
        else if(word.equals("brief")) return true;
        else if(word.equals("but")) return true;
        else if(word.equals("by")) return true;
        else if(word.equals("c")) return true;
        else if(word.equals("came")) return true;
        else if(word.equals("can")) return true;
        else if(word.equals("cannot")) return true;
        else if(word.equals("cant")) return true;
        else if(word.equals("cause")) return true;
        else if(word.equals("causes")) return true;
        else if(word.equals("certain")) return true;
        else if(word.equals("certainly")) return true;
        else if(word.equals("changes")) return true;
        else if(word.equals("clearly")) return true;
        else if(word.equals("co")) return true;
        else if(word.equals("com")) return true;
        else if(word.equals("come")) return true;
        else if(word.equals("comes")) return true;
        else if(word.equals("concerning")) return true;
        else if(word.equals("consequently")) return true;
        else if(word.equals("consider")) return true;
        else if(word.equals("considering")) return true;
        else if(word.equals("contain")) return true;
        else if(word.equals("containing")) return true;
        else if(word.equals("contains")) return true;
        else if(word.equals("corresponding")) return true;
        else if(word.equals("could")) return true;
        else if(word.equals("course")) return true;
        else if(word.equals("currently")) return true;
        else if(word.equals("d")) return true;
        else if(word.equals("definitely")) return true;
        else if(word.equals("described")) return true;
        else if(word.equals("despite")) return true;
        else if(word.equals("did")) return true;
        else if(word.equals("different")) return true;
        else if(word.equals("do")) return true;
        else if(word.equals("does")) return true;
        else if(word.equals("doing")) return true;
        else if(word.equals("done")) return true;
        else if(word.equals("down")) return true;
        else if(word.equals("downwards")) return true;
        else if(word.equals("during")) return true;
        else if(word.equals("e")) return true;
        else if(word.equals("each")) return true;
        else if(word.equals("edu")) return true;
        else if(word.equals("eg")) return true;
        else if(word.equals("eight")) return true;
        else if(word.equals("either")) return true;
        else if(word.equals("else")) return true;
        else if(word.equals("elsewhere")) return true;
        else if(word.equals("enough")) return true;
        else if(word.equals("entirely")) return true;
        else if(word.equals("especially")) return true;
        else if(word.equals("et")) return true;
        else if(word.equals("etc")) return true;
        else if(word.equals("even")) return true;
        else if(word.equals("ever")) return true;
        else if(word.equals("every")) return true;
        else if(word.equals("everybody")) return true;
        else if(word.equals("everyone")) return true;
        else if(word.equals("everything")) return true;
        else if(word.equals("everywhere")) return true;
        else if(word.equals("ex")) return true;
        else if(word.equals("exactly")) return true;
        else if(word.equals("example")) return true;
        else if(word.equals("except")) return true;
        else if(word.equals("f")) return true;
        else if(word.equals("far")) return true;
        else if(word.equals("few")) return true;
        else if(word.equals("fifth")) return true;
        else if(word.equals("first")) return true;
        else if(word.equals("five")) return true;
        else if(word.equals("followed")) return true;
        else if(word.equals("following")) return true;
        else if(word.equals("follows")) return true;
        else if(word.equals("for")) return true;
        else if(word.equals("former")) return true;
        else if(word.equals("formerly")) return true;
        else if(word.equals("forth")) return true;
        else if(word.equals("four")) return true;
        else if(word.equals("from")) return true;
        else if(word.equals("further")) return true;
        else if(word.equals("furthermore")) return true;
        else if(word.equals("g")) return true;
        else if(word.equals("get")) return true;
        else if(word.equals("gets")) return true;
        else if(word.equals("getting")) return true;
        else if(word.equals("given")) return true;
        else if(word.equals("gives")) return true;
        else if(word.equals("go")) return true;
        else if(word.equals("goes")) return true;
        else if(word.equals("going")) return true;
        else if(word.equals("gone")) return true;
        else if(word.equals("got")) return true;
        else if(word.equals("gotten")) return true;
        else if(word.equals("greetings")) return true;
        else if(word.equals("h")) return true;
        else if(word.equals("had")) return true;
        else if(word.equals("happens")) return true;
        else if(word.equals("hardly")) return true;
        else if(word.equals("has")) return true;
        else if(word.equals("have")) return true;
        else if(word.equals("having")) return true;
        else if(word.equals("he")) return true;
        else if(word.equals("hello")) return true;
        else if(word.equals("help")) return true;
        else if(word.equals("hence")) return true;
        else if(word.equals("her")) return true;
        else if(word.equals("here")) return true;
        else if(word.equals("hereafter")) return true;
        else if(word.equals("hereby")) return true;
        else if(word.equals("herein")) return true;
        else if(word.equals("hereupon")) return true;
        else if(word.equals("hers")) return true;
        else if(word.equals("herself")) return true;
        else if(word.equals("hi")) return true;
        else if(word.equals("him")) return true;
        else if(word.equals("himself")) return true;
        else if(word.equals("his")) return true;
        else if(word.equals("hither")) return true;
        else if(word.equals("hopefully")) return true;
        else if(word.equals("how")) return true;
        else if(word.equals("howbeit")) return true;
        else if(word.equals("however")) return true;
        else if(word.equals("i")) return true;
        else if(word.equals("ie")) return true;
        else if(word.equals("if")) return true;
        else if(word.equals("ignored")) return true;
        else if(word.equals("immediate")) return true;
        else if(word.equals("in")) return true;
        else if(word.equals("inasmuch")) return true;
        else if(word.equals("inc")) return true;
        else if(word.equals("indeed")) return true;
        else if(word.equals("indicate")) return true;
        else if(word.equals("indicated")) return true;
        else if(word.equals("indicates")) return true;
        else if(word.equals("inner")) return true;
        else if(word.equals("insofar")) return true;
        else if(word.equals("instead")) return true;
        else if(word.equals("into")) return true;
        else if(word.equals("inward")) return true;
        else if(word.equals("is")) return true;
        else if(word.equals("it")) return true;
        else if(word.equals("its")) return true;
        else if(word.equals("itself")) return true;
        else if(word.equals("j")) return true;
        else if(word.equals("just")) return true;
        else if(word.equals("k")) return true;
        else if(word.equals("keep")) return true;
        else if(word.equals("keeps")) return true;
        else if(word.equals("kept")) return true;
        else if(word.equals("know")) return true;
        else if(word.equals("knows")) return true;
        else if(word.equals("known")) return true;
        else if(word.equals("l")) return true;
        else if(word.equals("last")) return true;
        else if(word.equals("lately")) return true;
        else if(word.equals("later")) return true;
        else if(word.equals("latter")) return true;
        else if(word.equals("latterly")) return true;
        else if(word.equals("least")) return true;
        else if(word.equals("less")) return true;
        else if(word.equals("lest")) return true;
        else if(word.equals("let")) return true;
        else if(word.equals("like")) return true;
        else if(word.equals("liked")) return true;
        else if(word.equals("likely")) return true;
        else if(word.equals("little")) return true;
        else if(word.equals("ll")) return true; //stops.added to avoid words like you'll,I'll etc.
        else if(word.equals("look")) return true;
        else if(word.equals("looking")) return true;
        else if(word.equals("looks")) return true;
        else if(word.equals("ltd")) return true;
        else if(word.equals("m")) return true;
        else if(word.equals("mainly")) return true;
        else if(word.equals("many")) return true;
        else if(word.equals("may")) return true;
        else if(word.equals("maybe")) return true;
        else if(word.equals("me")) return true;
        else if(word.equals("mean")) return true;
        else if(word.equals("meanwhile")) return true;
        else if(word.equals("merely")) return true;
        else if(word.equals("might")) return true;
        else if(word.equals("more")) return true;
        else if(word.equals("moreover")) return true;
        else if(word.equals("most")) return true;
        else if(word.equals("mostly")) return true;
        else if(word.equals("much")) return true;
        else if(word.equals("must")) return true;
        else if(word.equals("my")) return true;
        else if(word.equals("myself")) return true;
        else if(word.equals("n")) return true;
        else if(word.equals("name")) return true;
        else if(word.equals("namely")) return true;
        else if(word.equals("nd")) return true;
        else if(word.equals("near")) return true;
        else if(word.equals("nearly")) return true;
        else if(word.equals("necessary")) return true;
        else if(word.equals("need")) return true;
        else if(word.equals("needs")) return true;
        else if(word.equals("neither")) return true;
        else if(word.equals("never")) return true;
        else if(word.equals("nevertheless")) return true;
        else if(word.equals("new")) return true;
        else if(word.equals("next")) return true;
        else if(word.equals("nine")) return true;
        else if(word.equals("no")) return true;
        else if(word.equals("nobody")) return true;
        else if(word.equals("non")) return true;
        else if(word.equals("none")) return true;
        else if(word.equals("noone")) return true;
        else if(word.equals("nor")) return true;
        else if(word.equals("normally")) return true;
        else if(word.equals("not")) return true;
        else if(word.equals("nothing")) return true;
        else if(word.equals("novel")) return true;
        else if(word.equals("now")) return true;
        else if(word.equals("nowhere")) return true;
        else if(word.equals("o")) return true;
        else if(word.equals("obviously")) return true;
        else if(word.equals("of")) return true;
        else if(word.equals("off")) return true;
        else if(word.equals("often")) return true;
        else if(word.equals("oh")) return true;
        else if(word.equals("ok")) return true;
        else if(word.equals("okay")) return true;
        else if(word.equals("old")) return true;
        else if(word.equals("on")) return true;
        else if(word.equals("once")) return true;
        else if(word.equals("one")) return true;
        else if(word.equals("ones")) return true;
        else if(word.equals("only")) return true;
        else if(word.equals("onto")) return true;
        else if(word.equals("or")) return true;
        else if(word.equals("other")) return true;
        else if(word.equals("others")) return true;
        else if(word.equals("otherwise")) return true;
        else if(word.equals("ought")) return true;
        else if(word.equals("our")) return true;
        else if(word.equals("ours")) return true;
        else if(word.equals("ourselves")) return true;
        else if(word.equals("out")) return true;
        else if(word.equals("outside")) return true;
        else if(word.equals("over")) return true;
        else if(word.equals("overall")) return true;
        else if(word.equals("own")) return true;
        else if(word.equals("p")) return true;
        else if(word.equals("particular")) return true;
        else if(word.equals("particularly")) return true;
        else if(word.equals("per")) return true;
        else if(word.equals("perhaps")) return true;
        else if(word.equals("placed")) return true;
        else if(word.equals("please")) return true;
        else if(word.equals("plus")) return true;
        else if(word.equals("possible")) return true;
        else if(word.equals("presumably")) return true;
        else if(word.equals("probably")) return true;
        else if(word.equals("provides")) return true;
        else if(word.equals("q")) return true;
        else if(word.equals("que")) return true;
        else if(word.equals("quite")) return true;
        else if(word.equals("qv")) return true;
        else if(word.equals("r")) return true;
        else if(word.equals("rather")) return true;
        else if(word.equals("rd")) return true;
        else if(word.equals("re")) return true;
        else if(word.equals("really")) return true;
        else if(word.equals("reasonably")) return true;
        else if(word.equals("ref")) return true;
        else if(word.equals("regarding")) return true;
        else if(word.equals("regardless")) return true;
        else if(word.equals("regards")) return true;
        else if(word.equals("relatively")) return true;
        else if(word.equals("respectively")) return true;
        else if(word.equals("right")) return true;
        else if(word.equals("s")) return true;
        else if(word.equals("said")) return true;
        else if(word.equals("same")) return true;
        else if(word.equals("saw")) return true;
        else if(word.equals("say")) return true;
        else if(word.equals("saying")) return true;
        else if(word.equals("says")) return true;
        else if(word.equals("second")) return true;
        else if(word.equals("secondly")) return true;
        else if(word.equals("see")) return true;
        else if(word.equals("seeing")) return true;
        else if(word.equals("seem")) return true;
        else if(word.equals("seemed")) return true;
        else if(word.equals("seeming")) return true;
        else if(word.equals("seems")) return true;
        else if(word.equals("seen")) return true;
        else if(word.equals("self")) return true;
        else if(word.equals("selves")) return true;
        else if(word.equals("sensible")) return true;
        else if(word.equals("sent")) return true;
        else if(word.equals("serious")) return true;
        else if(word.equals("seriously")) return true;
        else if(word.equals("seven")) return true;
        else if(word.equals("several")) return true;
        else if(word.equals("shall")) return true;
        else if(word.equals("she")) return true;
        else if(word.equals("should")) return true;
        else if(word.equals("since")) return true;
        else if(word.equals("six")) return true;
        else if(word.equals("so")) return true;
        else if(word.equals("some")) return true;
        else if(word.equals("somebody")) return true;
        else if(word.equals("somehow")) return true;
        else if(word.equals("someone")) return true;
        else if(word.equals("something")) return true;
        else if(word.equals("sometime")) return true;
        else if(word.equals("sometimes")) return true;
        else if(word.equals("somewhat")) return true;
        else if(word.equals("somewhere")) return true;
        else if(word.equals("soon")) return true;
        else if(word.equals("sorry")) return true;
        else if(word.equals("specified")) return true;
        else if(word.equals("specify")) return true;
        else if(word.equals("specifying")) return true;
        else if(word.equals("still")) return true;
        else if(word.equals("sub")) return true;
        else if(word.equals("such")) return true;
        else if(word.equals("sup")) return true;
        else if(word.equals("sure")) return true;
        else if(word.equals("t")) return true;
        else if(word.equals("take")) return true;
        else if(word.equals("taken")) return true;
        else if(word.equals("tell")) return true;
        else if(word.equals("tends")) return true;
        else if(word.equals("th")) return true;
        else if(word.equals("than")) return true;
        else if(word.equals("thank")) return true;
        else if(word.equals("thanks")) return true;
        else if(word.equals("thanx")) return true;
        else if(word.equals("that")) return true;
        else if(word.equals("thats")) return true;
        else if(word.equals("the")) return true;
        else if(word.equals("their")) return true;
        else if(word.equals("theirs")) return true;
        else if(word.equals("them")) return true;
        else if(word.equals("themselves")) return true;
        else if(word.equals("then")) return true;
        else if(word.equals("thence")) return true;
        else if(word.equals("there")) return true;
        else if(word.equals("thereafter")) return true;
        else if(word.equals("thereby")) return true;
        else if(word.equals("therefore")) return true;
        else if(word.equals("therein")) return true;
        else if(word.equals("theres")) return true;
        else if(word.equals("thereupon")) return true;
        else if(word.equals("these")) return true;
        else if(word.equals("they")) return true;
        else if(word.equals("think")) return true;
        else if(word.equals("third")) return true;
        else if(word.equals("this")) return true;
        else if(word.equals("thorough")) return true;
        else if(word.equals("thoroughly")) return true;
        else if(word.equals("those")) return true;
        else if(word.equals("though")) return true;
        else if(word.equals("three")) return true;
        else if(word.equals("through")) return true;
        else if(word.equals("throughout")) return true;
        else if(word.equals("thru")) return true;
        else if(word.equals("thus")) return true;
        else if(word.equals("to")) return true;
        else if(word.equals("together")) return true;
        else if(word.equals("too")) return true;
        else if(word.equals("took")) return true;
        else if(word.equals("toward")) return true;
        else if(word.equals("towards")) return true;
        else if(word.equals("tried")) return true;
        else if(word.equals("tries")) return true;
        else if(word.equals("truly")) return true;
        else if(word.equals("try")) return true;
        else if(word.equals("trying")) return true;
        else if(word.equals("twice")) return true;
        else if(word.equals("two")) return true;
        else if(word.equals("u")) return true;
        else if(word.equals("un")) return true;
        else if(word.equals("under")) return true;
        else if(word.equals("unfortunately")) return true;
        else if(word.equals("unless")) return true;
        else if(word.equals("unlikely")) return true;
        else if(word.equals("until")) return true;
        else if(word.equals("unto")) return true;
        else if(word.equals("up")) return true;
        else if(word.equals("upon")) return true;
        else if(word.equals("url")) return true;
        else if(word.equals("us")) return true;
        else if(word.equals("use")) return true;
        else if(word.equals("used")) return true;
        else if(word.equals("useful")) return true;
        else if(word.equals("uses")) return true;
        else if(word.equals("using")) return true;
        else if(word.equals("usually")) return true;
        else if(word.equals("uucp")) return true;
        else if(word.equals("v")) return true;
        else if(word.equals("value")) return true;
        else if(word.equals("various")) return true;
        else if(word.equals("ve")) return true; //stops.added to avoid words like I've,you've etc.
        else if(word.equals("very")) return true;
        else if(word.equals("via")) return true;
        else if(word.equals("viz")) return true;
        else if(word.equals("vs")) return true;
        else if(word.equals("w")) return true;
        else if(word.equals("want")) return true;
        else if(word.equals("wants")) return true;
        else if(word.equals("was")) return true;
        else if(word.equals("way")) return true;
        else if(word.equals("we")) return true;
        else if(word.equals("welcome")) return true;
        else if(word.equals("well")) return true;
        else if(word.equals("went")) return true;
        else if(word.equals("were")) return true;
        else if(word.equals("what")) return true;
        else if(word.equals("whatever")) return true;
        else if(word.equals("when")) return true;
        else if(word.equals("whence")) return true;
        else if(word.equals("whenever")) return true;
        else if(word.equals("where")) return true;
        else if(word.equals("whereafter")) return true;
        else if(word.equals("whereas")) return true;
        else if(word.equals("whereby")) return true;
        else if(word.equals("wherein")) return true;
        else if(word.equals("whereupon")) return true;
        else if(word.equals("wherever")) return true;
        else if(word.equals("whether")) return true;
        else if(word.equals("which")) return true;
        else if(word.equals("while")) return true;
        else if(word.equals("whither")) return true;
        else if(word.equals("who")) return true;
        else if(word.equals("whoever")) return true;
        else if(word.equals("whole")) return true;
        else if(word.equals("whom")) return true;
        else if(word.equals("whose")) return true;
        else if(word.equals("why")) return true;
        else if(word.equals("will")) return true;
        else if(word.equals("willing")) return true;
        else if(word.equals("wish")) return true;
        else if(word.equals("with")) return true;
        else if(word.equals("within")) return true;
        else if(word.equals("without")) return true;
        else if(word.equals("wonder")) return true;
        else if(word.equals("would")) return true;
        else if(word.equals("would")) return true;
        else if(word.equals("x")) return true;
        else if(word.equals("y")) return true;
        else if(word.equals("yes")) return true;
        else if(word.equals("yet")) return true;
        else if(word.equals("you")) return true;
        else if(word.equals("your")) return true;
        else if(word.equals("yours")) return true;
        else if(word.equals("yourself")) return true;
        else if(word.equals("yourselves")) return true;
        else if(word.equals("z")) return true;
        else if(word.equals("zero")) return true;
        else return false;
    }
}
