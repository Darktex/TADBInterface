package edu.ucsb.testuggine;

import edu.princeton.cs.algs4.Date;

public class TripAdvisorReview {
	String id;
	TripAdvisorUser author;

	Float globalRating; // Ratings. This is the final one.
	Float valueRating;
	Float atmosphereRating;
	Float serviceRating;
	Float foodRating;

	Integer helpfulCounter;

	Date date;
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
			Date date, String title, String text, String restaurant_id) {
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
	
	public TripAdvisorReview(String id, TripAdvisorUser author,
			Float globalRating, Float valueRating, Float atmosphereRating,
			Float serviceRating, Float foodRating, Integer helpfulCounter,
			String dateStr, String title, String text, String restaurant_id) {
		this.id = id;
		this.author = author;
		this.globalRating = globalRating;
		this.valueRating = valueRating;
		this.atmosphereRating = atmosphereRating;
		this.serviceRating = serviceRating;
		this.foodRating = foodRating;
		this.helpfulCounter = helpfulCounter;
		this.date = convertDate(dateStr);
		this.title = title;
		this.text = text;
		this.restaurant_id = restaurant_id;
	}
	
	private static Date convertDate(String dateStr) {
		Integer month_start_pos = 0;
		Integer month_end_pos = dateStr.indexOf(" ");
		
		Integer day_start_pos = month_end_pos + 1;
		Integer day_end_pos = dateStr.indexOf(", ", day_start_pos);
		
		Integer year_start_pos = day_end_pos + 2; // Comma + space
		Integer year_end_pos = dateStr.length();
		if (dateStr.contains("\nNEW")) year_end_pos = dateStr.indexOf("\nNEW");
		
		String monthStr = dateStr.substring(month_start_pos, month_end_pos).toUpperCase();
		Month month = Month.valueOf(monthStr);
		Integer monthNum = month.ordinal() + 1; // enum is 0-indexed
		Integer day = Integer.valueOf(dateStr.substring(day_start_pos, day_end_pos).trim());
		Integer year = Integer.valueOf(dateStr.substring(year_start_pos, year_end_pos).trim());
		
		return new Date(monthNum, day, year);
	}
	
	private enum Month {
		JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
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