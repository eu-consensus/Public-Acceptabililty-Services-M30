/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestServices;

import Tools.BaselineAnalysisTools;
import Tools.TopicAnalysisTools;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import Tools.databaseHandler;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author ViP
 */
@Path("scores/xml")
public class Scoresxml {

    @Context
    private UriInfo context;
    private databaseHandler dbh;

    /**
     * Creates a new instance of ScoresXml
     */
    public Scoresxml() {
        dbh=new databaseHandler();
        dbh.checkTables();
    }

    /**
     * Retrieves representation of an instance of RestServices.Scoresxml
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml(@QueryParam("uri") String uri,@QueryParam("keyword") String keyword,@QueryParam("datatag") String datatag) {
        try {
            //Reading source XML file
            ArrayList<String> texts=new ArrayList<String>();
            String text="";
            Pattern uriPatt=Pattern.compile("(\\w+?)://");
            Matcher uriMatch=uriPatt.matcher(uri);
            InputStream in=null;
            if(uriMatch.find()){
            if(!uriMatch.group(1).equals("file")){
                URL url = new URL(uri);
                URLConnection urlConnection = url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            }else{
                FileInputStream url = new FileInputStream(uri);
                in = new BufferedInputStream(url);
            }}else{
                FileInputStream url = new FileInputStream(uri);
                in = new BufferedInputStream(url);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            long l=0;
            if(in!=null)
                System.out.println("File found. Reading...");
            while ((strLine = br.readLine()) != null) {
                text+=strLine+"\n";
                if(text.contains("</"+datatag+">")){
                    texts.add(((text.split("<"+datatag+">"))[1].split("</"+datatag+">"))[0]);
                    text="";
                }
            }
            br.close();
            in.close();
            System.out.println("Parsed "+texts.size()+" documents.");
            
            //Calculating SOF
            DecimalFormat df = new DecimalFormat("#.####");
            System.out.println("Calculating Score Of Frequency...");
            TopicAnalysisTools tat=new TopicAnalysisTools();
            Double sof=tat.countFrequencyOld(texts,keyword);
            if(sof==null) return "<xml><result>Source XML not accessible, empty or datatag is wrong.</result></xml>";
            System.out.println("Score of Frequency for word '"+keyword+"' is "+df.format(sof));
            //http://www.w3schools.com/xml/simple.xml <description>
            
            //Calculating SOS
            System.out.println("Calculating Score Of Sentiment...");
            BaselineAnalysisTools bat=new BaselineAnalysisTools();
            bat.prepareTools();
            for (int i = 0; i < texts.size(); i++) {
                if(!texts.get(i).toLowerCase().contains(keyword.toLowerCase())){
                    texts.remove(i);
                    i--;
                }
            }
            Double sos=bat.SentiWordNetMeanAnalysisOld(texts,"");
            System.out.println("Score of Sentiment for word '"+keyword+"' is "+df.format(sos));
            
            //Storing to database
            System.out.println("Storing to database..");
            boolean suc=dbh.insert(keyword, sos, sof);
            
            System.out.println("Operation Completed for keyword: "+keyword+".");
            if(suc)
            return "<xml><result>Success</result></xml>";
            else
            return "<xml><result>Fail</result></xml>";  
        } catch (MalformedURLException ex) {
            return "<xml><result>"+ex.getMessage()+"</result></xml>";
        } catch (IOException ex) {
            return "<xml><result>"+ex.getMessage()+"</result></xml>";
        }
    }

    /**
     * PUT method for updating or creating an instance of Scoresxml
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
