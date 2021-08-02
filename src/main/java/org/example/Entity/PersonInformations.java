package org.example.Entity;

import org.example.Dao.PersonInformationDao;

import java.util.concurrent.ConcurrentHashMap;

public class PersonInformations {

	private String name;

	private String street;

	private String city;

	private String country;

	private String phone;

	private String email;

	private int id;

	PersonInformationDao personInformationDao;

	public PersonInformations() {

	}

	public PersonInformations(int id, String name, String street, String city, String country, String phone, String email) {
		setName(name);
		setStreet(street);
		setCity(city);
		setCountry(country);
		setPhone(phone);
		setEmail(email);
		setId(id);

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
