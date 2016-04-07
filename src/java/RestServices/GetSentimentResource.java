/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestServices;

import Tools.BaselineAnalysisTools;
import Tools.GlobalVarsStore;
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

/**
 * REST Web Service
 *
 * @author ViP
 */
@Path("GetSentiment")
public class GetSentimentResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetSentimentResource
     */
    public GetSentimentResource() {
    }

    /**
     * Retrieves representation of an instance of RestServices.GetSentimentResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("data") String data) {
        System.out.println("data=\""+data+"\"");
        BaselineAnalysisTools bat = new BaselineAnalysisTools();
        bat.prepareTools();
        String[] bag=clear(data).split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
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
            try{
            JSONObject objInner = new JSONObject();
            objInner.put("SOS", sent); 
            objInner.put("CONFIDENCE", (confidenceClassification*(1-GlobalVarsStore.containmentConfidenceWeight))+(confidenceContainment*GlobalVarsStore.containmentConfidenceWeight));
                return objInner.toString();
            }catch(Exception ex){
                try {
                    JSONObject objInner = new JSONObject();
                    objInner.put("Error", ex.getMessage());  
                    return objInner.toString();
                } catch (JSONException ex1) {
                    Logger.getLogger(GetSentimentResource.class.getName()).log(Level.SEVERE, null, ex1);
                    return null;
                }
            }
    }

    /**
     * PUT method for updating or creating an instance of GetSentimentResource
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
