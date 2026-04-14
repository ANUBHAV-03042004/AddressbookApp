package com.addressbook.service;

import com.addressbook.exception.AddressBookNotFoundException;
import com.addressbook.exception.DuplicateAddressBookException;
import com.addressbook.model.AddressBook;
import com.addressbook.repository.AddressBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;

    // Create a new address book
    public AddressBook createAddressBook(String name) {
        if (addressBookRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateAddressBookException("Address book '" + name + "' already exists.");
        }
        AddressBook book = new AddressBook();
        book.setName(name);
        return addressBookRepository.save(book);
    }

    // Get all address books
    public List<AddressBook> getAllAddressBooks() {
        return addressBookRepository.findAll();
    }

    // Get by ID
    public AddressBook getAddressBookById(Long id) {
        return addressBookRepository.findById(id)
                .orElseThrow(() -> new AddressBookNotFoundException("Address book with ID " + id + " not found."));
    }

    // Get by name
    public AddressBook getAddressBookByName(String name) {
        return addressBookRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new AddressBookNotFoundException("Address book '" + name + "' not found."));
    }

    // Delete address book
    public void deleteAddressBook(Long id) {
        if (!addressBookRepository.existsById(id)) {
            throw new AddressBookNotFoundException("Address book with ID " + id + " not found.");
        }
        addressBookRepository.deleteById(id);
    }
}
