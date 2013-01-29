package edu.ucsb.testuggine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.princeton.cs.algs4.Date;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdOut;

public class TADBInterface {
	JSONParser parser;
	MySQLConnection db;
	boolean verbose;

	/** Requires the path to the two files 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws SQLException */
	public TADBInterface(String restaurantsFilePath, String reviewsFilePath, boolean verbose) throws FileNotFoundException, IOException, ParseException, SQLException {
		parser = new JSONParser();
		db = new MySQLConnection();
		this.verbose = verbose;
		if (!restaurantsFilePath.isEmpty()) {
			In restaurantsIn = new In(restaurantsFilePath);
			while (!restaurantsIn.isEmpty()) {
				String line = restaurantsIn.readLine();
				Object reviewObj = parser.parse(line);
				JSONObject jObj = (JSONObject) reviewObj;
				TripAdvisorRestaurant r = parseRestaurant(jObj);
				writeToDB(r);
				for (String phoneNumber : r.phoneNumbers) {
					insertPhoneNumbers(r, phoneNumber);
				}
			}
		}
		if (!reviewsFilePath.isEmpty()) {
			In reviewsIn = new In(reviewsFilePath);
			while (!reviewsIn.isEmpty()) {
				String line = reviewsIn.readLine();
				Object reviewObj = parser.parse(line);
				JSONObject jObj = (JSONObject) reviewObj;
				TripAdvisorReview rvw = parseReview(jObj);
				writeToDB(rvw);
			}
		}
	}

	/** String format example:
	 * {"region_id": 32655,
	 * "url": "http://www.tripadvisor.com/..."
	 * "phone": "310-397-9999"
	 * "details": ["Price range:\n $20 - $45", "Cuisines:\n Mexican"],
	 * "address": {
	 * 		"region": "CA", 
	 * 		"street-address": "4500 S. Centinela Ave", 
	 * 		"postal-code": "90066-6206",
	 * 		"locality": "Los Angeles"}, 
	 * "type": "restaurant", 
	 * "id": 515684, 
	 * "name": "Casa Sanchez"
	 */
	private TripAdvisorRestaurant parseRestaurant(JSONObject jsonObject) {

		String region_id = String.valueOf((Long) jsonObject.get("region_id")).trim();
		String url = ((String) jsonObject.get("url")).trim();
		String phone = ((String) jsonObject.get("phone")).trim();
		ArrayList<String> phoneNums = processPhoneNumber(phone);


		String details = "";
		JSONArray detailsArray = (JSONArray) jsonObject.get("details");
		if (detailsArray == null) detailsArray = new JSONArray();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = detailsArray.iterator();
		while (iterator.hasNext()) {
			details += "; " + iterator.next().trim();
		}

		JSONObject address = (JSONObject) jsonObject.get("address");
		String addressRegion = retrieveField(address, "region");
		String streetAddress = retrieveField(address, "street-address");
		String postalCode = retrieveField(address, "postal-code");
		String locality = retrieveField(address, "locality");

		Address restaurantAddress = new Address(streetAddress, postalCode, locality, addressRegion);


		String type = retrieveField(address, "type");
		String id = String.valueOf((Long) jsonObject.get("id")).trim();
		String name = retrieveField(jsonObject, "name");		

		return new TripAdvisorRestaurant(name, restaurantAddress, phoneNums, url, details, id, region_id, type);
	}

	private String retrieveField(JSONObject element, String fieldname) { // returns an empty string if the field is not there
		if (!element.containsKey(fieldname)) return "";
		String result = ((String) element.get(fieldname)).trim();
		return result;
	}
	/** This function processes all the different types of phone numbers I have seen in the dataset.
	 * - Multiple numbers: "phone": "212-242-4300 / 212-243-8400"
	 * - Different separators: "phone": "212/732-7678" or even "phone": "206/328.2030"
	 * */
	private ArrayList<String> processPhoneNumber(String phone) {
		ArrayList<String> result = new ArrayList<String>();

		Pattern phoneNumberRegex = Pattern.compile("(\\+1 ?)?(\\d{3})\\D(\\d{3})\\D(\\d{4})");
		// optional countrycode, 3 digits, separator, 3 more digits, separator, last 4 digits. 
		// Each of the digits groups is put inside a capture group

		Matcher phoneMatcher = phoneNumberRegex.matcher(phone);

		while (phoneMatcher.find()) { // Until there are phone numbers
			String firstBlock = phoneMatcher.group(2);
			String secondBlock = phoneMatcher.group(3);
			String thirdBlock = phoneMatcher.group(4);
			String numStr = firstBlock + secondBlock + thirdBlock;
			result.add(numStr);
		}
		return result;
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
		Float globalRating = (float) ((Double) ratings.get("overall")).doubleValue();
		Float valueRating = (float) 0.0, atmosphereRating = (float) 0.0, serviceRating = (float) 0.0, foodRating = (float) 0.0;
		if (ratings.containsKey("value"))
			valueRating = (float) ((Double) ratings.get("value")).doubleValue();
		if (ratings.containsKey("atmosphere")) 
			atmosphereRating = (float) ((Double) ratings.get("atmosphere")).doubleValue();
		if (ratings.containsKey("service")) 
			serviceRating = (float) ((Double) ratings.get("service")).doubleValue();
		if (ratings.containsKey("food")) 
			foodRating = (float) ((Double) ratings.get("food")).doubleValue();
		Integer helpfulCounter = 0;
		if (ratings.containsKey("helpful"))
			helpfulCounter = (Integer) ratings.get("helpful"); // TODO Maybe the name will differ?

		String title = retrieveField(jsonObject, "title");
		String text = retrieveField(jsonObject, "text");
		JSONObject authorJSON = (JSONObject) jsonObject.get("author");
		TripAdvisorUser author = parseAuthor(authorJSON);
		String id = String.valueOf((Long) jsonObject.get("id"));
		String dateStr = retrieveField(jsonObject, "date");

		String restaurant_id = String.valueOf((Long) jsonObject.get("offering_id"));


		return new TripAdvisorReview(id, author, globalRating, valueRating, atmosphereRating,
				serviceRating, foodRating, helpfulCounter, dateStr, title, text, restaurant_id);
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

		Integer reviewsInCitiesCount = 0, helpfulCount = 0, totalReviewsCount = 0, restaurantReviewsCount = 0;

		if (jsonObject.containsKey("num_cities"))
			reviewsInCitiesCount = ((Long) jsonObject.get("num_cities")).intValue();
		if (jsonObject.containsKey("num_helpful_votes"))
			helpfulCount = ((Long) jsonObject.get("num_helpful_votes")).intValue();
		if (jsonObject.containsKey("num_reviews"))
			totalReviewsCount = ((Long) jsonObject.get("num_reviews")).intValue();
		if (jsonObject.containsKey("num_type_reviews"))
			restaurantReviewsCount = ((Long) jsonObject.get("num_type_reviews")).intValue();
		String location = (String) jsonObject.get("location");

		return new TripAdvisorUser(userName, reviewsInCitiesCount, helpfulCount, totalReviewsCount,
				restaurantReviewsCount, id, location);

	}

	void writeToDB(TripAdvisorRestaurant r) throws SQLException {
		String insertionQuery = "INSERT INTO `TripAdvisorRestaurant` " +
				"(`name`, `addressNum`, `addressStreet`, `addressCity`, " +
				"`addressRegion`, `addressZip`, `url`, " +
				"`details`, `id`, `region_id`, `type`) " +
				"VALUES " +
				"(?, ?, ?, ?, " +
				"?, ?, ?, ?, " +
				"?, ?, ?);";

		PreparedStatement prep = db.con.prepareStatement(insertionQuery);

		prep.setString(1, r.name); // Name CANNOT be null!
		safeInsert(prep, 2, r.address.number);
		safeInsert(prep, 3, r.address.street);
		safeInsert(prep, 4, r.address.city);
		safeInsert(prep, 5, r.address.region);
		safeInsert(prep, 6, r.address.zip);
		safeInsert(prep, 7, r.url);
		safeInsert(prep, 8, r.details);
		safeInsert(prep, 9, r.id);
		safeInsert(prep, 10, r.region_id);
		safeInsert(prep, 11, r.type);

		if (!isAlreadyInDB(r)) {
			if (verbose)
				StdOut.println("----\n" + prep + "\n--------");
			prep.execute();
		} else {
			if (verbose)
				StdOut.println("Restaurant " + r.name
						+ " already in the DB. Skipping...");
		}
		prep.close();
	}

	private void insertPhoneNumbers(TripAdvisorRestaurant r, String phoneNum) throws SQLException {
		String insertionQuery = "INSERT INTO  `TAreviews`.`TARestaurantNumber` " +
				"(`restaurant_id` ,`phoneNumber`)" +
				"VALUES (?,  ?)";
		PreparedStatement prep = db.con.prepareStatement(insertionQuery);
		prep.setString(1, r.id);
		prep.setString(2, phoneNum);
		if (!isAlreadyInDB(phoneNum)) {
			if (verbose)
				StdOut.println("----\n" + prep + "\n--------");
			prep.execute();
		} else {
			if (verbose)
				StdOut.println("Phone number " + phoneNum
					+ " already in the DB. Skipping...");
		}
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
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

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
		prep.setInt(9, rev.helpfulCounter);
		safeInsert(prep, 10, mySQLformat(rev.date));
		safeInsert(prep, 11, rev.title);
		safeInsert(prep, 12, rev.text);

		if (!alreadyExistsRes.first()) {
			if (verbose)
				StdOut.println("----\n" + prep + "\n--------");
			prep.execute();
		}
		else {
			if (verbose)
				StdOut.println("Insert failed. Review already in db: \n\t" + rev);
		}
		prep.close();
	}


	private String mySQLformat(Date d) {
		return d.year() + "-" + d.month() + "-" + d.day();
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
			if (verbose)
				StdOut.println("----\n" + prep + "\n--------");
			prep.execute();
		}
		else {
			if (verbose)
				StdOut.println("User already in DB: \n\t" + u );
		}
			
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

	private Boolean isAlreadyInDB(String phoneNumber) throws SQLException {
		String alreadyExistsCheckQuery = "SELECT * FROM  `TARestaurantNumber` WHERE  `phoneNumber` =  ?";
		PreparedStatement checkStatement = db.con
				.prepareStatement(alreadyExistsCheckQuery);
		checkStatement.setString(1, phoneNumber); // the ID of this restaurant
		ResultSet alreadyExistsRes = checkStatement.executeQuery();
		if (!alreadyExistsRes.first() ) return false;
		return true;
	}

	private static void safeInsert(PreparedStatement prep, int pos, String field)
			throws SQLException { // JDBC sends an empty string instead of a
		// NULL value.
		if (field == null || field.isEmpty())
			prep.setString(pos, null);
		else
			prep.setString(pos, field);
	}

}
