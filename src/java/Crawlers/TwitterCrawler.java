/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Crawlers;

import Entities.CustomStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author ViP
 */
public class TwitterCrawler {

    Twitter twitter;
    private int secsForRespawn;

    public TwitterCrawler() {
        secsForRespawn=0;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("7JmKYPf5FT6pNsv4buVTKA")
                .setOAuthConsumerSecret("vt0FQ5SE5C6UY18fqmHL7Hbfkdnj5cAF3dAYVMP0")
                .setOAuthAccessToken("359447855-oE9GMIqbe8zCqJeklXvxYWPqS6kP7DJD98KQ56fI")
                .setOAuthAccessTokenSecret("FWnVVSPqk30W0htd5QXU5PXZsR8VgSKWngsLu7WycBjRF");
        twitter = new TwitterFactory(cb.build()).getInstance();
        
    }
    
    public TwitterCrawler(String PKey, String Skey, String PToken, String SToken) {
        secsForRespawn=0;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(PKey)
                .setOAuthConsumerSecret(Skey)
                .setOAuthAccessToken(PToken)
                .setOAuthAccessTokenSecret(SToken);
        twitter = new TwitterFactory(cb.build()).getInstance();
        
    }
    
    public RateLimitStatus getRateLimitStatus() throws TwitterException{
        Map<String, RateLimitStatus> mp=twitter.getRateLimitStatus();
        return mp.get("/application/rate_limit_status");
    }

    public List<CustomStatus> search(String keyword, String since) {
        Query query = new Query(keyword);
        query.count(100);
        query.lang("en");
        query.since(since);
        QueryResult qr;
        try {
            List<Status> qrTweets;
            qr = twitter.search(query);
            qrTweets = qr.getTweets();
            try {
                for (int i = 0; i < 149; i++) {
                    query = qr.nextQuery();
                    qr = twitter.search(query);
                    qrTweets.addAll(qr.getTweets());
                }
            } catch (Exception exe) {
                //exe.printStackTrace();
            }
            //System.out.println(qr.getRateLimitStatus().getRemaining());
            List<CustomStatus> result=new ArrayList<CustomStatus>();
            for(int i=0;i<qrTweets.size();i++){
                result.add(new CustomStatus(qrTweets.get(i),keyword));
            }
            return result;
        } catch (TwitterException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<CustomStatus> safeSearch(String keyword, String since) {
        Query query = new Query(keyword);
        query.count(100);
        query.lang("en");
        query.since(since);
        QueryResult qr;
        System.out.println("Search started for "+keyword+" since:"+since);
        try {
            List<Status> qrTweets;
            RateLimitStatus rt;
            try{
                rt=twitter.getRateLimitStatus().get("/search/tweets");
            }catch(TwitterException ex){
                rt=new RateLimitStatus() {
                    
                    @Override
                    public int getRemaining() {
                        return 0;
                    }

                    @Override
                    public int getRemainingHits() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public int getLimit() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public int getResetTimeInSeconds() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public int getSecondsUntilReset() {
                        if(secsForRespawn>0) return secsForRespawn;
                        else return 900;
                    }
                };
            }
            int remainingSearches=rt.getRemaining();
            //System.out.println("Searches Remaining: "+remainingSearches);
                    if(remainingSearches<2){
                         int secsLeft=rt.getSecondsUntilReset();
                         secsForRespawn=secsLeft;
                         System.out.println("Rate Limit Reached, waiting "+secsLeft+" seconds for refresh...");
                         Date start=new Date();
                         Date current=new Date();
                         int wait=secsLeft;
                         while(wait>=0){
                            int rem=secsLeft-((int)Math.round((current.getTime()-start.getTime())/1000));
                            if(rem<wait){
                                wait=rem;
                                //System.out.print(" "+wait+" ");
                            }
                            current=new Date();
                         }
                         System.out.println("Rate Limit Reseted.");
                    }
            qr = twitter.search(query);
            qrTweets = qr.getTweets();
            int total = qrTweets.size();
            int page = 1;
            System.out.println("Got "+qrTweets.size()+" tweets from page 1.");
                query=qr.nextQuery();
                while(query!=null && total<=30000) {
                    try {
                        try{
                            rt=twitter.getRateLimitStatus().get("/search/tweets");
                        }catch(TwitterException ex){
                            rt=new RateLimitStatus() {

                                @Override
                                public int getRemaining() {
                                    return 0;
                                }

                                @Override
                                public int getRemainingHits() {
                                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                }

                                @Override
                                public int getLimit() {
                                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                }

                                @Override
                                public int getResetTimeInSeconds() {
                                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                }

                                @Override
                                public int getSecondsUntilReset() {
                                    if(secsForRespawn>0) return secsForRespawn;
                                    else return 900;
                                }
                            };
                        }
                        remainingSearches=rt.getRemaining();
                        //System.out.println("Searches Remaining: "+remainingSearches);
                        if(remainingSearches<2){
                             int secsLeft=rt.getSecondsUntilReset();
                             secsForRespawn=secsLeft;
                             System.out.println("Rate Limit Reached, waiting "+secsLeft+" seconds for refresh...");
                             Date start=new Date();
                             Date current=new Date();
                             int wait=secsLeft;
                             while(wait>=0){
                                int rem=secsLeft-((int)Math.round((current.getTime()-start.getTime())/1000));
                                if(rem<wait){
                                    wait=rem;
                                    //System.out.print(" "+wait+" ");
                                }
                                current=new Date();
                             }
                             System.out.println("Rate Limit Reseted.");
                        }
                        qr = twitter.search(query);
                        qrTweets.addAll(qr.getTweets());
                        page++;
                        System.out.println("Got "+(qrTweets.size()-total)+" tweets from page "+page+".");
                        total=qrTweets.size();
                        query=qr.nextQuery();
                    } catch (Exception exe) {
                        exe.printStackTrace();
                    }
                }
            System.out.println("Got "+total+" tweets for "+keyword+".");
            //System.out.println(qr.getRateLimitStatus().getRemaining());
            List<CustomStatus> result=new ArrayList<CustomStatus>();
            try{
            for(int i=0;i<qrTweets.size();i++){
                result.add(new CustomStatus(qrTweets.get(i), keyword));
            }
            }catch(Exception exee){exee.printStackTrace();}
            return result;
        } catch (TwitterException e) {
            try{
              e.printStackTrace();
            }catch(NullPointerException exe){exe.printStackTrace();}
            return null;
        }
    }
    
    public CustomStatus getById(String text) throws TwitterException{
            CustomStatus cs=new CustomStatus(this.twitter.showStatus(Long.parseLong(text)),null);
            System.out.println(cs.getText());
            return cs;
    }
}
