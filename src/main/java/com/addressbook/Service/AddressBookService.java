package com.addressbook.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.addressbook.DTO.ContactDTO;
import com.addressbook.Entity.Contact;

@Service
public class AddressBookService {
	
	List<Contact> contacts= new ArrayList<>();
//	 add a new contact
	public Contact addContact(ContactDTO contactDTO) {
		Contact newContact= toEntity(contactDTO);
		newContact.setId(contacts.size()+1);
		contacts.add(newContact);
		return newContact;
	}
 
	
	private Contact toEntity(ContactDTO contactDTO) {
		return new Contact(
				contactDTO.getFirstName(),
				contactDTO.getLastName(),
				contactDTO.getAddress(),
				contactDTO.getCity(),
				contactDTO.getState(),
				contactDTO.getZip(),
				contactDTO.getPhoneNumber(),
				contactDTO.getEmail()
		);
	}
	
	private ContactDTO toDTO(Contact contact) {
		ContactDTO dto= new ContactDTO();
		dto.setFirstName(contact.getFirstName());
		dto.setLastName(contact.getLastName());
		dto.setAddress(contact.getAddress());
		dto.setCity(contact.getCity());
		dto.setState(contact.getState());
		dto.setZip(contact.getZip());
		dto.setPhoneNumber(contact.getPhoneNumber());
		dto.setEmail(contact.getEmail());
		return dto;
	}
}
