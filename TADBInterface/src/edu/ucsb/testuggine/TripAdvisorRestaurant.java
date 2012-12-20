package edu.ucsb.testuggine;



public class TripAdvisorRestaurant {
	String name;
	Address address;
	String phoneNumber;
	String url; // The URL to mine, not the restaurant's website URL
	String details; // Price range, good for kids etc
	String id;
	String region_id;
	String type;
	
	/**
	 * @param name
	 * @param address
	 * @param phoneNumber
	 * @param url
	 * @param details
	 * @param id
	 * @param region_id
	 * @param type
	 */
	public TripAdvisorRestaurant(String name, Address address,
			String phoneNumber, String url, String details, String id,
			String region_id, String type) {
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.url = url;
		this.details = details;
		this.id = id;
		this.region_id = region_id;
		this.type = type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((details == null) ? 0 : details.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result
				+ ((region_id == null) ? 0 : region_id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TripAdvisorRestaurant other = (TripAdvisorRestaurant) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (details == null) {
			if (other.details != null)
				return false;
		} else if (!details.equals(other.details))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (region_id == null) {
			if (other.region_id != null)
				return false;
		} else if (!region_id.equals(other.region_id))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TripAdvisorRestaurant [name=" + name + ", address=" + address
				+ ", phoneNumber=" + phoneNumber + ", url=" + url
				+ ", details=" + details + ", id=" + id + ", region_id="
				+ region_id + ", type=" + type + "]";
	}
	
}
