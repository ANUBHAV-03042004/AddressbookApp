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
// read contacts
	@GetMapping("/getcontacts")
	public ResponseEntity<List<ContactDTO>> getAllContacts() {
		List<ContactDTO> contacts= addressBookService.getAllContacts();
		return new ResponseEntity<>(contacts, HttpStatus.OK);
	}
//	 edit a contact
	@PostMapping("/updatecontact/{firstName}/{lastName}")
	public ResponseEntity<ContactDTO> editContact(@PathVariable String firstName, @PathVariable String lastName, @RequestBody ContactDTO contactDTO){
		Contact updateCon= addressBookService.editContact(firstName, lastName, contactDTO);
		if (updateCon == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);			
		}
		return new ResponseEntity<>(contactDTO, HttpStatus.OK);
	}
//	to delete a person

	@DeleteMapping("/deletecontact/{firstName}/{lastName}")
	public ResponseEntity<String> deleteContact(@PathVariable String firstName, @PathVariable String lastName){
		boolean deleteCheck= addressBookService.deleteContact(firstName, lastName);
		if(deleteCheck) {
			return new ResponseEntity<>("Contact deleted.", HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Contact not found", HttpStatus.NOT_FOUND);
		}
	}
	

	
}
