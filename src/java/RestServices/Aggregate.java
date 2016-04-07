/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestServices;

import Tools.databaseHandler;
import java.util.ArrayList;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author ViP
 */
@Path("aggregate")
public class Aggregate {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Aggregate
     */
    public Aggregate() {
    }

    /**
     * Retrieves representation of an instance of RestServices.Aggregate
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("scenario") String scenario, @QueryParam("objective") Integer objective) {
        ArrayList<String> objectiveNames=new ArrayList<String>();
        if(scenario.equalsIgnoreCase("transportation")){
            objectiveNames.add("Change in Level of Service");
            objectiveNames.add("% change of Accidents cost");
            objectiveNames.add("% change of Air pollution (external) cost");
            objectiveNames.add("% change of Noise (external) cost");
            objectiveNames.add("Relative Operational Cost");
            objectiveNames.add("Availability of alternative routes and modes");
        }else if(scenario.equalsIgnoreCase("biofuel")){
            objectiveNames.add("Biodiversity");
            objectiveNames.add("CO2 Emissions");
            objectiveNames.add("Forest Land");
            objectiveNames.add("Price of Food");
        }
        
        databaseHandler dbh=new databaseHandler();
        ArrayList<Double[]> scores=dbh.readObjectiveScores(scenario, objective);
        Double[] sums=new Double[2];
        sums[0]=0.0;
        sums[1]=0.0;
        for (int i = 0; i < scores.size(); i++) {
            sums[0]+=scores.get(i)[0];
            sums[1]+=scores.get(i)[1];
        }
        sums[0]/=scores.size();
        sums[1]/=scores.size();
        return "{\"ObjectiveName\":\""+objectiveNames.get(objective-1)+"\",\"SOS\":"+sums[0]+",\"SOF\":"+sums[1]+"}";
    }

    /**
     * PUT method for updating or creating an instance of Aggregate
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
