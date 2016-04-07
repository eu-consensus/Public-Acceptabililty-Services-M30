/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestServices;

import Tools.BaselineAnalysisTools;
import Tools.GlobalVarsStore;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
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
import javax.ws.rs.QueryParam;
import org.json.JSONException;
import org.json.JSONObject;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import java.io.StringReader;
import java.util.List;

/**
 * REST Web Service
 *
 * @author ViP
 */
@Path("GetTargetedSentiment")
public class GetTargetedSentimentResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetTargetedSentimentResource
     */
    public GetTargetedSentimentResource() {
    }

    /**
     * Retrieves representation of an instance of RestServices.GetTargetedSentimentResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("data") String datas) {
        System.out.println("Working Directory = " +
              System.getProperty("user.dir"));
        JSONObject objOuter = new JSONObject();
        try {
            JSONObject inputJsonObject= new JSONObject(datas);
            String targetPhrase=inputJsonObject.getString("target");
            String contextText=inputJsonObject.getString("data");
            
            String modelPath = DependencyParser.DEFAULT_MODEL;
            
            MaxentTagger tagger = new MaxentTagger(GlobalVarsStore.taggerPath);
            DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);

            DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(contextText));
            ArrayList<TypedDependency> td=new ArrayList<TypedDependency>();
            for (List<HasWord> sentence : tokenizer) {
              List<TaggedWord> tagged = tagger.tagSentence(sentence);
              GrammaticalStructure gs = parser.predict(tagged);
              td.addAll(gs.typedDependencies());
            }
            
            BaselineAnalysisTools bat = new BaselineAnalysisTools();
            bat.prepareTools();
            String[] bag=clear(contextText).split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
            double sent=0.0;
            ArrayList<Double> weights=new ArrayList<Double>();
            int pos=0;
            int neg=0;
            int neu=0;
            double confidenceContainment=0.0;
            double confidenceClassification=0.0;
                for (int i = 0; i < bag.length; i++) {
                    Double res=GlobalVarsStore.lex.testWord(bag[i]);  
                    if(res!=null){
                        weights.add(res);
                        confidenceContainment++;
                    }
                }
                Double totalSent=0.0;
                for (int i = 0; i < weights.size(); i++) {
                    totalSent+=weights.get(i);
                    if(weights.get(i)>0){
                        pos++;
                    }else if(weights.get(i)<0){
                        neg++;
                    }else{
                        neu++;
                    }
                }
                if(weights.size()>0){
                    sent=totalSent/weights.size();
                    confidenceContainment=confidenceContainment/bag.length;
                    if(sent>0){
                        confidenceClassification=pos/(double)(pos+neg+neu);
                    }else if(sent<0){
                        confidenceClassification=neg/(double)(pos+neg+neu);
                    }else{
                        confidenceClassification=neu/(double)(pos+neg+neu);
                    }
                }
                
                String[] targets;
                if(targetPhrase.contains(" ")){
                    targets=targetPhrase.split(" ");
                }else{
                    targets=new String[1];
                    targets[0]=targetPhrase;
                }
                double tempSent=0;
                double tSent=0;
                double tSize=0;
                double sentMod=1;
                for (int i = 0; i < targets.length; i++) {
                    for (int j = 0; j < td.size(); j++) {
                        String secondLevel=null;
                        double secondSentMod=1;
                        if(targets[i].equals(td.get(j).gov().value())){
                            if(td.get(j).reln().getShortName().equals("neg")){
                                sentMod=-1;
                            }else{
                                if(td.get(j).reln().getShortName().equals("dobj")){
                                   secondLevel= td.get(j).dep().value();
                                }
                                tSize++;
                                tempSent=GlobalVarsStore.lex.testWord(td.get(j).dep().value()); 
                            }
                        }else if(targets[i].equals(td.get(j).dep().value())){
                            if(td.get(j).reln().getShortName().equals("neg")){
                                sentMod=-1;
                            }else{
                                if(td.get(j).reln().getShortName().equals("dobj")){
                                   secondLevel= td.get(j).gov().value();
                                }
                                tSize++;
                                tempSent=GlobalVarsStore.lex.testWord(td.get(j).gov().value()); 
                            }
                        }
                        if(secondLevel!=null){
                            for (int k = 0; k < td.size(); k++) {
                                if(!targets[i].equals(td.get(k).dep().value()) && secondLevel.equals(td.get(k).gov().value())){
                                    if(td.get(k).reln().getShortName().equals("neg")){
                                        secondSentMod=-1;
                                    }
                                }else if(!targets[i].equals(td.get(k).gov().value()) && secondLevel.equals(td.get(k).dep().value())){
                                    if(td.get(k).reln().getShortName().equals("neg")){
                                        secondSentMod=-1;
                                    }
                                }
                            }
                        }
                        tSent+=tempSent*secondSentMod;
                    }
                }
                if(tSize>0){
                    tSent/=tSize;
                }
                if(tSent==0){
                    tSent=sent*sentMod;
                }
                
                objOuter.put("SOS", tSent); 
                objOuter.put("CONFIDENCE", (confidenceClassification*(1-GlobalVarsStore.containmentConfidenceWeight))+(confidenceContainment*GlobalVarsStore.containmentConfidenceWeight));
            }catch (JSONException ex) {
                Logger.getLogger(GetBatchSentimentResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            return objOuter.toString();
    }

    /**
     * PUT method for updating or creating an instance of GetTargetedSentimentResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
    
    private String clear(String dirty){
        String clear="";
        if(dirty.indexOf("http://")>=0){
            if(dirty.indexOf(" ", dirty.indexOf("http://"))>=0){      
                clear=dirty.toLowerCase().replaceAll("http://[\\S|\\p{Punct}]*", "URL");
            }else{
                clear=dirty.toLowerCase().replaceAll("http://.*", "URL");
            }
        }else{
        clear=dirty.toLowerCase();
        }
        if(clear.indexOf("rt ")>=0){
            clear=clear.replace("rt ", "");
        }
        if(clear.indexOf("#")>=0){
            clear=clear.replaceAll("#","");
        }
        if(clear.indexOf("@")>=0){
            clear=clear.replaceAll("@\\w+", "REF");
            if(clear.indexOf("REF:")>=0){
                clear=clear.replaceAll("REF:", "");
            }
        }
        if(clear.indexOf("\n")>=0){
            clear=clear.replaceAll("\\n", " ");
        }
        if(clear.indexOf("& ")>=0){
            clear=clear.replaceAll("& ", "and ");
        }
        if(clear.indexOf("&")>=0){
            clear=clear.replaceAll("&", "");
        }
        return clear.trim();
    }
}
