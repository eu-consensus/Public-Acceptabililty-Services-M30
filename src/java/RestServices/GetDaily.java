/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestServices;

import Tools.databaseHandler;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 * REST Web Service
 *
 * @author ViP
 */
@Path("getDaily")
public class GetDaily {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetDaily
     */
    public GetDaily() {
    }

    /**
     * Retrieves representation of an instance of RestServices.GetDaily
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("scenario") String scenario, @QueryParam("objective") Integer objective, @QueryParam("page") Integer page) {
        System.out.println("Score getDaily started.");        
//System.out.println("Service Started");
        databaseHandler dbh=new databaseHandler();
        //System.out.println("dbh created");
        //dbh.checkTables();
        //System.out.println("tables checked");
        if(page==null || page<=0) page=1;
        //System.out.println("page checked");
        return dbh.readDailyLogs(scenario, objective, page);
    }

    /**
     * PUT method for updating or creating an instance of GetDaily
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
