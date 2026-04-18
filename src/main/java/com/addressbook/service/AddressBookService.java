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

    /** FIX: name uniqueness is now per-user, not global */
    public AddressBook createAddressBook(String name, String owner) {
        if (addressBookRepository.existsByNameIgnoreCaseAndOwner(name, owner)) {
            throw new DuplicateAddressBookException(
                    "You already have an address book named '" + name + "'.");
        }
        AddressBook book = new AddressBook();
        book.setName(name);
        book.setOwner(owner);
        return addressBookRepository.save(book);
    }

    /** FIX: returns only the caller's books */
    public List<AddressBook> getAllAddressBooks(String owner) {
        return addressBookRepository.findByOwner(owner);
    }

    /** FIX: enforces ownership — returns 404 if book belongs to another user */
    public AddressBook getAddressBookById(Long id, String owner) {
        return addressBookRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new AddressBookNotFoundException(
                        "Address book with ID " + id + " not found."));
    }

    /**
     * Internal overload used by ContactService which already has a validated book.
     * Do NOT expose this via a controller directly.
     */
    public AddressBook getAddressBookById(Long id) {
        return addressBookRepository.findById(id)
                .orElseThrow(() -> new AddressBookNotFoundException(
                        "Address book with ID " + id + " not found."));
    }

    /** FIX: ownership checked — prevents deleting another user's book */
    public void deleteAddressBook(Long id, String owner) {
        // FIX: single DB call instead of existsById + deleteById
        AddressBook book = addressBookRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new AddressBookNotFoundException(
                        "Address book with ID " + id + " not found."));
        addressBookRepository.delete(book);
    }
}
