package edu.ucsb.testuggine;

import java.util.ArrayList;



public class TripAdvisorRestaurant {
	String name;
	Address address;
	ArrayList<String> phoneNumbers;
	String url; // The URL to mine, not the restaurant's website URL
	String details; // Price range, good for kids etc
	String id;
	String region_id;
	String type;
	/**
	 * @param name
	 * @param address
	 * @param phoneNumbers
	 * @param url
	 * @param details
	 * @param id
	 * @param region_id
	 * @param type
	 */
	public TripAdvisorRestaurant(String name, Address address,
			ArrayList<String> phoneNumbers, String url, String details,
			String id, String region_id, String type) {
		this.name = name;
		this.address = address;
		this.phoneNumbers = phoneNumbers;
		this.url = url;
		this.details = details;
		this.id = id;
		this.region_id = region_id;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "TripAdvisorRestaurant [name=" + name + ", address=" + address
				+ ", phoneNumbers=" + phoneNumbers + ", url=" + url
				+ ", details=" + details + ", id=" + id + ", region_id="
				+ region_id + ", type=" + type + "]";
	}

	
}
