/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import Entities.Afinn;
import Entities.Lexicon;
import Entities.SentiWordNet;
import java.util.Date;

/**
 *
 * @author ViP
 */
public class GlobalVarsStore {
    //User Specified Variables
    //public static String databaseUsername="USERNAME";
    //public static String databasePassword="PASSWORD";
    //public static String databaseServer="jdbc:mysql://SERVER_IP/DATABASE";
    //public static String databaseDriver=null; /*DATABASE DRIVER or NULL*/
    public static String databasePool="consensus_resource";
    //public static String databasePoolExternal="twitter_app_resource";
    public static String trainDataDir = "C:\\trainData\\";
    public static String taggerPath = "models/english-left3words-distsim.tagger";
    
    //System Variables
    public static databaseHandler publicDBh= new databaseHandler();
    public static double containmentConfidenceWeight=0.3;
    public static SentiWordNet swn;
    public static String[] contradictionWords = {"but","instead","however"};
    public static String backupFilename;
    public static int backupPreSent;
    public static Double[] threshold=new Double[]{-0.1,0.1};
    public static Lexicon lex;
    public static String lexicon="afinn";
    public static String lexiPath;
    public static boolean lock=false;
    public static boolean lockBiofuel=false;
    public static boolean lockTransport=false;
    public static Date lockDate;
    public static String[] tweeterSKeys = {
        "DoHz48iv0PmvXJ8HBPWbiAlE4lbgNYhuhSLiBDTDn2PWuJGvfc",
        "OSEKmD4q9w3TFp1bawKdv9cIh9oOYp6oy0H4s46K5nTx141BiC",
        "b0QDzgaropxW5fzZLY26LB1RAFLh2hZuU0cZirL5anEptsSsem",
        "xDZcbNyFzKBGJpaad8pHLk8FOr3UmDNSyKSz9ltbAoOcIlDyUW",
        "EHu0ywKkKsl1qPBLFfe9qfc5ZWeyR6Wv5Hd9eYajr4MzCl6Ghq",
        "RsxdrumKOaqmWDVXDij406l4fkWPf3Ucr6xh0uDGXzocbwmcIb"};
    public static String[] tweeterSTokens = {
        "GUKiVUK8idOsrFv7vtEAemNrZE2Ac3dUMyAggAnvwuyqw",
        "olsWbUQEkjbVKUudVezkf4Ve8zQXFG0LyDMmkRSpy83Se",
        "yUGNT3VCpNZ3pdOw13HNrOQlgjpStjel05dxOn9M0MFEY",
        "lxLgtW6fp2F79CCJ5bvHW3vMbPwear5vK4aJPFO0blZDr",
        "ek58nyOdt8RQ8yFmxvjsLBsJ1vMPnd85u1MdJ7l562oXU",
        "KEB7U6h8RGSfPUhawcxTFHK91jwHANRk4RGLdRTw7lfAz"};
    public static String[] tweeterPKeys = {
        "bKAbzY8V3ooXtCmDyNsYPasC7",
        "0HZjx9iCCDiduDCMObxKq87Ua",
        "sj0vUUEE8kuu8Jx4lfGhNjZVy",
        "ODpHYzc2B8zA4LbhvpUAZzvLn",
        "g4B5aEMk7HUelgwVuS7OaWQnJ",
        "8hdO2npjYrzaRV2rAh0N65Ql5"};
    public static String[] tweeterPTokens = {
        "359447855-5bja5qCy2MxCj8Hhq0Xv8cQ9AiKWo6O1WN078RmO",
        "359447855-x7rT2AUgbeeeqRFp2ke9COG0HIZADgEXOP5ALZLh",
        "359447855-mM8v6VKL2vxvOFPMlBIBts0JnhQKaj8qkXFWUA98",
        "4824330867-sdTfOlXJYWjAJGh6f0XOOwpRTJwDlqYreGRc9Um",
        "4824330867-GQ6DdxjGjQ0hFNjxLUUuG4IJLZlFBZv0cRGOSDR",
        "4824330867-8RgNk78RXO8a7qSenWVXlbTosS7u7RPEgwh3p3F"};
    
    public static String printTime(Date date) {
        return date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }
    
    public static void trainLexicon(String lexicon,String path){
        lexicon=lexicon.toLowerCase().trim();
        if(GlobalVarsStore.lexicon.equals(lexicon)){
            if(GlobalVarsStore.lex==null){
                if(lexicon.equals("wordnet")){
                    GlobalVarsStore.lex=new SentiWordNet();
                }else if(lexicon.equals("afinn")){
                    GlobalVarsStore.lex=new Afinn();
                }
            }
        }else{
            GlobalVarsStore.lexicon=lexicon.toLowerCase().trim();
            if(lexicon.equals("wordnet")){
                GlobalVarsStore.lex=new SentiWordNet();
            }else if(lexicon.equals("afinn")){
                GlobalVarsStore.lex=new Afinn();
            }
        }
        if(GlobalVarsStore.lexiPath==null){
            GlobalVarsStore.lexiPath=path;
            System.out.println(GlobalVarsStore.printTime(new Date())+": Parsing "+lexicon+"... ");
            GlobalVarsStore.lex.populate(path);
            System.out.println(GlobalVarsStore.printTime(new Date())+": "+lexicon+" parsed for "+GlobalVarsStore.lex.getWordCount()+" words. ");
        }else if(!GlobalVarsStore.lexiPath.equals(path)){
            GlobalVarsStore.lexiPath=path;
            System.out.println(GlobalVarsStore.printTime(new Date())+": Parsing "+lexicon+"... ");
            GlobalVarsStore.lex.populate(path);
            System.out.println(GlobalVarsStore.printTime(new Date())+": "+lexicon+" parsed for "+GlobalVarsStore.lex.getWordCount()+" words. ");
        }
    }
}
