
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package hw3_chunyi;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import java.util.Iterator;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
        
/**
 *
 * @author tracy
 */
public class Populate {

   final static String JDBCDriver = "oracle.jdbc.driver.OracleDriver";  
   final static String dbURL = "jdbc:oracle:thin:@//127.0.0.1/oracle";

   final static String business_json = "C:\\Users\\tracy\\Documents\\COEN280\\YelpDataset\\yelp_business.json";
   final static String check_json = "C:\\Users\\tracy\\Documents\\COEN280\\YelpDataset\\yelp_checkin.json";
   final static String review_json = "C:\\Users\\tracy\\Documents\\COEN280\\YelpDataset\\yelp_review.json";
   final static String user_json = "C:\\Users\\tracy\\Documents\\COEN280\\YelpDataset\\yelp_user.json";

   final static String username = "scott";
   final static String password = "tiger";
   private Connection conn;
   private String[] tables = {"checkin","review","users","categories","subcategory","maincategory", "business"};
   
   final static List<String> AllMainCategories = new ArrayList<String>();
 
   public static void main(String[] args) throws IOException, JSONException {
        // TODO code application logic here       
       Populate pop = new Populate();
       
       try {
           pop.initCategories();
           pop.initiateDB();
       } catch (SQLException e) {
           System.out.println(e.toString());
       }       
       int i = 0;        
    }
   private void initCategories(){
       AllMainCategories.add("Active Life");
       AllMainCategories.add("Arts & Entertainment");
       AllMainCategories.add("Automotive");
       AllMainCategories.add("Car Rental");
       AllMainCategories.add("Cafes");
       AllMainCategories.add("Beauty & Spas");
       AllMainCategories.add("Convenience Stores");
       AllMainCategories.add("Dentists");
       AllMainCategories.add("Doctors");
       AllMainCategories.add("Drugstores");
       AllMainCategories.add("Department Stores");
       AllMainCategories.add("Education");
       AllMainCategories.add("Event Planning & Services");
       AllMainCategories.add("Flowers & Gifts");
       AllMainCategories.add("Food");
       AllMainCategories.add("Health & Medical");
       AllMainCategories.add("Home Services");
       AllMainCategories.add("Home & Garden");
       AllMainCategories.add("Hospitals");
       AllMainCategories.add("Hotels & Travel");
       AllMainCategories.add("Hardware Stores");
       AllMainCategories.add("Grocery");
       AllMainCategories.add("Medical Centers");
       AllMainCategories.add("Nurseries & Gardening");
       AllMainCategories.add("Nightlife");
       AllMainCategories.add("Restaurants");
       AllMainCategories.add("Shopping");
       AllMainCategories.add("Transportation");
   }
   private void createConnection(){       
        try {
            Class.forName(JDBCDriver);
            conn = DriverManager.getConnection(dbURL, username, password);
        }catch(ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
  
   private void initiateDB() throws SQLException, IOException, JSONException{     
       createConnection();
       clearDB();
       parseJsonBusiness(conn, business_json);
       parseJsonUser(conn, user_json);       
       parseJsonCheck(conn, check_json);
       parseJsonReview(conn, review_json );
       
   }
    public static void parseJsonBusiness(Connection conn, String filePath) throws IOException, JSONException, SQLException{
       File file = new File(filePath);
       FileReader fileReader = new FileReader(file);
       BufferedReader bufferReader = new BufferedReader(fileReader);
       String line = null;
       while((line = bufferReader.readLine()) != null){
           JSONObject obj = new JSONObject(line);
           insertIntoBusiness(obj, conn);
           insertIntoMainCategory(obj, conn);
       }        
    }
   public static void parseJsonUser(Connection conn, String filePath) throws IOException, JSONException, SQLException{
       File file = new File(filePath);
       FileReader fileReader = new FileReader(file);
       BufferedReader bufferReader = new BufferedReader(fileReader);
       String line = null;
       while((line = bufferReader.readLine()) != null){
           JSONObject obj = new JSONObject(line);
           insertIntoUser(obj, conn);
       }                 
   }
   public static void parseJsonCheck(Connection conn, String filePath) throws IOException, JSONException, SQLException{
       File file = new File(filePath);
       FileReader fileReader = new FileReader(file);
       BufferedReader bufferReader = new BufferedReader(fileReader);
       String line = null;
       while((line = bufferReader.readLine()) != null){
           JSONObject obj = new JSONObject(line);
           insertIntoCheckin(obj, conn);
       }                        
   }
   
   public static void parseJsonReview(Connection conn, String filePath) throws IOException, JSONException, SQLException{
       File file = new File(filePath);
       FileReader fileReader = new FileReader(file);
       BufferedReader bufferReader = new BufferedReader(fileReader);
       String line = null;
       while((line = bufferReader.readLine()) != null){
           JSONObject obj = new JSONObject(line);
           insertIntoReview(obj, conn);
       }                 
   }
   
   private static void insertIntoUser(JSONObject obj, Connection conn)throws JSONException, SQLException{
       /*
      CREATE TABLE Users (
        UId VARCHAR (50),
        NAME VARCHAR (50),
        REVIEW_CNT INT,
        AVG_stars NUMBER,
        Friend_CNT INT
        YELP_SINCE VARCHAR
        );  
       */
       String sql = "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?)";
       String user_id = obj.getString("user_id");
       String name = obj.getString("name");
       int review_cnt = obj.getInt("review_count");
       double avg_stars = obj.getDouble("average_stars");
       JSONArray friends = obj.getJSONArray("friends");
       int friend_cnt = friends.length();
       String yelp_since = obj.getString("yelping_since");
       PreparedStatement  statement = conn.prepareStatement(sql);
       statement.setString(1, user_id);
       statement.setString(2, name);
       statement.setInt(3, review_cnt);
       statement.setDouble(4, avg_stars);
       statement.setInt(5, friend_cnt);
       statement.setString(6, yelp_since);
       try{
           statement.executeUpdate();
           statement.close();
       }catch(SQLException e){
            System.err.println(sql);
            System.out.println(e.toString());
       }           
   }
    
   private static void insertIntoBusiness(JSONObject obj, Connection conn)throws JSONException, SQLException{
       /*
        CREATE TABLE Business (
            BID VARCHAR (50),
            City VARCHAR (30),
            State VARCHAR (5),
            BName VARCHAR (100),
            Stars NUMBER,
            Address VARCHAR (150),
            Review_cnt VARCHAR(10)
        );
       */
       
       String business_id = obj.getString("business_id");
       String city = obj.getString("city");
       String state = obj.getString("state");
       String bName = obj.getString("name");
       Double stars = obj.getDouble("stars");
       String address = obj.getString("full_address");
       int review_cnt = obj.getInt("review_count");
     
       String sql = "INSERT INTO business VALUES (?, ?, ?, ?, ?, ?, ?)";
       PreparedStatement  statement = conn.prepareStatement(sql);
       statement.setString(1, business_id);
       statement.setString(2, city);
       statement.setString(3, state);
       statement.setString(4, bName);
       statement.setDouble(5, stars);
       statement.setString(6, address);
       statement.setInt(7, review_cnt);
       try{
           statement.executeUpdate();
           statement.close();
       }catch(SQLException e){
            System.err.println(sql);
            System.out.println(e.toString());
       }           
   }
    private static void insertIntoMainCategory(JSONObject obj, Connection conn)throws JSONException, SQLException{
       List<String> Main_Categories = new ArrayList<String>();
       List<String> Sub_Categories = new ArrayList<String>();
       
       String business_id = obj.getString("business_id");
       String sql_main = "INSERT INTO MainCategory VALUES (?, ?)";
       PreparedStatement  statement_main = conn.prepareStatement(sql_main);
       statement_main.setString(1, business_id);
       
       String sql_sub = "INSERT INTO SubCategory VALUES (?, ?)";
       PreparedStatement  statement_sub = conn.prepareStatement(sql_sub);
       statement_sub.setString(1, business_id);
       
       JSONArray rawcategories = obj.getJSONArray("categories");
       for (int i = 0; i < rawcategories.length(); ++i) {
           String category = rawcategories.getString(i);
            if(AllMainCategories.contains(category)){
                Main_Categories.add(category);
            }
            else{
                Sub_Categories.add(category);
            }
       }       
       for (int i = 0; i < Main_Categories.size(); ++i){
                String category = Main_Categories.get(i);
                statement_main.setString(2,category );
                try{
                    statement_main.executeUpdate();               
                }catch(SQLException e){
                     System.err.println(sql_main);
                     System.out.println(e.toString());
                }               
       }
       for (int i = 0; i < Sub_Categories.size(); ++i){
                String category = Sub_Categories.get(i);
                statement_sub.setString(2,category );
                try{
                    statement_sub.executeUpdate();               
                }catch(SQLException e){
                     System.err.println(sql_sub);
                     System.out.println(e.toString());
                }               
       }       
       String sql_main_sub = "INSERT INTO Categories VALUES (?, ?)";
       PreparedStatement  statement_main_sub = conn.prepareStatement(sql_main_sub);
       
       for (int i = 0; i < Main_Categories.size(); i++){
           for (int j = 0; j < Sub_Categories.size(); j++ ){
                String m_category = Main_Categories.get(i);
                String s_category = Sub_Categories.get(j);
                statement_main_sub.setString(1,m_category );
                statement_main_sub.setString(2,s_category );
                try{
                    statement_main_sub.executeUpdate();               
                }catch(SQLException e){
                     System.err.println(sql_main_sub);
                     System.out.println(e.toString());
                }       
           }
       }        

       statement_main.close();
       statement_sub.close();
       statement_main_sub.close();
   }
  
    private static void insertIntoCheckin(JSONObject obj, Connection conn)throws JSONException, SQLException{
       /*
        CREATE TABLE Checkin (
            BID VARCHAR (50),
            HOUR INT,
            DAY  INT,
            NUM INT 
        );
       */ 

       String business_id = obj.getString("business_id");
       String sql = "INSERT INTO Checkin VALUES (?, ?, ?, ?)";
       PreparedStatement statement = conn.prepareStatement(sql);
       statement.setString(1, business_id);
//       JSONArray checkin_info = obj.getJSONArray("checkin_info");
       JSONObject checkin_info = obj.getJSONObject("checkin_info");
       Iterator<String> keysItr = checkin_info.keys();
       
       while(keysItr.hasNext())  {
            String key = keysItr.next();
            String value = checkin_info.getString(key);
            int pos = -1;
            for(int i = 0; i < key.length(); i++){
                if(key.charAt(i) == '-'){
                    pos = i;
                }
            }            
            String hour = key.substring(0,pos);
            String day = key.substring(pos+1);
            statement.setInt(2, Integer.parseInt(hour));
            statement.setInt(3, Integer.parseInt(day));
            statement.setInt(4, Integer.parseInt(value) );
//            statement.setString(2, loc);
            try{
                statement.executeUpdate();               
            }catch(SQLException e){
                 System.err.println(sql);
                 System.out.println(e.toString());
            }    
       }     
       statement.close();

   }
    private static void insertIntoReview(JSONObject obj, Connection conn)throws JSONException, SQLException{
//CREATE TABLE Review (
//    review_ID VARCHAR (50),
//    user_Id VARCHAR (50),
//    business_ID VARCHAR (50),
//    VOTES_CNT INT,
//    Stars INT,
//    review_DATE DATE,
//    review_text CLOB
//);

       String review_id = obj.getString("review_id");
       String user_id = obj.getString("user_id");
       String business_id = obj.getString("business_id");
       int stars = obj.getInt("stars");
       String review_date = obj.getString("date");
       String review_text = obj.getString("text");
       
       JSONObject votes = obj.getJSONObject("votes");
       Iterator<String> keysItr = votes.keys();
       int vote_count = 0;
       while(keysItr.hasNext())  {
            String key = keysItr.next();
            vote_count += votes.getInt(key);
       }
            
       String sql = "INSERT INTO Review VALUES (?, ?, ?, ?, ?, ?, ?)";
       PreparedStatement statement = conn.prepareStatement(sql);
       statement.setString(1, review_id);
       statement.setString(2, user_id);
       statement.setString(3, business_id);
       statement.setInt(4, vote_count);
       statement.setInt(5, stars);
       statement.setDate(6, java.sql.Date.valueOf(review_date));
       statement.setString(7, review_text);
       
       try{
           statement.executeUpdate();               
        }catch(SQLException e){
           System.err.println(sql);
           System.out.println(e.toString());
         }     
       statement.close();
   }
   private void clearDB() throws SQLException{
       for(String table : this.tables){
            deleteAllRows(table);
       }
   }
   
   public void deleteAllRows(String table) throws SQLException{
       Statement statement = conn.createStatement();
       statement.executeUpdate("DELETE FROM "+table);
   }
 
}


