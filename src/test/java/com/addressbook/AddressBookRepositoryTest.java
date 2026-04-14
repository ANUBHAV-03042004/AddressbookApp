package com.addressbook;

import com.addressbook.model.AddressBook;
import com.addressbook.repository.AddressBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test 1 — Repository layer test for AddressBook
 * Uses @DataJpaTest: spins up only JPA/H2, no full Spring context
 */
@DataJpaTest
public class AddressBookRepositoryTest {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @BeforeEach
    void setUp() {
        addressBookRepository.deleteAll();
    }

    @Test
    void testSaveAndFindAddressBook() {
        // Arrange
        AddressBook book = new AddressBook();
        book.setName("Friends");

        // Act
        AddressBook saved = addressBookRepository.save(book);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Friends", saved.getName());
    }

    @Test
    void testFindByNameIgnoreCase() {
        // Arrange
        AddressBook book = new AddressBook();
        book.setName("Office");
        addressBookRepository.save(book);

        // Act
        Optional<AddressBook> found = addressBookRepository.findByNameIgnoreCase("office");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Office", found.get().getName());
    }

    @Test
    void testExistsByNameIgnoreCase() {
        // Arrange
        AddressBook book = new AddressBook();
        book.setName("Family");
        addressBookRepository.save(book);

        // Assert
        assertTrue(addressBookRepository.existsByNameIgnoreCase("family"));
        assertFalse(addressBookRepository.existsByNameIgnoreCase("Strangers"));
    }

    @Test
    void testDeleteAddressBook() {
        // Arrange
        AddressBook book = new AddressBook();
        book.setName("ToDelete");
        AddressBook saved = addressBookRepository.save(book);

        // Act
        addressBookRepository.deleteById(saved.getId());

        // Assert
        assertFalse(addressBookRepository.existsById(saved.getId()));
    }
}
