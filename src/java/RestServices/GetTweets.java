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
@Path("GetTweets")
public class GetTweets {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetTweets
     */
    public GetTweets() {
    }

    /**
     * Retrieves representation of an instance of RestServices.GetTweets
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("keyword") String keyword,@QueryParam("date") String date) {
        System.out.println("Score getTweets started by client.");
        databaseHandler dbh=new databaseHandler();
        dbh.checkTables();
        return dbh.readTweetsString(keyword, date);
    }

    /**
     * PUT method for updating or creating an instance of GetTweets
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
