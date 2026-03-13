package com.addressbook.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.addressbook.DTO.ContactDTO;
import com.addressbook.Entity.ContactEntity;
import com.addressbook.Service.AddressbookService;
@RestController
public class AddressbookController {

	@Autowired
	AddressbookService addressBookService;
//	add a new contact
	@PostMapping("/addcontact")
	public ResponseEntity<ContactEntity> addContact(@RequestBody ContactDTO contactDTO) {
		ContactEntity newContact= addressBookService.addContact(contactDTO);
		return new ResponseEntity<>(newContact, HttpStatus.CREATED);
	}
// read contacts
	@GetMapping("/getcontacts")
	public ResponseEntity<List<ContactDTO>> getAllContacts() {
		List<ContactDTO> contacts= addressBookService.getAllContacts();
		return new ResponseEntity<>(contacts, HttpStatus.OK);
	}
//	 edit a contact
	@PostMapping("/updatecontact/{firstName}/{lastName}")
	public ResponseEntity<ContactDTO> editContact(@PathVariable String firstName, @PathVariable String lastName, @RequestBody ContactDTO contactDTO){
		ContactEntity updateCon= addressBookService.editContact(firstName, lastName, contactDTO);
		if (updateCon == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);			
		}
		return new ResponseEntity<>(contactDTO, HttpStatus.OK);
	}

	
}
