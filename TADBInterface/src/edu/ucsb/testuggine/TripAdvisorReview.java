package edu.ucsb.testuggine;

public class TripAdvisorReview {
	String id;
	TripAdvisorUser author;

	Float globalRating; // Ratings. This is the final one.
	Float valueRating;
	Float atmosphereRating;
	Float serviceRating;
	Float foodRating;

	Integer helpfulCounter;

	String date;
	String title;
	String text;
	
	String restaurant_id;	
	
	/**
	 * @param id
	 * @param author
	 * @param globalRating
	 * @param valueRating
	 * @param atmosphereRating
	 * @param serviceRating
	 * @param foodRating
	 * @param helpfulCounter
	 * @param date
	 * @param title
	 * @param text
	 * @param offering_id
	 */
	public TripAdvisorReview(String id, TripAdvisorUser author,
			Float globalRating, Float valueRating, Float atmosphereRating,
			Float serviceRating, Float foodRating, Integer helpfulCounter,
			String date, String title, String text, String restaurant_id) {
		this.id = id;
		this.author = author;
		this.globalRating = globalRating;
		this.valueRating = valueRating;
		this.atmosphereRating = atmosphereRating;
		this.serviceRating = serviceRating;
		this.foodRating = foodRating;
		this.helpfulCounter = helpfulCounter;
		this.date = date;
		this.title = title;
		this.text = text;
		this.restaurant_id = restaurant_id;
	}

	@Override
	public String toString() {
		return "TripAdvisorReview [id=" + id + ", author=" + author
				+ ", globalRating=" + globalRating + ", valueRating="
				+ valueRating + ", atmosphereRating=" + atmosphereRating
				+ ", serviceRating=" + serviceRating + ", foodRating="
				+ foodRating + ", helpfulCounter=" + helpfulCounter + ", date="
				+ date + ", title=" + title + ", text=" + text
				+ ", restaurant_id=" + restaurant_id + "]";
	}


}