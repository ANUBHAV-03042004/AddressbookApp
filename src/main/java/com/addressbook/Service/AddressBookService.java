package com.addressbook.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.addressbook.DTO.ContactDTO;
import com.addressbook.Entity.ContactEntity;

@Service
public class AddressbookService {
	
	List<ContactEntity> contacts= new ArrayList<>();
	 
	public ContactEntity addContact(ContactDTO contactDTO) {
		ContactEntity newContact= toEntity(contactDTO);
		newContact.setId(contacts.size()+1);
		contacts.add(newContact);
		return newContact;
	}
 
	
	private ContactEntity toEntity(ContactDTO contactDTO) {
		return new ContactEntity(
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
	
	private ContactDTO toDTO(ContactEntity contact) {
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
