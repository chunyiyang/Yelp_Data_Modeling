/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.GroupLayout;
import java.util.Arrays;
import net.proteanit.sql.DbUtils;
//import org.apache.commons.dbutils.DbUtils;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableModel;

/**
 * @author yuan
 */
public class Hw3 extends javax.swing.JFrame {

    final static String JDBCDriver = "oracle.jdbc.driver.OracleDriver";
    final static String dbURL = "jdbc:oracle:thin:@//127.0.0.1:1521/oracle";

    //  Database credentials
    final static String username = "scott";
    final static String password = "tiger";
    private Connection conn = null;
    private List<javax.swing.JCheckBox> countriesCheckBox = new ArrayList<javax.swing.JCheckBox>();
    private List<String> chooseCountries = new ArrayList<String>();

    private String ratingValue = "";
    private String ratingCondition = "=";
    private String NumOfReviewCondition = "=";
    private String NumOfReviewValue = "";
    private String fromYear = "";
    private String toYear = "";
    private String finalSql;
    
    private String UserReviewCount = "";
    private String UserReviewCountCondition = "";
    private String NumFriend = "";
    private String NumFriendCondition = "";
    private String NumAvgStars = "";
    private String NumAvgStarsCondition = "";
    private List<String> chooseMain = new ArrayList<String>();
    private List<String> chooseSub = new ArrayList<String>();
    private boolean isAnd = true;    
    private boolean bBusinessMode = true;
    /**
     * Creates new form Hw3
     */
    public Hw3() {
        createConnection(dbURL, username, password);
        System.out.println("Initiate GUI");
        initComponents();
        start();
    }

    private void start() {
        // get maincategories from db       
        // ****
        ArrayList<String> genres = getMaincategories();

        // add checkbox for each main category
        javax.swing.JPanel jPanelGenre = new javax.swing.JPanel();
        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanelGenre);
        jPanelGenre.setLayout(jPanelLayout);
        GroupLayout.ParallelGroup group = jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup seqGroup = jPanelLayout.createSequentialGroup();
        int i = 0;
        for (String genre : genres) {
            javax.swing.JCheckBox genreCheckBox = new javax.swing.JCheckBox();
            genreCheckBox.setText(genre);
            genreCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (genreCheckBox.isSelected()) {
                    // ****
                        chooseMain.add(genre);
                        clear4MainChange();
                        setSubBasedOnMain();
                        updateFinalSql();
                    } else {
                        chooseMain.remove(genre);
                        clear4MainChange();
                        setSubBasedOnMain();
                        updateFinalSql();
                    }
                }
            });
            group.addComponent(genreCheckBox);
            if (i == 0) {
                seqGroup.addContainerGap().addComponent(genreCheckBox);
            } else {
                seqGroup.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(genreCheckBox);
            }
        }
        seqGroup.addContainerGap(99, Short.MAX_VALUE);

        jPanelLayout.setHorizontalGroup(
                jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(group)
                                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
                jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(seqGroup)
        );
        GenresPane.setViewportView(jPanelGenre);
        pack();
    }
    private void setSubBasedOnMain() {
        // get genres from db
        // ****
//        ArrayList<String> countries = getCountriesBasedOnGenre();
        ArrayList<String> subcategories = getCountriesBasedOnGenre();

        // add checkbox for each genre
        javax.swing.JPanel jPanelGenre = new javax.swing.JPanel();
        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanelGenre);
        jPanelGenre.setLayout(jPanelLayout);
        GroupLayout.ParallelGroup group = jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup seqGroup = jPanelLayout.createSequentialGroup();
        int i = 0;
        for (String subcategory : subcategories) {
            javax.swing.JCheckBox countryCheckBox = new javax.swing.JCheckBox();
            countryCheckBox.setText(subcategory);
            countryCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (countryCheckBox.isSelected()) {
                        chooseSub.add(subcategory);
//                        clear4CountryChange();
                        updateFinalSql();
                    } else {
                        // fire country without this genre
                        chooseSub.remove(subcategory);
//                        clear4CountryChange();
                        updateFinalSql();
                    }

                }
            });
            countriesCheckBox.add(countryCheckBox);
            group.addComponent(countryCheckBox);
            if (i == 0) {
                seqGroup.addContainerGap().addComponent(countryCheckBox);
            } else {
                seqGroup.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(countryCheckBox);
            }
        }

        seqGroup.addContainerGap(99, Short.MAX_VALUE);

        jPanelLayout.setHorizontalGroup(
                jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(group)
                                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
                jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(seqGroup)
        );
        countrySPane.setViewportView(jPanelGenre);

        pack();

    }

    // ****
    private String generateSql4SubCGBasedOnMainCG() {
        // fire country with this genre
        String select = "Select DISTINCT C.SubCG\n";
        String from = "From Categories C";
        String where = "Where ";
        String order = "Order By C.SubCG";
        int index = 0;
        for (String mainCategory : chooseMain) {
                if (index++ == 0) {
                  where += "C.MainCG = "  + "'"  + mainCategory + "'" ;;
                } else {
                    where += " OR C.MainCG = " + "'" + mainCategory + "'";
                }
        }
        where += "\n";
        from += "\n";

        return select + from + where + order;

    }

    private ArrayList<String> getMaincategories() {

        ArrayList<String> maincategories = new ArrayList<String>();
        String sql = null;
        try {
            sql = "SELECT DISTINCT MAINCG FROM maincategory ORDER BY MAINCG";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            // Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                String maincategory = rs.getString("MAINCG");
                maincategories.add(maincategory);
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println("get maincategories Error:\n" + sql + "\n");

            ex.printStackTrace();
        }
        return maincategories;
    }
    // ****
    private ArrayList<String> getSubcategories() {

        ArrayList<String> subcategories = new ArrayList<String>();
        String sql = null;
        try {
//            sql = generateSql4CountriesBasedOnGenre();
            sql = "SELECT DISTINCT SUBCG FROM SubCategory ORDER BY SUBCG";
            updateFinalSql(sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            // Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                String subcategory = rs.getString("SUBCG");
                subcategories.add(subcategory);
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println("get subcategories Error:\n" + sql + "\n");
            ex.printStackTrace();
        }
        return subcategories;
    }
        
    private ArrayList<String> getCountriesBasedOnGenre() {

        ArrayList<String> countries = new ArrayList<String>();
        String sql = null;
        try {
            sql = generateSql4SubCGBasedOnMainCG();
            updateFinalSql(sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            // Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                String country = rs.getString("SubCG");
                countries.add(country);
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println("get country Error:\n" + sql + "\n");
            ex.printStackTrace();
        }
        return countries;
    }

    private String getMovieIdSqlBasedOnGenre() {
        // fire country with this genre

        if (chooseMain.isEmpty()) {
            return null;
        }

        String select = "Select DISTINCT B0.name\n";
        String from = "From ";
        String where = "Where ";

        if (isAnd) {
            int index = 0;
            String pre = "";
            String cur = null;

            for (int i = 0; i < chooseMain.size(); i++) {

                String mainCategory = chooseMain.get(i);
                cur = "B" + index;

                if (i == 0) {
                    from += "Business " + cur;
                    where += cur + ".MAINCG =" + "'" + mainCategory + "' ";
                } else {
                    from += ", GENRE " + cur;
                    where += "AND " + pre + ".MAINCG = " + cur + ".MAINCG "
                            + "AND " + cur + ".MAINCG =" + "'" + mainCategory + "' ";
                }
                pre = cur;
                index++;
            }
            where += "\n";
            from += "\n";

        } else {
            String cur = "G0";

            from += "Business " + cur;
            int index = 0;
            for (String mainCategory : chooseMain) {
                if (index++ == 0) {
                    where += "( G0.MAINCG = " + "'" + mainCategory + "'";
                } else {
                    where += " OR G0.MAINCG = " + "'" + mainCategory + "'";
                }
            }
            where += ")\n";
            from += "\n";
        }

        return select + from + where;

    }
        private String getReviewSql(){
        String strReviewFrom = ReviewFrom.getText();
        String strReviewTo = ReviewTo.getText();
        String strStarValue = StarValue.getText();
        String strStarComboBox = StarComboBox.getSelectedItem().toString();
        String strVoteValue = VoteValue.getText();        
        String strVoteComboBox = VoteComboBox.getSelectedItem().toString();

//        SELECT BID FROM review 
//        where review_date > strReviewFrom 
//                AND review_date < strReviewTo
//        GROUP BY BID
//        HAVING AVG(STARS) strStarComboBox strStarValue
//                AND SUM(VOTES_CNT ) strVoteComboBox strVoteValue
        
       String subQueryReview = "";
       Boolean bSubWhere = false;
       
       if(strReviewFrom.length() > 0){
           subQueryReview += " SELECT DISTINCT business_id FROM review WHERE review_date >=  TO_DATE( '" + strReviewFrom + "' , 'YYYY-MM-DD') \n";
           bSubWhere = true;
       }

       if(strReviewTo.length() > 0){
           if(!bSubWhere){
               subQueryReview += " SELECT DISTINCT business_id FROM review \n WHERE review_date <= TO_DATE( '" + strReviewTo + "'  , 'YYYY-MM-DD') \n";
               bSubWhere = true;
           }
           else{
               subQueryReview += " AND review_date <= TO_DATE( '" + strReviewTo + "'  , 'YYYY-MM-DD')  \n";
           }           
       }       
       if(strStarValue.length() > 0){
           if(!bSubWhere){
               subQueryReview += " SELECT DISTINCT  business_id FROM review \n GROUP BY business_id \n  HAVING AVG(Stars)  " + strStarComboBox +  " " + strStarValue + "\n";
               bSubWhere = true;
           }
           else{
               subQueryReview += " GROUP BY business_id \n  HAVING AVG(Stars)  " + strStarComboBox +  " " + strStarValue + "\n";
           }                      
       }

       if(strVoteValue.length() > 0){
           if(!bSubWhere){
               subQueryReview += " SELECT DISTINCT business_id FROM review \n GROUP BY business_id \n  HAVING SUM(VOTES_CNT)  " + strVoteComboBox +  " " + strVoteValue + "\n";
               bSubWhere = true;
           }          
           else if(strStarValue.length() == 0){
               subQueryReview += " GROUP BY business_id \n HAVING SUM(VOTES_CNT)  " + strVoteComboBox +  " " + strVoteValue + "\n";
           }           
           else{
               subQueryReview += " AND SUM(VOTES_CNT)  " + strVoteComboBox +  " " + strVoteValue + "\n";
           }
       }       
 
        return subQueryReview;
    }
        
    private String getCheckInSql(){
        String strCheckInFromDay = CheckInFromDay.getText();
        String strCheckInToDay = CheckInToDay.getText();
        String strCheckInFromHour = CheckInFromHour.getText();
        String strCheckInToHour = CheckInToHour.getText();
        String strCheckInComboBox = CheckInComboBox.getSelectedItem().toString();;
        String strCheckInValue = CheckInValue.getText();
 
       String subQueryCheckIn = "";
       Boolean bSubWhere = false;
        if(strCheckInValue.length() > 0 && isInteger(strCheckInValue)){
            subQueryCheckIn += " SELECT BID FROM checkin ";
            if(strCheckInFromDay.length() > 0 && isInteger(strCheckInFromDay)){
                if(!bSubWhere){
                    subQueryCheckIn += " WHERE ";
                    bSubWhere = true;
                }
                else{
                    subQueryCheckIn += " AND "; 
                }
                subQueryCheckIn += " DAY >= " + strCheckInFromDay + "\n";               
            }
            if(strCheckInToDay.length() > 0 && isInteger(strCheckInToDay)){
                if(!bSubWhere){
                    subQueryCheckIn += " WHERE ";
                    bSubWhere = true;
                }
                else{
                    subQueryCheckIn += " AND "; 
                }
                subQueryCheckIn += " DAY <= " + strCheckInToDay + "\n";               
            }
            if(strCheckInFromHour.length() > 0 && isInteger(strCheckInFromHour)){
                if(!bSubWhere){
                    subQueryCheckIn += " WHERE ";
                    bSubWhere = true;
                }
                else{
                    subQueryCheckIn += " AND "; 
                }
                subQueryCheckIn += " HOUR >= " + strCheckInFromHour + "\n";               
            }
            if(strCheckInToHour.length() > 0 && isInteger(strCheckInToHour)){
                if(!bSubWhere){
                    subQueryCheckIn += " WHERE ";
                    bSubWhere = true;
                }
                else{
                    subQueryCheckIn += " AND "; 
                }
                subQueryCheckIn += " HOUR < " + strCheckInToHour + "\n";               
            }
            subQueryCheckIn += " GROUP BY BID \n HAVING SUM(NUM) " + strCheckInComboBox + " " + strCheckInValue;  
        }
        return subQueryCheckIn;
    }
    
//    CREATE TABLE Review (
//    review_ID VARCHAR (50) ,
//    user_Id VARCHAR (50),
//    business_ID VARCHAR (50),
//    VOTES_CNT INT,
//    Stars INT,
//    review_DATE DATE,
//    review_text CLOB,
//    PRIMARY KEY(review_ID),
//    FOREIGN KEY(user_Id) REFERENCES Users(user_id) ON DELETE CASCADE,
//    FOREIGN KEY(business_ID) REFERENCES Business(BID) ON DELETE CASCADE    
//);

    
    private void updateReviewPanel(String BID){
        String sql = "";
        if(bBusinessMode){
            sql += "Select DISTINCT r.review_ID, r.review_DATE, r.review_text \n"
                    + "FROM Review r  WHERE r.business_ID = " + "'" + BID + "'" + "\n";
        }
        else{
            sql += "Select DISTINCT r.review_ID, r.review_DATE, r.review_text \n"
                    + "FROM Review r  WHERE r.user_Id = " + "'" + BID + "'" + "\n";            
        }
        updateFinalSql(sql);
        String columnNames[] = {"review_id", "review_date", "review_text"};    
        String result = "     review_id      review_date      review_text   \n";        
        ArrayList<String[]> rlist = new ArrayList<>();        
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            // Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                int index = 0;
                String line[] = new String[columnNames.length];
                rlist.add(line);
                line[index++] = rs.getString("review_id");
                line[index++] =  rs.getString("review_date");
                line[index++] =  rs.getString("review_text");             
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(" Error:\n" + sql + "\n");
            ex.printStackTrace();
        }
        
        String res[][] = new String[rlist.size()][columnNames.length];
        int index = 0;
        for(String arr[] : rlist){
            res[index++] = arr;
        }
       // Create a new table instance
       javax.swing.JTable table = new javax.swing.JTable(res, columnNames);    
       // Add the table to a scrolling pane
       

//       Result_Table
       ReviewScrollPane.setViewportView(table);
       pack();        
    }

    private String getBusinessSql(){
        String condition = " AND ";
        Boolean bWhere = false;
        String sql = "Select DISTINCT b.bid,  b.bname, b.city, b.state, b.stars, b.review_cnt  \n"
                + "FROM Business b, MainCategory m, SubCategory s\n";
        int condition_index = 0;
        for (String mainCategory : chooseMain){
            if(!bWhere){
                sql += " WHERE ";
                bWhere = true;
            }
            if (condition_index == 0){
                sql += "b.bid = m.bid AND (";
            }
            else{
                sql += " OR \n";
            }
            condition_index ++;
            sql += "m.MAINCG = " + "'" + mainCategory + "'" + "\n";
        }
        if (condition_index > 0){
            sql += ")";
        }
        condition_index = 0;
        for (String subCategory : chooseSub){
            if (!bWhere && condition_index == 0){
                sql += "WHERE b.bid = s.bid AND (";
                bWhere = true;
            }
            else if(bWhere && condition_index == 0){
                sql += " AND b.bid = s.bid AND (";
            }
            else{
                sql += " OR \n";
            }
            condition_index ++;
            sql += "s.SUBCG = " + "'" + subCategory + "'" + "\n";            
        }
        if (condition_index > 0){
            sql += ")";
        } 
        
        String subQueryCheckIn = getCheckInSql();

        if(subQueryCheckIn.length() > 0){
            sql += " AND b.bid IN ( " + subQueryCheckIn + ")";
        }        
        
        String subQueryReview = getReviewSql();
        
        if(subQueryReview.length() > 0){
            sql += " AND b.bid IN ( " + subQueryReview + ")";
        }
        return sql;
    }
    
    
    
    private void updateBusinessResult(){
       
        String sql = getBusinessSql();       
        updateFinalSql(sql);
        String columnNames[] = {"name", "city", "state", "stars", "review_cnt"};    
        String result = "     name      city      state   stars   review_cnt\n";        
        ArrayList<String[]> rlist = new ArrayList<>();   

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            jTable1.setModel(DbUtils.resultSetToTableModel(rs)); 
            // Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                int index = 0;
                String line[] = new String[columnNames.length];
                rlist.add(line);
                line[index++] = rs.getString("bname");
                line[index++] =  rs.getString("city");
                line[index++] =  rs.getString("state");
                line[index++] =  rs.getString("stars");
                line[index++] =  rs.getString("review_cnt");               
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(" Error:\n" + sql + "\n");
            ex.printStackTrace();
        }
        
        String res[][] = new String[rlist.size()][columnNames.length];
        int index = 0;
        for(String arr[] : rlist){
            res[index++] = arr;
        }
       // Create a new table instance
//       javax.swing.JTable table = new javax.swing.JTable(res, columnNames);    
//       jTable1 = table;
//       ResultSpane.setViewportView(jTable1);
            
//       jTable1.setModel(DbUtils.resultSetToTableModel(rs));       

       pack();             
    }
    private void updateUserResult(){
        String condition = " OR ";
        if(isAnd){
            condition = " AND ";
        } 
        
        String sql = "Select USER_ID, name, review_cnt, avg_stars, friend_cnt, yelp_since   \n"
                + "FROM users u\n";
        int condition_index = 0;

        String yelp_since = yelpfrom.getText();
        if(yelp_since.length()>0 ){
            if (condition_index == 0){
                sql += "WHERE\n";
            }
            else{
                sql += isAnd;
            }
            condition_index++;
            sql += " u.yelp_since" + " > " +  "'" + yelp_since + "'" + "\n"; 
        }

        UserReviewCountCondition = ReviewCountComboBox.getSelectedItem().toString();
        UserReviewCount = ReviewCountValue.getText();
        if(UserReviewCount.length() > 0 && isInteger(UserReviewCount)){
            if (condition_index == 0){
                sql += " WHERE ";
            }
            else{
                sql += condition;
            }
            condition_index++;
            sql += " u.review_cnt" + UserReviewCountCondition +  UserReviewCount + "\n"; 
        }
        NumFriendCondition = FriendsNumComboBox.getSelectedItem().toString();
        NumFriend = FriendsNum.getText();
        if(NumFriend.length() > 0 && isInteger(NumFriend)){
            if (condition_index == 0){
                sql += " WHERE ";
            }
            else{
                sql += condition;
            }
            condition_index++;
            sql += " u.review_cnt" + NumFriendCondition +  NumFriend + "\n"; 
        }
        NumAvgStarsCondition = AvgStarsComboBox.getSelectedItem().toString();
        NumAvgStars = AvgStars.getText();
        if(NumAvgStars.length()>0  && isNumeric(UserReviewCount)){
            if (condition_index == 0){
                sql += " WHERE ";
            }
            else{
                sql += condition;
            }
            condition_index++;
            sql += " u.review_cnt" + NumAvgStarsCondition +  NumAvgStars + "\n"; 
        }

        updateFinalSql(sql);        
        String columnNames[] = {"Name", "review_cnt", "avg_stars", "friend_cnt", "yelp_since"};    
        String result = "     Name      review_cnt      avg_stars   friend_cnt   yelp_since\n";        
        ArrayList<String[]> rlist = new ArrayList<>();
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            jTable1.setModel(DbUtils.resultSetToTableModel(rs)); 
            // Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                int index = 0;
                String line[] = new String[columnNames.length];
                rlist.add(line);
                line[index++] = rs.getString("name");
                line[index++] =  rs.getString("review_cnt");
                line[index++] =  rs.getString("avg_stars");
                line[index++] =  rs.getString("friend_cnt");
                line[index++] =  rs.getString("yelp_since");
                
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(" Error:\n" + sql + "\n");

            ex.printStackTrace();
        }
        
        String res[][] = new String[rlist.size()][columnNames.length];
        int index = 0;
        for(String arr[] : rlist){
            res[index++] = arr;
        }
       // Create a new table instance
//       javax.swing.JTable table = new javax.swing.JTable(res, columnNames);    
       // Add the table to a scrolling pane
//       ResultSpane.setViewportView(table);
       pack();        
    }

    private void ratingChange() {
        updateFinalSql();
    }
    
    private void ratingYelpChange(){
        updateFinalSql();    
    }
    private void numOfReviewChange() {
        updateFinalSql();
    }

    private void tagWeightChange() {
        updateFinalSql();
    }

    private void updateFinalSql(String sql) {
        finalSql = sql;
        Query.setText(finalSql);
    }

    private void updateFinalSql() {
        Query.setText("");
    }

    private String generateNewFinalSql() {
        String sql = null;
        return sql;
    }

    private void restart() {
        chooseMain.clear();
        GenresPane.setViewportView(null);
        chooseSub.clear();
        countrySPane.setViewportView(null);
        updateFinalSql();
        start();
    }

    private void clear4MainChange() {
        countrySPane.setViewportView(null);
        chooseCountries.clear();
        updateFinalSql();
    }


    private void createConnection(String dbURL, String username, String password) {

        try {
            Class.forName(JDBCDriver);
            conn = DriverManager.getConnection(dbURL, username, password);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Hw3().setVisible(true);
            }
        });
    }

    private void createConnection() {

        try {
            Class.forName(JDBCDriver);
            conn = DriverManager.getConnection(dbURL, username, password);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isInteger(String str) {
        for (int i = str.length(); --i >= 0;) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0;) {
            if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != '.') {
                return false;
            }
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        countrySPane = new javax.swing.JScrollPane();
        FilmingSPane = new javax.swing.JScrollPane();
        jPanel22 = new javax.swing.JPanel();
        CheckInComboBox = new javax.swing.JComboBox<>();
        CheckInValue = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        CheckInToHour = new javax.swing.JTextField();
        CheckInFromHour = new javax.swing.JTextField();
        CheckInToDay = new javax.swing.JTextField();
        CheckInFromDay = new javax.swing.JTextField();
        GenresPane = new javax.swing.JScrollPane();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        StarComboBox = new javax.swing.JComboBox<>();
        StarValue = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        VoteComboBox = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        VoteValue = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        AndALL = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Query = new javax.swing.JTextArea();
        executeButton = new javax.swing.JButton();
        executeBizButton = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        ResultSpane = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        ReviewCountComboBox = new javax.swing.JComboBox<>();
        ReviewCountValue = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        FriendsNumComboBox = new javax.swing.JComboBox<>();
        FriendsNum = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        AvgStarsComboBox = new javax.swing.JComboBox<>();
        AvgStars = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        yelpfrom = new javax.swing.JTextField();
        jPanel23 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        ReviewTo = new javax.swing.JTextField();
        ReviewFrom = new javax.swing.JTextField();
        ReviewScrollPane = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Hw3");

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel1.setText("Business");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(163, 163, 163))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setForeground(new java.awt.Color(0, 0, 204));
        jPanel2.setAlignmentX(0.0F);
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setAutoscrolls(true);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("Main-Categories");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jLabel2)
                .addContainerGap(120, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel3.setText("Sub-Categories");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(135, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("Check IN");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(118, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(109, 109, 109))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        jPanel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Reviw");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        countrySPane.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jPanel22.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        CheckInComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=", "<", ">", "<=", ">=" }));
        CheckInComboBox.setToolTipText("");
        CheckInComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckInComboBoxActionPerformed(evt);
            }
        });

        CheckInValue.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                CheckInValueInputMethodTextChanged(evt);
            }
        });
        CheckInValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckInValueActionPerformed(evt);
            }
        });
        CheckInValue.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                CheckInValuePropertyChange(evt);
            }
        });

        jLabel26.setText("Num of Checkins:");

        jLabel27.setText("value");

        jPanel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel19.setText("From     Day                  Hour");

        jLabel18.setText("To        Day                  Hour");

        CheckInToHour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckInToHourActionPerformed(evt);
            }
        });

        CheckInFromHour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckInFromHourActionPerformed(evt);
            }
        });

        CheckInToDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckInToDayActionPerformed(evt);
            }
        });

        CheckInFromDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckInFromDayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(CheckInToDay, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CheckInFromDay, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CheckInFromHour, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CheckInToHour, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(63, 63, 63))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CheckInFromDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckInFromHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CheckInToDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckInToHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CheckInComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CheckInValue, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(38, 38, 38))
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(143, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                        .addComponent(CheckInComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CheckInValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42))
        );

        FilmingSPane.setViewportView(jPanel22);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 38, Short.MAX_VALUE)
        );

        jPanel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        StarComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=", "<", ">", "<=", ">=" }));
        StarComboBox.setToolTipText("");
        StarComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StarComboBoxActionPerformed(evt);
            }
        });

        StarValue.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                StarValueInputMethodTextChanged(evt);
            }
        });
        StarValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StarValueActionPerformed(evt);
            }
        });
        StarValue.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                StarValuePropertyChange(evt);
            }
        });

        jLabel10.setText("Star");

        jLabel13.setText("value");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(90, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StarValue, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StarComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StarComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StarValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        VoteComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=", "<", ">", "<=", ">=" }));
        VoteComboBox.setToolTipText("");
        VoteComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VoteComboBoxActionPerformed(evt);
            }
        });

        jLabel15.setText("value");

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("votes");
        jLabel14.setOpaque(true);

        VoteValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VoteValueActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(VoteValue, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(VoteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(VoteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(VoteValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        AndALL.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        AndALL.setToolTipText("Choose AND or OR");
        AndALL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AndALLActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel12.setText("Search Between Attributes' Values:");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AndALL, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(60, 60, 60))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AndALL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        Query.setEditable(false);
        Query.setColumns(20);
        Query.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Query.setRows(5);
        Query.setAutoscrolls(false);
        jScrollPane4.setViewportView(Query);

        executeButton.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        executeButton.setText("Execute User Query");
        executeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeButtonActionPerformed(evt);
            }
        });

        executeBizButton.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        executeBizButton.setText("Execute Business Query");
        executeBizButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeBizButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addComponent(executeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(executeBizButton, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(executeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(executeBizButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel18.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setText("Result");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(216, 216, 216)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel9)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        ResultSpane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ResultSpaneMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ResultSpaneMousePressed(evt);
            }
        });
        ResultSpane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ResultSpaneKeyPressed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        ResultSpane.setViewportView(jTable1);

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel4.setText("User");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(278, 278, 278)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(249, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        ReviewCountComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=", "<", ">", "<=", ">=" }));
        ReviewCountComboBox.setToolTipText("");
        ReviewCountComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReviewCountComboBoxActionPerformed(evt);
            }
        });

        ReviewCountValue.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                ReviewCountValueInputMethodTextChanged(evt);
            }
        });
        ReviewCountValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReviewCountValueActionPerformed(evt);
            }
        });
        ReviewCountValue.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                ReviewCountValuePropertyChange(evt);
            }
        });

        jLabel11.setText("Review Count");

        jLabel20.setText("value");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
                .addComponent(ReviewCountComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ReviewCountValue, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ReviewCountComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ReviewCountValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel19.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        FriendsNumComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=", "<", ">", "<=", ">=" }));
        FriendsNumComboBox.setToolTipText("");
        FriendsNumComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FriendsNumComboBoxActionPerformed(evt);
            }
        });

        FriendsNum.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                FriendsNumInputMethodTextChanged(evt);
            }
        });
        FriendsNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FriendsNumActionPerformed(evt);
            }
        });
        FriendsNum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                FriendsNumPropertyChange(evt);
            }
        });

        jLabel21.setText("Num. of Friends");

        jLabel22.setText("value");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(FriendsNumComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(FriendsNum, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FriendsNumComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FriendsNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        AvgStarsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=", "<", ">", "<=", ">=" }));
        AvgStarsComboBox.setToolTipText("");
        AvgStarsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AvgStarsComboBoxActionPerformed(evt);
            }
        });

        AvgStars.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                AvgStarsInputMethodTextChanged(evt);
            }
        });
        AvgStars.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AvgStarsActionPerformed(evt);
            }
        });
        AvgStars.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                AvgStarsPropertyChange(evt);
            }
        });

        jLabel23.setText("Avg. Stars");

        jLabel24.setText("value");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(AvgStarsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AvgStars, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(AvgStars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(AvgStarsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel21.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel25.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel25.setText("Member since: (Format YYYY-MM)");
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        yelpfrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yelpfromActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(yelpfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(yelpfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel28.setText("From");

        jLabel29.setText("To");

        ReviewTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReviewToActionPerformed(evt);
            }
        });

        ReviewFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReviewFromActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3))
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ReviewFrom)
                    .addComponent(ReviewTo))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ReviewFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ReviewTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(GenresPane, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(9, 9, 9)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(countrySPane))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(FilmingSPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(530, 530, 530))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ReviewScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(235, 235, 235)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ResultSpane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(GenresPane)
                                    .addComponent(countrySPane)
                                    .addComponent(FilmingSPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                            .addComponent(ResultSpane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ReviewScrollPane))
                        .addGap(9, 9, 9))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(107, 107, 107))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



    private void executeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeButtonActionPerformed
        // TODO add your handling code here:
            bBusinessMode = false;
            updateUserResult();
    }//GEN-LAST:event_executeButtonActionPerformed

    private void AndALLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AndALLActionPerformed
        // TODO add your handling code here:
        String set = AndALL.getSelectedItem().toString();
        if (set.equals("OR")) {
            isAnd = false;
        } else {
            isAnd = true;
        }
        restart();
    }//GEN-LAST:event_AndALLActionPerformed

    private void StarValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StarValueActionPerformed
        // TODO add your handling code here:
        String value = StarValue.getText();
        if (value.equals("")) {
            ratingValue = "";
            ratingChange();
            return;
        }

        if (!isNumeric(value)) {
            StarValue.setText("Invalid input");
            return;
        }

        if (Double.valueOf(value) > 10) {
            StarValue.setText("should < 10");
            return;
        }
        ratingValue = value;
        ratingChange();
//         System.out.println("some thing input"+RatingValue.getText()); 

    }//GEN-LAST:event_StarValueActionPerformed

    private void CheckInFromHourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckInFromHourActionPerformed
        // TODO add your handling code here:
        String value = CheckInFromHour.getText();
        if (value.equals("")) {
            fromYear = "";
            ratingChange();
            return;
        }
        if (!isInteger(fromYear)) {
            CheckInFromHour.setText("Invalid input");
        }
        fromYear = value;
        ratingChange();

    }//GEN-LAST:event_CheckInFromHourActionPerformed

    private void VoteValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VoteValueActionPerformed
        // TODO add your handling code here:
        String value = VoteValue.getText();
        if (value.equals("")) {
            NumOfReviewValue = "";
            ratingChange();
            return;
        }

        if (!isInteger(value)) {
            StarValue.setText("Invalid input");
            return;
        }

        NumOfReviewValue = value;
        ratingChange();
    }//GEN-LAST:event_VoteValueActionPerformed

    private void CheckInToHourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckInToHourActionPerformed
        // TODO add your handling code here:
        String value = CheckInToHour.getText();

        if (value.equals("")) {
            toYear = "";
            ratingChange();
            return;
        }

        if (!isInteger(toYear)) {
            CheckInToHour.setText("Invalid input");
        }
        toYear = value;
        ratingChange();

    }//GEN-LAST:event_CheckInToHourActionPerformed

    private void StarComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StarComboBoxActionPerformed
        // TODO add your handling code here:
        ratingCondition = StarComboBox.getSelectedItem().toString();
        if (!ratingValue.equals("")) {
            ratingChange();
        }

    }//GEN-LAST:event_StarComboBoxActionPerformed

    private void VoteComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VoteComboBoxActionPerformed
        // TODO add your handling code here:
        NumOfReviewCondition = VoteComboBox.getSelectedItem().toString();
        if (!NumOfReviewValue.equals("")) {
            ratingChange();
        }
    }//GEN-LAST:event_VoteComboBoxActionPerformed

    private void StarValueInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_StarValueInputMethodTextChanged
    }//GEN-LAST:event_StarValueInputMethodTextChanged

    private void StarValuePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_StarValuePropertyChange
    }//GEN-LAST:event_StarValuePropertyChange

    private void ReviewCountComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReviewCountComboBoxActionPerformed
        UserReviewCountCondition = ReviewCountComboBox.getSelectedItem().toString();
    }//GEN-LAST:event_ReviewCountComboBoxActionPerformed

    private void ReviewCountValueInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_ReviewCountValueInputMethodTextChanged
        String value = ReviewCountValue.getText();
        if (value.equals("")) {
            UserReviewCount = "";
            ratingYelpChange();
            return;
        }
        if (!isInteger(value)) {
            ReviewCountValue.setText("Invalid input");
            return;
        }
        UserReviewCount = value;
        ratingYelpChange();        // TODO add your handling code here:
    }//GEN-LAST:event_ReviewCountValueInputMethodTextChanged

    private void ReviewCountValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReviewCountValueActionPerformed
        // TODO add your handling code here:
        String value = ReviewCountValue.getText();
        if (value.equals("")) {
            UserReviewCount = "";
            ratingYelpChange();
            return;
        }
        if (!isInteger(value)) {
            ReviewCountValue.setText("Invalid input");
            return;
        }
        UserReviewCount = value;
        ratingYelpChange();
    }//GEN-LAST:event_ReviewCountValueActionPerformed

    private void ReviewCountValuePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_ReviewCountValuePropertyChange
       
    }//GEN-LAST:event_ReviewCountValuePropertyChange

    private void FriendsNumComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FriendsNumComboBoxActionPerformed
        NumFriendCondition = FriendsNumComboBox.getSelectedItem().toString();
    }//GEN-LAST:event_FriendsNumComboBoxActionPerformed

    private void FriendsNumInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_FriendsNumInputMethodTextChanged
        String value = FriendsNum.getText();
        if (value.equals("")) {
            NumFriend = "";
            return;
        }
        if (!isInteger(value)) {
            FriendsNum.setText("Invalid input");
            return;
        }
        NumFriend = value;
    }//GEN-LAST:event_FriendsNumInputMethodTextChanged

    private void FriendsNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FriendsNumActionPerformed
        String value = FriendsNum.getText();
        if (value.equals("")) {
            NumFriend = "";
            return;
        }
        if (!isInteger(value)) {
            FriendsNum.setText("Invalid input");
            return;
        }
        NumFriend = value;
    }//GEN-LAST:event_FriendsNumActionPerformed

    private void FriendsNumPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_FriendsNumPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_FriendsNumPropertyChange

    private void AvgStarsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AvgStarsComboBoxActionPerformed
        NumAvgStarsCondition = AvgStarsComboBox.getSelectedItem().toString();
    }//GEN-LAST:event_AvgStarsComboBoxActionPerformed

    private void AvgStarsInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_AvgStarsInputMethodTextChanged
        String value = AvgStars.getText();
        if (value.equals("")) {
            NumAvgStars = "";
            ratingYelpChange();
            return;
        }
        if (!isInteger(value)) {
            AvgStars.setText("Invalid input");
            return;
        }
        NumAvgStars = value;
        ratingYelpChange();
    }//GEN-LAST:event_AvgStarsInputMethodTextChanged

    private void AvgStarsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AvgStarsActionPerformed
        String value = AvgStars.getText();
        if (value.equals("")) {
            NumAvgStars = "";
            ratingYelpChange();
            return;
        }
        if (!isInteger(value)) {
            AvgStars.setText("Invalid input");
            return;
        }
        NumAvgStars = value;
        ratingYelpChange();
    }//GEN-LAST:event_AvgStarsActionPerformed

    private void AvgStarsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_AvgStarsPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_AvgStarsPropertyChange

    private void CheckInComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckInComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CheckInComboBoxActionPerformed

    private void CheckInValueInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_CheckInValueInputMethodTextChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_CheckInValueInputMethodTextChanged

    private void CheckInValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckInValueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CheckInValueActionPerformed

    private void CheckInValuePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_CheckInValuePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_CheckInValuePropertyChange

    private void yelpfromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yelpfromActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_yelpfromActionPerformed

    private void executeBizButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeBizButtonActionPerformed
        bBusinessMode = true;
        updateBusinessResult();
    }//GEN-LAST:event_executeBizButtonActionPerformed

    private void ReviewToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReviewToActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ReviewToActionPerformed

    private void ReviewFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReviewFromActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ReviewFromActionPerformed

    private void CheckInToDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckInToDayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CheckInToDayActionPerformed

    private void CheckInFromDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckInFromDayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CheckInFromDayActionPerformed

    private void ResultSpaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultSpaneMouseClicked
        // TODO add your handling code here:
        int pos = evt.getY();

        
    }//GEN-LAST:event_ResultSpaneMouseClicked

    private void ResultSpaneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultSpaneMousePressed
        // TODO add your handling code here:
        int pos = evt.getY();

    }//GEN-LAST:event_ResultSpaneMousePressed

    private void ResultSpaneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ResultSpaneKeyPressed
        // TODO add your handling code here:
        int pos = evt.getID();

    }//GEN-LAST:event_ResultSpaneKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();
        String BID = jTable1.getModel().getValueAt(row, 0).toString();
        updateReviewPanel(BID);
    }//GEN-LAST:event_jTable1MouseClicked



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> AndALL;
    private javax.swing.JTextField AvgStars;
    private javax.swing.JComboBox<String> AvgStarsComboBox;
    private javax.swing.JComboBox<String> CheckInComboBox;
    private javax.swing.JTextField CheckInFromDay;
    private javax.swing.JTextField CheckInFromHour;
    private javax.swing.JTextField CheckInToDay;
    private javax.swing.JTextField CheckInToHour;
    private javax.swing.JTextField CheckInValue;
    private javax.swing.JScrollPane FilmingSPane;
    private javax.swing.JTextField FriendsNum;
    private javax.swing.JComboBox<String> FriendsNumComboBox;
    private javax.swing.JScrollPane GenresPane;
    private javax.swing.JTextArea Query;
    private javax.swing.JScrollPane ResultSpane;
    private javax.swing.JComboBox<String> ReviewCountComboBox;
    private javax.swing.JTextField ReviewCountValue;
    private javax.swing.JTextField ReviewFrom;
    private javax.swing.JScrollPane ReviewScrollPane;
    private javax.swing.JTextField ReviewTo;
    private javax.swing.JComboBox<String> StarComboBox;
    private javax.swing.JTextField StarValue;
    private javax.swing.JComboBox<String> VoteComboBox;
    private javax.swing.JTextField VoteValue;
    private javax.swing.JScrollPane countrySPane;
    private javax.swing.JButton executeBizButton;
    private javax.swing.JButton executeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField yelpfrom;
    // End of variables declaration//GEN-END:variables
}
