package com.addressbook.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.addressbook.DTO.ContactDTO;
import com.addressbook.Entity.ContactEntity;
import com.addressbook.Service.AddressbookService;
@RestController
public class AddressbookController {

	@Autowired
	AddressbookService addressbookService;
	
	@PostMapping("/addcontact")
	public ResponseEntity<ContactEntity> addContact(@RequestBody ContactDTO contactDTO) {
		ContactEntity newContact= addressbookService.addContact(contactDTO);
		return new ResponseEntity<>(newContact, HttpStatus.CREATED);
	}
 
	
}
