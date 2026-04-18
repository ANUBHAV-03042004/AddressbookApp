package com.addressbook;

import com.addressbook.model.AddressBook;
import com.addressbook.model.Contact;
import com.addressbook.repository.AddressBookRepository;
import com.addressbook.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test 2 — Repository layer test for Contact
 * Tests all custom JPA query methods.
 *
 * FIX: AddressBook.owner is a non-nullable column (@Column(nullable = false)).
 *      The previous setUp() created an AddressBook without an owner, which caused
 *      a constraint-violation on save.  All AddressBook instances now set owner.
 */
@DataJpaTest
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressBookRepository addressBookRepository;

    private AddressBook book;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        addressBookRepository.deleteAll();

        book = new AddressBook();
        book.setName("TestBook");
        book.setOwner("testuser"); // FIX: owner is non-nullable — must be set before save
        book = addressBookRepository.save(book);
    }

    private Contact buildContact(String firstName, String lastName, String city, String state) {
        Contact c = new Contact();
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setAddress("123 Main St");
        c.setCity(city);
        c.setState(state);
        c.setZip("110001");
        c.setPhoneNumber("9876543210");
        c.setEmail(firstName.toLowerCase() + "@test.com");
        c.setAddressBook(book);
        return c;
    }

    @Test
    void testSaveAndFindContactById() {
        // Arrange
        Contact c = buildContact("Anubhav", "Sharma", "Delhi", "Delhi");
        Contact saved = contactRepository.save(c);

        // Act
        Optional<Contact> found = contactRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Anubhav", found.get().getFirstName());
        assertEquals("Sharma", found.get().getLastName());
    }

    @Test
    void testFindByAddressBookId() {
        // Arrange
        contactRepository.save(buildContact("Ravi", "Kumar", "Mumbai", "Maharashtra"));
        contactRepository.save(buildContact("Priya", "Singh", "Mumbai", "Maharashtra"));

        // Act
        List<Contact> contacts = contactRepository.findByAddressBookId(book.getId());

        // Assert
        assertEquals(2, contacts.size());
    }

    @Test
    void testFindByFirstAndLastNameIgnoreCaseAndBookId() {
        // Arrange
        contactRepository.save(buildContact("John", "Doe", "Agra", "UP"));

        // Act
        Optional<Contact> found = contactRepository
                .findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId("john", "doe", book.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    void testFindByCityIgnoreCase() {
        // Arrange
        contactRepository.save(buildContact("Amit", "Jain", "Mathura", "UP"));
        contactRepository.save(buildContact("Neha", "Gupta", "mathura", "UP"));

        // Act
        List<Contact> contacts = contactRepository.findByCityIgnoreCase("Mathura");

        // Assert
        assertEquals(2, contacts.size());
    }

    @Test
    void testExistsByDuplicateContact() {
        // Arrange
        contactRepository.save(buildContact("Duplicate", "User", "Noida", "UP"));

        // Assert
        assertTrue(contactRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(
                "duplicate", "user", book.getId()));
        assertFalse(contactRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(
                "New", "User", book.getId()));
    }

    @Test
    void testFindByAddressBookIdOrderByFirstNameAsc() {
        // Arrange
        contactRepository.save(buildContact("Zara", "Ali", "Delhi", "Delhi"));
        contactRepository.save(buildContact("Arjun", "Mehta", "Delhi", "Delhi"));
        contactRepository.save(buildContact("Meena", "Shah", "Delhi", "Delhi"));

        // Act
        List<Contact> sorted = contactRepository.findByAddressBookIdOrderByFirstNameAscLastNameAsc(book.getId());

        // Assert
        assertEquals("Arjun", sorted.get(0).getFirstName());
        assertEquals("Meena", sorted.get(1).getFirstName());
        assertEquals("Zara", sorted.get(2).getFirstName());
    }
}
