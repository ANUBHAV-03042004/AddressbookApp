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
// read contacts
	public List<ContactDTO> getAllContacts() {
		List<ContactDTO> contactDTOs= new ArrayList<>();
		for (Contact contact:contacts) {
			contactDTOs.add(toDTO(contact));
		}
		return contactDTOs;
	}
	
// edit a contact
	public Contact editContact(String firstName, String lastName, ContactDTO contactDTO) {
		for (Contact contact:contacts) {
			if (contact.getFirstName().equalsIgnoreCase(firstName) && contact.getLastName().equalsIgnoreCase(lastName)) {
				contact.setFirstName(contactDTO.getFirstName());
				contact.setLastName(contactDTO.getLastName());
				contact.setAddress(contactDTO.getAddress());
				contact.setCity(contactDTO.getCity());
				contact.setState(contactDTO.getState());
				contact.setZip(contactDTO.getZip());
				contact.setPhoneNumber(contactDTO.getPhoneNumber());
				contact.setEmail(contactDTO.getEmail());
				return contact;
			}
		}
		return null;
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
