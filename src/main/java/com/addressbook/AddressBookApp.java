package com.addressbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AddressBookApp {

	public static void main(String[] args) {
		SpringApplication.run(AddressBookApp.class, args);
		System.out.println("Welcome to address book.");
	}

}
