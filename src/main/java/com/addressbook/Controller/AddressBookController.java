package com.addressbook.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.addressbook.DTO.ContactDTO;
import com.addressbook.Entity.Contact;
import com.addressbook.Service.AddressBookService;
@RestController
public class AddressBookController {

	@Autowired
	AddressBookService addressBookService;
//	add a new contact
	@PostMapping("/addcontact")
	public ResponseEntity<Contact> addContact(@RequestBody ContactDTO contactDTO) {
		Contact newContact= addressBookService.addContact(contactDTO);
		return new ResponseEntity<>(newContact, HttpStatus.CREATED);
	}
 
	
}
