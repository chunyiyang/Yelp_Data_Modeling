-- create Business Table
CREATE TABLE Business (
    BID VARCHAR (50) ,
    City VARCHAR (30),
    State VARCHAR (5),
    BName VARCHAR (100),
    Stars NUMBER,
    Address VARCHAR (150),
    Review_cnt INT,
    PRIMARY KEY(BID)
);

-- create Business MainCategory Table
CREATE TABLE MainCategory (
    BID VARCHAR (50),
    MainCG VARCHAR (50),
    FOREIGN KEY(BID) REFERENCES Business(BID) ON DELETE CASCADE
);

-- create Business SubCategory Table
CREATE TABLE SubCategory (
    BID VARCHAR (50),
    SubCG VARCHAR (50),
    FOREIGN KEY(BID) REFERENCES Business(BID) ON DELETE CASCADE
);

CREATE TABLE Categories (
    MainCG VARCHAR (50),
    SubCG VARCHAR (50),
    PRIMARY KEY (MainCG, SubCG)
);

-- create Users Table
CREATE TABLE Users (
    USER_ID VARCHAR (50),
    NAME VARCHAR (50),
    REVIEW_CNT INT,
    AVG_stars NUMBER,
    Friend_CNT INT,
    YELP_SINCE VARCHAR(20),
    PRIMARY KEY(USER_ID)
);

-- create Review Table
CREATE TABLE Review (
    review_ID VARCHAR (50) ,
    user_Id VARCHAR (50),
    business_ID VARCHAR (50),
    VOTES_CNT INT,
    Stars INT,
    review_DATE DATE,
    review_text CLOB,
    PRIMARY KEY(review_ID),
    FOREIGN KEY(user_Id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY(business_ID) REFERENCES Business(BID) ON DELETE CASCADE    
);

-- create Checkin Table
CREATE TABLE Checkin (
    BID VARCHAR (50),
    HOUR INT,
    DAY  INT,
    NUM INT,
    FOREIGN KEY(BID) REFERENCES Business(BID) ON DELETE CASCADE
);

