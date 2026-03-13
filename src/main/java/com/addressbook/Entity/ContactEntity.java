package com.addressbook.Entity;

public class ContactEntity {
	
	private long id;
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String phoneNumber;
	private String email;
	
	public ContactEntity() {
		this("first_name_not_set","second_name_not_set","address_not_set","city_not_set","state_not_set","00000","000000000","email_not_set");
	}
	
	public ContactEntity(String firstName, String lastNmae, String address, String city, String state, String zip ,String phoneNumber, String email) {
		setFirstName(firstName);
		setLastName(lastNmae);
		setAddress(address);
		setCity(city);
		setState(state);
		setZip(zip);
		setPhoneNumber(phoneNumber);
		setEmail(email);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id= id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName= firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName= lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address= address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city= city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state= state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip= zip;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber= phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email= email;
	}
	
	@Override
	public String toString() {
		return "Name:"+ getFirstName() +" "+ getLastName() 
		+"\nAddress:"+ getAddress() 
		+"\nCity:"+ getCity() 
		+"\nState:"+ getState() 
		+"\nZip:"+ getZip() 
		+"\nPhone Number:"+ getPhoneNumber() 
		+"\nEmail:"+ getEmail();
	}

}
