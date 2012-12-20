package edu.ucsb.testuggine;

/** Format: 
 * {"username": "Estherleibel", 
 * "num_cities": 2, 
 * "num_helpful_votes": 1, 
 * "num_reviews": 20, 
 * "num_type_reviews": 16, 
 * "id": "C3ECF97B6A4424B63DB907412344B520", 
 * "location": "New York City, New York"}
 * */
public class TripAdvisorUser {
	String userName;

	Integer num_cities; // How many cities has the user written reviews in?
	Integer num_helpful_votes; // How many people found this user's reviews helpful
	Integer num_reviews;
	Integer num_type_reviews; // How many of the reviews were actually about restaurants
	
	String id;
	String location;
	/**
	 * @param userName
	 * @param num_cities
	 * @param num_helpful_votes
	 * @param num_reviews
	 * @param num_type_reviews
	 * @param id
	 * @param location
	 */
	public TripAdvisorUser(String userName, Integer num_cities,
			Integer num_helpful_votes, Integer num_reviews,
			Integer num_type_reviews, String id, String location) {
		this.userName = userName;
		this.num_cities = num_cities;
		this.num_helpful_votes = num_helpful_votes;
		this.num_reviews = num_reviews;
		this.num_type_reviews = num_type_reviews;
		this.id = id;
		this.location = location;
	}
	@Override
	public String toString() {
		return "TripAdvisorUser [userName=" + userName + ", num_cities="
				+ num_cities + ", num_helpful_votes=" + num_helpful_votes
				+ ", num_reviews=" + num_reviews + ", num_type_reviews="
				+ num_type_reviews + ", id=" + id + ", location=" + location
				+ "]";
	}
	
}
