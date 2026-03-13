package com.addressbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AddressbookApp {

	public static void main(String[] args) {
		SpringApplication.run(AddressbookApp.class, args);
		System.out.println("Welcome to address book.");
	}

}
