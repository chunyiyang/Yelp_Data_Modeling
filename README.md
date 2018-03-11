# Yelp Data Modeling

# Description:
A data analysis application for Yelp.com’s business review data. The emphasis is on the database infrastructure of the application.
This project uses Oracle Database 11gR2 and Java.

# Use case:
The primary users for this application are potential customers seeking for businesses and users that match their search criteria. My application has a user interface that provides the user the available business categories (main and sub-categories) and the checkin attribute along with business review and yelp user information associated with each business category. Using this application the user will search for the businesses from various business categories that have the properties (attributes) the user is looking for. The user can filter the search results by checkin (from/to and Day/Hours), reviews (To/From, No. of stars/votes) and users information. My application also allows the user to view the reviews provided for each business.

My application also provides user search function by choosing 'yelp since', review count, number of frineds and average stars attribute. 



# GUI:
![alt text](./images/GUI.bmp)

# Input:
	Business Objects
Business objects contain basic information about local businesses.
{
	'business_id': (encrypted business id), 'full_address': (localized address),
	‘hours’: (the days of the week when business is open; the opening and closing times on those days) 'open': True / False (corresponds to closed, not business hours),
	‘categories’: (categories associated with the business) 'city': (city),
	'state': (state), 'latitude': latitude, 'longitude': longitude,
	'review_count': review count, 'name': (business name), 'neighborhoods': [(hood names)],
	'stars': (star rating, rounded to half-stars), ‘attributes’: (business properties),
	'type': 'business'
}

	Review Objects
Review objects contain the review text, the star rating, and information on votes Yelp users have cast on the review. Use user_id to associate this review with others by the same user. Use business_id to associate this review with others of the same business.
{
	'votes': {
	'useful': (count of useful votes), 'funny': (count of funny votes), 'cool': (count of cool votes)
	}
	'user_id': (the identifier of the authoring user), 'review_id': (the identifier of the reviewed business), 'stars': (star rating, integer 1-5),
	'date': (date, formatted like '2011-04-19'), 'text': (review text),
	'type': 'review',
	'business_id': (the identifier of the reviewed business)
}

	User Objects
User objects contain aggregate information about a single user across all of Yelp (including businesses and reviews not in this dataset).

{
	‘yelping_since’: (the date when user account was created) 'votes': {
	'useful': (count of useful votes across all reviews), 'funny': (count of funny votes across all reviews), 'cool': (count of cool votes across all reviews)
	5
	}
	'review_count': (review count),
	'name': (first name, last initial, like 'Matt J.'), 'user_id': (unique user identifier),
	‘friends’: (friends of the user), ‘fans’: (number fans of the user),
	'average_stars': (floating point average, like 4.31), 'type': 'user',
	‘compliments’: (comments from other users), ‘elite’: ()
}

	Check-in Objects
{
	'type': 'checkin',
	'business_id': (encrypted business id), 'checkin_info': {
	'0-0': (number of checkins from 00:00 to 01:00 on all Sundays), '1-0': (number of checkins from 01:00 to 02:00 on all Sundays),
	...
	'14-4': (number of checkins from 14:00 to 15:00 on all Thursdays),
	...
	'23-6': (number of checkins from 23:00 to 00:00 on all Saturdays)
	} # if there was no checkin for a hour-day block it will not be in the list
}


