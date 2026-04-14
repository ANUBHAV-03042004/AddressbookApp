package com.addressbook.service;

import com.addressbook.dto.ContactDTO;
import com.addressbook.exception.ContactNotFoundException;
import com.addressbook.exception.DuplicateContactException;
import com.addressbook.model.AddressBook;
import com.addressbook.model.Contact;
import com.addressbook.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressBookService addressBookService;


    public ContactDTO addContact(Long addressBookId, ContactDTO dto) {
        AddressBook book = addressBookService.getAddressBookById(addressBookId);

        // Duplicate check (same firstName + lastName in same book)
        if (contactRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(
                dto.getFirstName(), dto.getLastName(), addressBookId)) {
            throw new DuplicateContactException(
                    "Contact '" + dto.getFirstName() + " " + dto.getLastName() + "' already exists in this address book.");
        }

        Contact contact = mapToEntity(dto, book);
        Contact saved = contactRepository.save(contact);
        return mapToDTO(saved);
    }


    public List<ContactDTO> getAllContacts(Long addressBookId) {
        addressBookService.getAddressBookById(addressBookId); // validates book exists
        return contactRepository.findByAddressBookId(addressBookId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

 
    public ContactDTO getContactById(Long contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ContactNotFoundException("Contact with ID " + contactId + " not found."));
        return mapToDTO(contact);
    }

    public ContactDTO editContact(Long addressBookId, String firstName, String lastName, ContactDTO updatedDTO) {
        addressBookService.getAddressBookById(addressBookId);

        Contact contact = contactRepository
                .findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(firstName, lastName, addressBookId)
                .orElseThrow(() -> new ContactNotFoundException(
                        "Contact '" + firstName + " " + lastName + "' not found in address book."));

        // Update fields
        if (updatedDTO.getFirstName() != null) contact.setFirstName(updatedDTO.getFirstName());
        if (updatedDTO.getLastName() != null) contact.setLastName(updatedDTO.getLastName());
        if (updatedDTO.getAddress() != null) contact.setAddress(updatedDTO.getAddress());
        if (updatedDTO.getCity() != null) contact.setCity(updatedDTO.getCity());
        if (updatedDTO.getState() != null) contact.setState(updatedDTO.getState());
        if (updatedDTO.getZip() != null) contact.setZip(updatedDTO.getZip());
        if (updatedDTO.getPhoneNumber() != null) contact.setPhoneNumber(updatedDTO.getPhoneNumber());
        if (updatedDTO.getEmail() != null) contact.setEmail(updatedDTO.getEmail());

        Contact saved = contactRepository.save(contact);
        return mapToDTO(saved);
    }

    public void deleteContact(Long addressBookId, String firstName, String lastName) {
        addressBookService.getAddressBookById(addressBookId);

        Contact contact = contactRepository
                .findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(firstName, lastName, addressBookId)
                .orElseThrow(() -> new ContactNotFoundException(
                        "Contact '" + firstName + " " + lastName + "' not found in address book."));

        contactRepository.delete(contact);
    }

    public List<ContactDTO> searchByCity(String city) {
        return contactRepository.findByCityIgnoreCase(city)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

  
    public List<ContactDTO> searchByState(String state) {
        return contactRepository.findByStateIgnoreCase(state)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public long countByCity(String city) {
        return contactRepository.countByCityIgnoreCase(city);
    }

    public long countByState(String state) {
        return contactRepository.countByStateIgnoreCase(state);
    }

    public List<ContactDTO> getSortedByName(Long addressBookId) {
        addressBookService.getAddressBookById(addressBookId);
        return contactRepository.findByAddressBookIdOrderByFirstNameAscLastNameAsc(addressBookId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

 
    public List<ContactDTO> getSortedByCityStateZip(Long addressBookId) {
        addressBookService.getAddressBookById(addressBookId);
        return contactRepository.findByAddressBookIdOrderByCityAscStateAscZipAsc(addressBookId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ContactDTO> searchByName(String name) {
        return contactRepository.findByFullNameContaining(name)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

  
    private Contact mapToEntity(ContactDTO dto, AddressBook book) {
        Contact contact = new Contact();
        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setAddress(dto.getAddress());
        contact.setCity(dto.getCity());
        contact.setState(dto.getState());
        contact.setZip(dto.getZip());
        contact.setPhoneNumber(dto.getPhoneNumber());
        contact.setEmail(dto.getEmail());
        contact.setAddressBook(book);
        return contact;
    }

    private ContactDTO mapToDTO(Contact contact) {
        ContactDTO dto = new ContactDTO();
        dto.setId(contact.getId());
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
