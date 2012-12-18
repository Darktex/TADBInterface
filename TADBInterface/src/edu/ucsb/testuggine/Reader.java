package edu.ucsb.testuggine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.princeton.cs.introcs.In;

public class Reader {
	JSONParser parser;
	MySQLConnection db;

	/** Requires the path to the two files 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException */
	public Reader(String restaurantsFilePath, String reviewsFilePath) throws FileNotFoundException, IOException, ParseException {
		parser = new JSONParser();
		In restaurantsIn = new In(restaurantsFilePath);
		while (!restaurantsIn.isEmpty()) {
			String line = restaurantsIn.readLine();
			Object reviewObj = parser.parse(line);
			JSONObject jObj = (JSONObject) reviewObj;
			TripAdvisorRestaurant r = parseRestaurant(jObj);
		}
		
		
		In reviewsIn = new In(reviewsFilePath);
		while (!reviewsIn.isEmpty()) {
			String line = reviewsIn.readLine();
			Object reviewObj = parser.parse(line);
			JSONObject jObj = (JSONObject) reviewObj;
			TripAdvisorReview rvw = parseReview(jObj);
		}
		
		

	}

	/** String format example:
	 * 
	 * {"region_id": 60763, 
	 * "url": "http://www.tripadvisor.com/...", 
	 * "phone": "2128896600", 
	 * "details": ["Price range: $5 - $20", "Cuisines: American, Hamburgers, Ice Cream, Hot Dogs", 
	 * 				"Good for: Families with children, Outdoor seating, Dining on a budget", 
	 * 				"Dining options: Breakfast/Brunch, Lunch, Dinner, Takeout, Late Night, Dessert"], 
	 * "address": {"region": "NY", 
	 * 				"street-address": "Southeast corner of Madison Square Park", 
	 * 				"postal-code": "10010", 
	 * 				"locality": "New York City"}, 
	 * "type": "restaurant", 
	 * "id": 911447, 
	 * "name": "Shake Shack"} */
	private TripAdvisorRestaurant parseRestaurant(JSONObject jsonObject) {

		String url = (String) jsonObject.get("url");
		String phone = (String) jsonObject.get("phone");

		ArrayList<String> details = new ArrayList<String>();
		JSONArray detailsArray = (JSONArray) jsonObject.get("details");
		Iterator<String> iterator = detailsArray.iterator();
		while (iterator.hasNext()) {
			details.add(iterator.next());
		}

		JSONObject address = (JSONObject) jsonObject.get("messages");
		String addressRegion = (String) address.get("region");
		String streetAddress = (String) address.get("street-address");
		String postalCode = (String) address.get("postal-code");
		String locality = (String) address.get("locality");

		String type = (String) jsonObject.get("type");
		String id = (String) jsonObject.get("id");
		String name = (String) jsonObject.get("name");		


		return new TripAdvisorRestaurant();
	}
	
	
	/** Format: 
	 * {"ratings": {"overall": 4.0}, 
	 * "title": "\u201cUnderstated, yet impressive\u201d", 
	 * "text": "It just ...", 
	 * "author": {"username": "BeBoBangus", "num_cities": 2, 
	 * 				"num_reviews": 5, "location": "BeBoBangus", 
	 * 				"id": "C89E367F21AC6B61AB085D1B2409540C", 
	 * 				"num_type_reviews": 3}, 
	 * "offering_id": 911447, 
	 * "date": "December 12, 2012\nNEW", 
	 * "id": 147314103}
	 * */
	private TripAdvisorReview parseReview(JSONObject jsonObject) {
		JSONObject ratings = (JSONObject) jsonObject.get("ratings");
		String globalRating = (String) ratings.get("overall");
		String title = (String) jsonObject.get("title");
		String text = (String) jsonObject.get("text");
		JSONObject authorJSON = (JSONObject) jsonObject.get("author");
		TripAdvisorUser author = parseAuthor(authorJSON);
		String id = (String) jsonObject.get("offering_id");
		String dateStr = (String) jsonObject.get("date");
		Date date = parsedate(dateStr);
		offering_id = ???
		
		
		return new TripAdvisorReview(id, author, globalRating, valueRating, atmosphereRating, serviceRating,
				foodRating, helpfulCounter, date, text);
	}
	
	
	/** Format: 
	 * {"username": "Estherleibel", 
	 * "num_cities": 2, 
	 * "num_helpful_votes": 1, 
	 * "num_reviews": 20, 
	 * "num_type_reviews": 16, 
	 * "id": "C3ECF97B6A4424B63DB907412344B520", 
	 * "location": "New York City, New York"}
	 * */
	private TripAdvisorUser parseAuthor(JSONObject jsonObject) {
		String id = (String) jsonObject.get("id");
		String userName = (String) jsonObject.get("username");
		Integer reviewsInCitiesCount = (Integer) jsonObject.get("num_cities");
		Integer helpfulCount = (Integer) jsonObject.get("num_helpful_votes");
		Integer totalReviewsCount = (Integer) jsonObject.get("num_reviews");
		Integer restaurantReviewsCount = (Integer) jsonObject.get("num_type_reviews");
		
		return new TripAdvisorUser(id, userName, 
				totalReviewsCount, restaurantReviewsCount, reviewsInCitiesCount, helpfulCount);
	}


	public static void main(String[] args) {


	}

}
