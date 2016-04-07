/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import Entities.CustomStatus;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ViP
 */
public class databaseHandler {
    // JDBC driver name and database URL
   //String JDBC_DRIVER;  
   //String DB_URL;

   //  Database credentials
   //String USER;
   //String PASS;
    
    private static DataSource datasource;
    //private static DataSource datasourceExternal;

    public databaseHandler() {
        //if(JDBC_DRIVER==null){
        //    this.JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
        //}else{
        //    this.JDBC_DRIVER = JDBC_DRIVER;
        //}
        //this.DB_URL = DB_URL;
        //this.USER = USER;
        //this.PASS = PASS;
        if(datasource==null){
            try{
                datasource = (DataSource) new InitialContext().lookup(GlobalVarsStore.databasePool);
            }
            catch (NamingException e){
                    e.printStackTrace();
            }
        }
        /*if(datasourceExternal==null){
            try{
                datasourceExternal = (DataSource) new InitialContext().lookup(GlobalVarsStore.databasePoolExternal);
            }
            catch (NamingException e){
                    e.printStackTrace();
            }
        }*/
    }
    
   public boolean checkTables(){
        Connection conn = null;
        Statement stmt = null;
        int inserted=0;
        try{
           //Class.forName(this.JDBC_DRIVER);
           //conn = DriverManager.getConnection(DB_URL,USER,PASS);
           conn = datasource.getConnection();
           stmt = conn.createStatement();
            //System.out.println("Checking tables");
           String sql = "CREATE TABLE IF NOT EXISTS `PublicAcceptabilityScores` (`Keyword` varchar(120) NOT NULL,`SOS` double NOT NULL,`SOF` double NOT NULL, PRIMARY KEY (`Keyword`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
           //System.out.println("PublicAcceptabilityScores checked");
           inserted = stmt.executeUpdate(sql);
           sql = "CREATE TABLE IF NOT EXISTS `PublicAcceptabilityScoresDaily` (`Keyword` varchar(120) NOT NULL,`SOS` double NOT NULL,`SOF` double NOT NULL,`Date` date NOT NULL,PRIMARY KEY (`Keyword`,`Date`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
           //System.out.println("PublicAcceptabilityScoresDaily checked");
           inserted += stmt.executeUpdate(sql);
           sql = "CREATE TABLE IF NOT EXISTS `PublicAcceptabilityTweets` (`Keyword` varchar(120) NOT NULL,`Tweet` varchar(767) NOT NULL,`SOS` double DEFAULT NULL,`UserScore` double DEFAULT NULL,`UserCount` int(11) NOT NULL DEFAULT '0',`parseDate` date DEFAULT NULL,`tweetID` bigint(20) NOT NULL,`uploadDate` date DEFAULT NULL,PRIMARY KEY (`tweetID`),UNIQUE KEY `Tweet_UNIQUE` (`Tweet`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
           //System.out.println("PublicAcceptabilityTweets checked");
           inserted += stmt.executeUpdate(sql);
           stmt.close();
           conn.close();
        }catch(SQLException se){se.printStackTrace(); return false;
        }catch(Exception e){e.printStackTrace(); return false;
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try
        if(inserted>0) return true;
        else return false;
   }
   
   public String readTweetsString (String keyword, String date){
       String res="";
        Connection conn = null;
        Statement stmt = null;
        try{
           //Class.forName(this.JDBC_DRIVER);
           //conn = DriverManager.getConnection(DB_URL,USER,PASS);
            //System.out.println("Reading tweets for keyword "+keyword+" and date "+date+".");
           conn = datasource.getConnection();
           stmt = conn.createStatement();
           String sql="";
           if(date!=null && date.length()>4)
                sql = "SELECT `Tweet`,`SOS`,`parseDate`,`Keyword` FROM PublicAcceptabilityTweets WHERE `Keyword`='"+keyword+"' AND `parseDate`='"+date+"';";
           else
                sql = "SELECT `Tweet`,`SOS`,`parseDate`,`Keyword` FROM PublicAcceptabilityTweets WHERE `Keyword`='"+keyword+"';";
           //System.out.println(sql);
           ResultSet rs = stmt.executeQuery(sql);
           try{
            res=convertTweets(rs).toString();
           }catch(Exception ex){res=ex.getMessage();}
           stmt.close();
           conn.close();
        }catch(SQLException se){se.printStackTrace(); return null;
        }catch(Exception e){e.printStackTrace();  return null;
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try
        
       return res;
   }
   
   public ArrayList<ArrayList<Integer>> readScenarioTotal(ArrayList<ArrayList<String>> keywords, String dateSince, String dateUntill){
       /* ArrayList<String> scenarioKeys=new ArrayList<String>();
        if(scenario.equalsIgnoreCase("transportation")){
                    scenarioKeys.addAll(ccn.transportKeywords1);
                    scenarioKeys.addAll(ccn.transportKeywords2);
                    scenarioKeys.addAll(ccn.transportKeywords3);
                    scenarioKeys.addAll(ccn.transportKeywords4);
                    scenarioKeys.addAll(ccn.transportKeywords5);
                    scenarioKeys.addAll(ccn.transportKeywords6);
                }else if(scenario.trim().toLowerCase().equals("biofuel")){
                    scenarioKeys.addAll(ccn.biofuelKeywords1);
                    scenarioKeys.addAll(ccn.biofuelKeywords2);
                    scenarioKeys.addAll(ccn.biofuelKeywords3);
                    scenarioKeys.addAll(ccn.biofuelKeywords4);
                }
        */
       ArrayList<ArrayList<Integer>> res=new ArrayList<ArrayList<Integer>>();
       ArrayList<Integer> semiTotal=null;
       Connection conn = null;
       Statement stmt = null;
       try{
           //Class.forName(this.JDBC_DRIVER);
           //conn = DriverManager.getConnection(DB_URL,USER,PASS);
            //System.out.println("Reading tweets for keyword "+keyword+" and date "+date+".");
           conn = datasource.getConnection();
           stmt = conn.createStatement();
           String sql="";
           for (int i = 0; i < keywords.size(); i++) {
               semiTotal=new ArrayList<Integer>();
               for (int j = 0; j < keywords.get(i).size(); j++) {
                    sql = "SELECT COUNT(`uploadDate`) FROM PublicAcceptabilityTweets WHERE `Keyword`='"+keywords.get(i).get(j)+"' AND `parseDate`>='"+dateSince+"' AND `parseDate`<'"+dateUntill+"';";
                    ResultSet rs = stmt.executeQuery(sql);
                    int sum=0;
                    while(rs.next()){
                        sum+=rs.getInt(1);
                    }
                    semiTotal.add(sum);
               }
               res.add(semiTotal);
           }
           stmt.close();
           conn.close();
       }catch(SQLException se){se.printStackTrace(); return null;
       }catch(Exception e){e.printStackTrace();  return null;
       }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
       }//end try
       
       return res;
   }
   
   public ArrayList<CustomStatus> readPastTweets (String keyword, String dateSince, String dateUntill){
        //dateSince in yyyy-MM-dd format
        ArrayList<CustomStatus> res=new ArrayList<CustomStatus>();
        Connection conn = null;
        Statement stmt = null;
        try{
           //Class.forName(this.JDBC_DRIVER);
           //conn = DriverManager.getConnection(DB_URL,USER,PASS);
            //System.out.println("Reading tweets for keyword "+keyword+" and date "+date+".");
           conn = datasource.getConnection();
           stmt = conn.createStatement();
           String sql="";
           sql = "SELECT `Tweet`,`SOS`,`uploadDate`,`Keyword`,`tweetID` FROM PublicAcceptabilityTweets WHERE `Keyword`='"+keyword+"' AND `parseDate`>='"+dateSince+"' AND `parseDate`<'"+dateUntill+"' ORDER BY `uploadDate` DESC;";
           //System.out.println(sql);
           ResultSet rs = stmt.executeQuery(sql);
           while(rs.next()){
               String tdate=null;
               if(rs.getDate(3)!=null)
               tdate=rs.getDate(3).toString();
               //System.out.println("tdate = "+rs.getDate(3));
               res.add(new CustomStatus(rs.getLong(5), rs.getDouble(2),tdate,rs.getString(1),0,0,rs.getString(4)));
           }
           stmt.close();
           conn.close();
        }catch(SQLException se){se.printStackTrace(); return null;
        }catch(Exception e){e.printStackTrace();  return null;
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try
        
       return res;
   }
   
   public ArrayList<CustomStatus> readTweets (String keyword, String dateSince){
        //dateSince in yyyy-MM-dd format
        ArrayList<CustomStatus> res=new ArrayList<CustomStatus>();
        Connection conn = null;
        Statement stmt = null;
        try{
           //Class.forName(this.JDBC_DRIVER);
           //conn = DriverManager.getConnection(DB_URL,USER,PASS);
            //System.out.println("Reading tweets for keyword "+keyword+" and date "+date+".");
           conn = datasource.getConnection();
           stmt = conn.createStatement();
           String sql="";
           if(dateSince!=null && dateSince.length()>4)
                sql = "SELECT `Tweet`,`SOS`,`uploadDate`,`Keyword`,`tweetID` FROM PublicAcceptabilityTweets WHERE `Keyword`='"+keyword+"' AND `parseDate`>='"+dateSince+"' ORDER BY `uploadDate` DESC;";
           else
                sql = "SELECT `Tweet`,`SOS`,`uploadDate`,`Keyword`,`tweetID` FROM PublicAcceptabilityTweets WHERE `Keyword`='"+keyword+"' ORDER BY `uploadDate` DESC;";
           //System.out.println(sql);
           ResultSet rs = stmt.executeQuery(sql);
           while(rs.next()){
               String tdate=null;
               if(rs.getDate(3)!=null)
               tdate=rs.getDate(3).toString();
               //System.out.println("tdate = "+rs.getDate(3));
               res.add(new CustomStatus(rs.getLong(5), rs.getDouble(2),tdate,rs.getString(1),0,0,rs.getString(4)));
           }
           stmt.close();
           conn.close();
        }catch(SQLException se){se.printStackTrace(); return null;
        }catch(Exception e){e.printStackTrace();  return null;
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try
        
       return res;
   }
   
   private static JSONArray convertTweets( ResultSet rs )
    throws SQLException, JSONException
  {
    JSONArray json = new JSONArray();
    ArrayList<String> dates=new ArrayList<String>();
    ArrayList<JSONArray> inners=new ArrayList<JSONArray>();
    
    while(rs.next()) {
      JSONObject objInner = new JSONObject();
      objInner.put("Tweet", rs.getString(1)); 
      objInner.put("SOS", rs.getDouble(2)); 
      objInner.put("Keyword", rs.getString(4));
      String date=rs.getDate(3).toString();
      int index=dates.indexOf(date);
      if(index<0){
          dates.add(date);
          JSONArray jsonInner = new JSONArray();
          jsonInner.put(objInner);
          inners.add(jsonInner);
      }else{
          inners.get(index).put(objInner);
      }
    }
    
      for (int i = 0; i < dates.size(); i++) {
          JSONObject outer=new JSONObject();
          outer.put(dates.get(i),inners.get(i));
          json.put(outer);
      }
    
    return json;
  }
   
   public boolean insertTweet(String keyword, String Tweet, Double sos, Long tweetID, String tweetDate) {
        Connection conn = null;
        Statement stmt = null;
        int inserted=0;
        try{
            conn = datasource.getConnection();
            stmt = conn.createStatement();
            String sql;
            if(Tweet.length()>767) Tweet=Tweet.substring(0,767);
            if(tweetID!=null && tweetDate!=null){
                try{
                    DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                    Date result =  df.parse(tweetDate);
                    tweetDate = (result.getYear()+1900)+"-"+(result.getMonth()+1)+"-"+result.getDate();
                }catch(ParseException pex){}
                sql = "INSERT INTO PublicAcceptabilityTweets ( tweetID, uploadDate, Keyword, Tweet, SOS, UserScore, parseDate) VALUES ( "+tweetID+",'"+tweetDate+"','"+keyword+"','"+Tweet.replaceAll("'", "").replaceAll("`", " ")+"',"+sos+", NULL, NOW() ) ON DUPLICATE KEY UPDATE SOS=VALUES(SOS);";//, Tweet=VALUES(Tweet), parseDate=VALUES(parseDate), uploadDate=VALUES(uploadDate);";
            }
            else if(tweetID!=null)
                sql = "INSERT INTO PublicAcceptabilityTweets ( tweetID, uploadDate, Keyword, Tweet, SOS, UserScore, parseDate) VALUES ( "+tweetID+", NULL,'"+keyword+"','"+Tweet.replaceAll("'", "").replaceAll("`", " ")+"',"+sos+", NULL, NOW() ) ON DUPLICATE KEY UPDATE SOS=VALUES(SOS);";//, Tweet=VALUES(Tweet), parseDate=VALUES(parseDate);";
            else
                sql = "INSERT INTO PublicAcceptabilityTweets ( Keyword, Tweet, SOS, UserScore, parseDate) VALUES ( '"+keyword+"','"+Tweet.replaceAll("'", "").replaceAll("`", " ")+"',"+sos+", NULL, NOW() ) ON DUPLICATE KEY UPDATE SOS=VALUES(SOS);";
            inserted = stmt.executeUpdate(sql);
            //System.out.println(sql);
            stmt.close();
            conn.close();
         }catch(SQLException se){se.printStackTrace();
         }catch(Exception e){e.printStackTrace();
         }finally{
            //finally block used to close resources
            try{
               if(stmt!=null)
                  stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
               if(conn!=null)
                  conn.close();
            }catch(SQLException se){
               se.printStackTrace();
            }//end finally try
         }//end try
        /*try{
            conn = datasourceExternal.getConnection();
            stmt = conn.createStatement();
            String sql;
            if(Tweet.length()>767) Tweet=Tweet.substring(0,767);
            sql = "INSERT INTO data ( Tweet, Keyword, SOS) VALUES ( '"+Tweet.replaceAll("'", "")+"','"+keyword+"',"+sos+" ) ON DUPLICATE KEY UPDATE SOS=VALUES(SOS);";
            inserted = stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
         }catch(SQLException se){System.out.println(se.getMessage()); return false;
         }catch(Exception e){e.printStackTrace(); return false;
         }finally{
            //finally block used to close resources
            try{
               if(stmt!=null)
                  stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
               if(conn!=null)
                  conn.close();
            }catch(SQLException se){
               se.printStackTrace();
            }//end finally try
         }//end try*/
         if(inserted>0) return true;
         else return false;
   }

   public boolean insertPast(String keyword, Double sos, Double sof, String date) {
   Connection conn = null;
   Statement stmt = null;
   int inserted=0;
   try{
      //Class.forName(this.JDBC_DRIVER);
      //conn = DriverManager.getConnection(DB_URL,USER,PASS);
      conn = datasource.getConnection();
      stmt = conn.createStatement();
      String sql;
      sql = "INSERT INTO PublicAcceptabilityScoresDaily ( Keyword, SOF, SOS , Date ) VALUES ( '"+keyword+"',"+sof+","+sos+",'"+date+"' ) ON DUPLICATE KEY UPDATE SOF=VALUES(SOF), SOS=VALUES(SOS);";
      inserted = stmt.executeUpdate(sql);
      stmt.close();
      conn.close();
   }catch(SQLException se){se.printStackTrace(); return false;
   }catch(Exception e){e.printStackTrace(); return false;
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   if(inserted>0) return true;
   else return false;
   }
   
   public boolean insert(String keyword, Double sos, Double sof) {
   Connection conn = null;
   Statement stmt = null;
   int inserted=0;
   try{
      //Class.forName(this.JDBC_DRIVER);
      //conn = DriverManager.getConnection(DB_URL,USER,PASS);
      conn = datasource.getConnection();
      stmt = conn.createStatement();
      String sql;
      sql = "INSERT INTO PublicAcceptabilityScores ( Keyword, SOF, SOS ) VALUES ( '"+keyword+"',"+sof+","+sos+" ) ON DUPLICATE KEY UPDATE SOF=VALUES(SOF), SOS=VALUES(SOS);";
      //System.out.println(sql);
      inserted = stmt.executeUpdate(sql);
      sql = "INSERT INTO PublicAcceptabilityScoresDaily ( Keyword, SOF, SOS , Date ) VALUES ( '"+keyword+"',"+sof+","+sos+",CURDATE() ) ON DUPLICATE KEY UPDATE SOF=VALUES(SOF), SOS=VALUES(SOS);";
      inserted = stmt.executeUpdate(sql);
      stmt.close();
      conn.close();
   }catch(SQLException se){se.printStackTrace(); return false;
   }catch(Exception e){e.printStackTrace(); return false;
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   if(inserted>0) return true;
   else return false;
}
   public ArrayList<Double[]> readObjectiveScores(String scenario, Integer objective){
       ArrayList<Double[]> res=new ArrayList<Double[]>();
       CrawlersConnector ccn=new CrawlersConnector();
       ArrayList<String> keys=null;
        if(scenario.equalsIgnoreCase("transportation")){
            if(objective==1){keys=ccn.transportKeywords1;}
            else if(objective==2){keys=ccn.transportKeywords2;}
            else if(objective==3){keys=ccn.transportKeywords3;}
            else if(objective==4){keys=ccn.transportKeywords4;}
            else if(objective==5){keys=ccn.transportKeywords5;}
            else if(objective==6){keys=ccn.transportKeywords6;}
        }else if(scenario.equalsIgnoreCase("biofuel")){
            if(objective==1){keys=ccn.biofuelKeywords1;}
            else if(objective==2){keys=ccn.biofuelKeywords2;}
            else if(objective==3){keys=ccn.biofuelKeywords3;}
            else if(objective==4){keys=ccn.biofuelKeywords4;}
        }
        String keyChain="WHERE Keyword = '";
        int i;
        for (i = 0; i < keys.size()-1; i++) {
           keyChain+=keys.get(i)+"' OR Keyword = '";
        }
        keyChain+=keys.get(i)+"';";
       
        Connection conn = null;
        Statement stmt = null;
        try{
           //Class.forName(this.JDBC_DRIVER);
           //conn = DriverManager.getConnection(DB_URL,USER,PASS);
           conn = datasource.getConnection();
           stmt = conn.createStatement();
           String sql = "SELECT SOS,SOF FROM PublicAcceptabilityScores "+keyChain;
           ResultSet rs = stmt.executeQuery(sql);
           while(rs.next()){
               Double[] cur=new Double[2];
               cur[0]=rs.getDouble("SOS");
               cur[1]=rs.getDouble("SOF");
               res.add(cur);
           }
           stmt.close();
           conn.close();
        }catch(SQLException se){se.printStackTrace(); return null;
        }catch(Exception e){e.printStackTrace();  return null;
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try
        
       return res;
   }
   
   public String readDailyLogs(String scenario, Integer objective, Integer page){
       ArrayList<String> objectiveNames=new ArrayList<String>();
       String res="";
       CrawlersConnector ccn=new CrawlersConnector();
       ArrayList<String> keys=null;
       //System.out.println("method triggered");
        if(scenario.equalsIgnoreCase("transportation")){
            objectiveNames.add("Change in Level of Service");
            objectiveNames.add("% change of Accidents cost");
            objectiveNames.add("% change of Air pollution (external) cost");
            objectiveNames.add("% change of Noise (external) cost");
            objectiveNames.add("Relative Operational Cost");
            objectiveNames.add("Availability of alternative routes and modes");
            if(objective==1){keys=ccn.transportKeywords1;}
            else if(objective==2){keys=ccn.transportKeywords2;}
            else if(objective==3){keys=ccn.transportKeywords3;}
            else if(objective==4){keys=ccn.transportKeywords4;}
            else if(objective==5){keys=ccn.transportKeywords5;}
            else if(objective==6){keys=ccn.transportKeywords6;}
        }else if(scenario.equalsIgnoreCase("biofuel")){
            objectiveNames.add("Biodiversity");
            objectiveNames.add("CO2 Emissions");
            objectiveNames.add("Forest Land");
            objectiveNames.add("Price of Food");
            if(objective==1){keys=ccn.biofuelKeywords1;}
            else if(objective==2){keys=ccn.biofuelKeywords2;}
            else if(objective==3){keys=ccn.biofuelKeywords3;}
            else if(objective==4){keys=ccn.biofuelKeywords4;}
        }
        //System.out.println("keys found");
        String keyChain="Keyword = '";
        int i;
        for (i = 0; i < keys.size()-1; i++) {
           keyChain+=keys.get(i)+"' OR Keyword = '";
        }
        keyChain+=keys.get(i)+"'";
       //System.out.println("keychain created");
        Connection conn = null;
        Statement stmt = null;
        try{
           //Class.forName(this.JDBC_DRIVER);
           //conn = DriverManager.getConnection(DB_URL,USER,PASS);
           conn = datasource.getConnection();
           stmt = conn.createStatement();
           String sql = "SELECT * FROM PublicAcceptabilityScoresDaily WHERE `Date` > DATE_ADD(CURDATE(), INTERVAL -"+(page*30)+" DAY) AND `Date` <= DATE_ADD(CURDATE(), INTERVAL -"+((page-1)*30)+" DAY) AND ("+keyChain+");";
           ResultSet rs = stmt.executeQuery(sql);
           try{
            JSONObject objName=new JSONObject();
            objName.put("Scenario", scenario);
            objName.put("Objective", objectiveNames.get(objective-1));
            //System.out.println("response received");
            res=convertDailyLogs(rs).put(objName).toString();
            //System.out.println("conversion done");
           }catch(Exception ex){res=ex.getMessage();}
           stmt.close();
           conn.close();
        }catch(SQLException se){se.printStackTrace(); return null;
        }catch(Exception e){e.printStackTrace();  return null;
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try
        //System.out.println("method end");
       return res;
   }
   
   private static JSONArray convertDailyLogs( ResultSet rs )
    throws SQLException, JSONException
  {
    JSONArray json = new JSONArray();
    ArrayList<String> dates=new ArrayList<String>();
    ArrayList<JSONArray> inners=new ArrayList<JSONArray>();
    
    while(rs.next()) {
      JSONObject objInner = new JSONObject();
      objInner.put("SOS", rs.getDouble(2)); 
      objInner.put("SOF", rs.getDouble(3)); 
      objInner.put("Keyword", rs.getString(1));
      String date=rs.getDate(4).toString();
      int index=dates.indexOf(date);
      if(index<0){
          dates.add(date);
          JSONArray jsonInner = new JSONArray();
          jsonInner.put(objInner);
          inners.add(jsonInner);
      }else{
          inners.get(index).put(objInner);
      }
    }
    
      for (int i = 0; i < dates.size(); i++) {
          JSONObject outer=new JSONObject();
          outer.put(dates.get(i),inners.get(i));
          json.put(outer);
      }
    
    return json;
  }
   
   private static JSONArray convert( ResultSet rs )
    throws SQLException, JSONException
  {
    JSONArray json = new JSONArray();
    ResultSetMetaData rsmd = rs.getMetaData();
    int numColumns = rsmd.getColumnCount();
    
    while(rs.next()) {
      JSONObject obj = new JSONObject();

      for (int i=1; i<numColumns+1; i++) {
        String column_name = rsmd.getColumnName(i);

        if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
         obj.put(column_name, rs.getArray(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
         obj.put(column_name, rs.getInt(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
         obj.put(column_name, rs.getBoolean(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
         obj.put(column_name, rs.getBlob(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
         obj.put(column_name, rs.getDouble(column_name)); 
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
         obj.put(column_name, rs.getFloat(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
         obj.put(column_name, rs.getInt(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
         obj.put(column_name, rs.getNString(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
         obj.put(column_name, rs.getString(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
         obj.put(column_name, rs.getInt(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
         obj.put(column_name, rs.getInt(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
         obj.put(column_name, rs.getDate(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
        obj.put(column_name, rs.getTimestamp(column_name));   
        }
        else{
         obj.put(column_name, rs.getObject(column_name));
        }
      }

      json.put(obj);
    }

    return json;
  }
}
