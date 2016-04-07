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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author ViP
 */
@Path("GetBatchSentiment")
public class GetBatchSentimentResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetBatchSentimentResource
     */
    public GetBatchSentimentResource() {
    }

    /**
     * Retrieves representation of an instance of RestServices.GetBatchSentimentResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("data") String datas) {
        JSONArray objOuter = new JSONArray();
        try {
            JSONArray inputJsonArray= new JSONArray(datas);
            String[] dataArray=new String[inputJsonArray.length()];
            long[] idsArray=new long[inputJsonArray.length()];
            for (int i = 0; i < inputJsonArray.length(); i++) {
                idsArray[i]=inputJsonArray.getJSONObject(i).getLong("id");
                dataArray[i]=inputJsonArray.getJSONObject(i).getString("data");
            }
            BaselineAnalysisTools bat = new BaselineAnalysisTools();
            bat.prepareTools();
            for (int i = 0; i < dataArray.length; i++) {
                String[] bag=clear(dataArray[i]).split("(?:(?:[^a-zA-Z]+')|(?:'[^a-zA-Z]+))|(?:[^a-zA-Z']+)");
                double sent=0.0;
                ArrayList<Double> weights=new ArrayList<Double>();
                int pos=0;
                int neg=0;
                int neu=0;
                double confidenceContainment=0.0;
                double confidenceClassification=0.0;
                for (int j = 0; j < bag.length; j++) {
                    Double res=GlobalVarsStore.lex.testWord(bag[j]);
                    if(res!=null){
                        weights.add(res);
                        confidenceContainment++;
                    }
                }
                Double totalSent=0.0;
                for (int j = 0; j < weights.size(); j++) {
                    totalSent+=weights.get(j);
                    if(weights.get(j)>0){
                        pos++;
                    }else if(weights.get(j)<0){
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
                JSONObject objInner = new JSONObject();
                try {
                    objInner.put("StringID", idsArray[i]);
                    objInner.put("SOS", sent);
                    objInner.put("CONFIDENCE", (confidenceClassification*(1-GlobalVarsStore.containmentConfidenceWeight))+(confidenceContainment*GlobalVarsStore.containmentConfidenceWeight));
                    objOuter.put(i,objInner);
                } catch (JSONException ex) {
                    Logger.getLogger(GetBatchSentimentResource.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(GetBatchSentimentResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return objOuter.toString();
    }

    /**
     * PUT method for updating or creating an instance of GetBatchSentimentResource
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
