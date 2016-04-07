/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestServices;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author ViP
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(RestServices.Aggregate.class);
        resources.add(RestServices.GetBatchSentimentResource.class);
        resources.add(RestServices.GetDaily.class);
        resources.add(RestServices.GetSentimentResource.class);
        resources.add(RestServices.GetTargetedSentimentResource.class);
        resources.add(RestServices.GetTweets.class);
        resources.add(RestServices.Scoresfile.class);
        resources.add(RestServices.Scoresretrace.class);
        resources.add(RestServices.Scoresservice.class);
        resources.add(RestServices.Scoresserviceal.class);
        resources.add(RestServices.Scoresserviceall_old.class);
        resources.add(RestServices.Scoresxml.class);
        resources.add(RestServices.Test.class);
    }
    
}
