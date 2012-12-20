package edu.ucsb.testuggine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdOut;

public class TADBInterface {
	JSONParser parser;
	MySQLConnection db;

	/** Requires the path to the two files 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws SQLException */
	public TADBInterface(String restaurantsFilePath, String reviewsFilePath) throws FileNotFoundException, IOException, ParseException, SQLException {
		parser = new JSONParser();
		In restaurantsIn = new In(restaurantsFilePath);
		while (!restaurantsIn.isEmpty()) {
			String line = restaurantsIn.readLine();
			Object reviewObj = parser.parse(line);
			JSONObject jObj = (JSONObject) reviewObj;
			TripAdvisorRestaurant r = parseRestaurant(jObj);
			writeToDB(r);
		}
		
		
		In reviewsIn = new In(reviewsFilePath);
		while (!reviewsIn.isEmpty()) {
			String line = reviewsIn.readLine();
			Object reviewObj = parser.parse(line);
			JSONObject jObj = (JSONObject) reviewObj;
			TripAdvisorReview rvw = parseReview(jObj);
			writeToDB(rvw);
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

		String region_id = (String) jsonObject.get("region_id");
		String url = (String) jsonObject.get("url");
		String phone = (String) jsonObject.get("phone");

		String details = "";
		JSONArray detailsArray = (JSONArray) jsonObject.get("details");
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = detailsArray.iterator();
		while (iterator.hasNext()) {
			details += "; " + iterator.next();
		}

		JSONObject address = (JSONObject) jsonObject.get("address");
		String addressRegion = (String) address.get("region");
		String streetAddress = (String) address.get("street-address");
		String postalCode = (String) address.get("postal-code");
		String locality = (String) address.get("locality");
		
		Address restaurantAddress = new Address(streetAddress, postalCode, locality, addressRegion);
		
		
		String type = (String) jsonObject.get("type");
		String id = (String) jsonObject.get("id");
		String name = (String) jsonObject.get("name");		


		return new TripAdvisorRestaurant(name, restaurantAddress, phone, url, details, id, region_id, type);
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
	 * {"food": 5.0, "atmosphere": 5.0, "overall": 5.0, "value": 5.0, "service": 5.0}
	 * */
	private TripAdvisorReview parseReview(JSONObject jsonObject) {
		
		// TODO: Ask Myle about the helpful counter. Was it not mined? It's not the sample data I got.
		
		JSONObject ratings = (JSONObject) jsonObject.get("ratings");
		Float globalRating = (Float) ratings.get("overall");
		Float valueRating = (Float) ratings.get("value");
		Float atmosphereRating = (Float) ratings.get("atmosphere");
		Float serviceRating = (Float) ratings.get("service");
		Float foodRating = (Float) ratings.get("food");
		
		String title = (String) jsonObject.get("title");
		String text = (String) jsonObject.get("text");
		JSONObject authorJSON = (JSONObject) jsonObject.get("author");
		TripAdvisorUser author = parseAuthor(authorJSON);
		String id = (String) jsonObject.get("id");
		String dateStr = (String) jsonObject.get("date");
		String date = mySQLformat(dateStr);
		String restaurant_id = (String) jsonObject.get("offering_id");
		
		
		return new TripAdvisorReview(id, author, globalRating, valueRating, atmosphereRating,
				serviceRating, foodRating, null, date, title, text, restaurant_id);
	}
	
	/**
	 * Input format: December 11, 2012\nNEW
	 * Output format: return d.year() + "-" + d.month() + "-" + d.day();
	 *  */
	private String mySQLformat(String dateStr) {
		Integer month_start_pos = 0;
		Integer month_end_pos = dateStr.indexOf(" ");
		
		Integer day_start_pos = month_end_pos + 1;
		Integer day_end_pos = dateStr.indexOf(", ", day_start_pos);
		
		Integer year_start_pos = day_end_pos + 2; // Comma + space
		Integer year_end_pos = dateStr.length();
		if (dateStr.contains("\nNEW")) year_end_pos = dateStr.indexOf("\nNEW");
		
		String month = dateStr.substring(month_start_pos, month_end_pos);
		String day = dateStr.substring(day_start_pos, day_end_pos);
		String year = dateStr.substring(year_start_pos, year_end_pos);
		
		return year + "-" + month + "-" + day;
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
		String location = (String) jsonObject.get("location");
		
		return new TripAdvisorUser(userName, reviewsInCitiesCount, helpfulCount, totalReviewsCount,
				restaurantReviewsCount, id, location);
		
	}

	void writeToDB(TripAdvisorRestaurant r) throws SQLException {
		String insertionQuery = "INSERT INTO  `TripAdvisorRestaurant` ("
				+ "`name` ,`addressNum` ,`addressStreet` ,`addressCity` ,"
				+ "`addressRegion` ,`addressZip` ,`phoneNumber` ,`url`," +
				"`details`, `id`, `region_id`, `type`)"
				+ "VALUES (? ,  ?,  ?,  "
				+ "?,  ?,  ?,  ?,  ?,  "
				+ "?,  ?,  ?, ?);";

		PreparedStatement prep = db.con.prepareStatement(insertionQuery);

		prep.setString(1, r.name); // Name CANNOT be null!
		safeInsert(prep, 2, r.address.number);
		safeInsert(prep, 3, r.address.street);
		safeInsert(prep, 4, r.address.city);
		safeInsert(prep, 5, r.address.region);
		safeInsert(prep, 6, r.address.zip);
		safeInsert(prep, 7, r.phoneNumber);
		safeInsert(prep, 8, r.url);
		safeInsert(prep, 9, r.details);
		safeInsert(prep, 10, r.id);
		safeInsert(prep, 11, r.region_id);
		safeInsert(prep, 12, r.type);

		if (!isAlreadyInDB(r)) {
			StdOut.println("----\n" + prep + "\n--------");
			prep.execute();
		} else
			StdOut.println("Restaurant " + r.name
					+ " already in the DB. Skipping...");
		prep.close();
	}

	void writeToDB(TripAdvisorReview rev) throws SQLException {

		String alreadyExistsCheckQuery = "SELECT * FROM  `TripAdvisorReview` WHERE  `id` =  ?";
		PreparedStatement checkStatement = db.con
				.prepareStatement(alreadyExistsCheckQuery);
		checkStatement.setString(1, rev.id);
		ResultSet alreadyExistsRes = checkStatement.executeQuery(); // if it's already there, don't insert
		String insertionQuery = "INSERT INTO `TripAdvisorReview` " +
				"(`id`, `author_id`, `restaurant_id`, `globalRating`, `valueRating`, `atmosphereRating`, " +
				"`serviceRating`, `foodRating`, `helpfulCounter`, `date`, `title`, `text`) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

		PreparedStatement prep = db.con.prepareStatement(insertionQuery);

		prep.setString(1, rev.id);

		writeToDb(rev.author);
		prep.setString(2, rev.author.id);
		prep.setString(3, rev.restaurant_id);
		
		prep.setFloat(4, rev.globalRating);
		prep.setFloat(5,  rev.valueRating);
		prep.setFloat(6,  rev.atmosphereRating);
		prep.setFloat(7,  rev.serviceRating);
		prep.setFloat(8,  rev.foodRating);
		prep.setInt(9, 0);	// prep.setInt(9,  rev.helpfulCounter); ?
		safeInsert(prep, 10, mySQLformat(rev.date));
		safeInsert(prep, 11, rev.title);
		safeInsert(prep, 12, rev.text);

		if (!alreadyExistsRes.first()) {
			prep.execute();
		}
		else 
			StdOut.println("Insert failed. Review already in db: \n\t" + rev);
		prep.close();
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
	void writeToDb(TripAdvisorUser u) throws SQLException {

		String alreadyExistsCheckQuery = "SELECT * FROM  `TripAdvisorUser` WHERE  `id` =  ?";
		PreparedStatement checkStatement = db.con
				.prepareStatement(alreadyExistsCheckQuery);
		checkStatement.setString(1, u.id);
		ResultSet alreadyExistsRes = checkStatement.executeQuery(); // if it's already there, don't insert
		String insertionQuery = "INSERT INTO `TripAdvisorUser` (`username`, `num_cities`, " +
				"`num_helpful_votes`, `num_reviews`, `num_type_reviews`, `id`, `location`) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement prep = db.con.prepareStatement(insertionQuery);
		prep.setString(1, u.userName); 
		prep.setInt(2, u.num_cities); // Name CANNOT be null!
		prep.setInt(3, u.num_helpful_votes);
		prep.setInt(4, u.num_reviews);
		prep.setInt(5, u.num_type_reviews);
		prep.setString(6, u.id); // Can't be null
		safeInsert(prep, 7, u.location);

		if (!alreadyExistsRes.first()) {
			prep.execute();
		}
		else 
			StdOut.println("User already in DB: \n\t" + u );
		prep.close();
	}

	private boolean isAlreadyInDB(TripAdvisorRestaurant r) throws SQLException {
		String alreadyExistsCheckQuery = "SELECT * FROM  `TripAdvisorRestaurant` WHERE  `id` =  ?";
		PreparedStatement checkStatement = db.con
				.prepareStatement(alreadyExistsCheckQuery);
		checkStatement.setString(1, r.id); // the ID of this restaurant
		ResultSet alreadyExistsRes = checkStatement.executeQuery();
		if (!alreadyExistsRes.first() ) return false;
		return true;
	}
	
	private static void safeInsert(PreparedStatement prep, int pos, String field)
			throws SQLException { // JDBC sends an empty string instead of a
		// NULL value.
		if (field.isEmpty())
			prep.setString(pos, null);
		else
			prep.setString(pos, field);
	}

	public static void main(String[] args) {


	}

}
