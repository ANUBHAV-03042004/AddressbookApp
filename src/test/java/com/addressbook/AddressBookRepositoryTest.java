package com.addressbook;

import com.addressbook.model.AddressBook;
import com.addressbook.repository.AddressBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test 1 — Repository layer test for AddressBook
 * Uses @DataJpaTest: spins up only JPA/H2, no full Spring context.
 *
 * FIX: findByNameIgnoreCase / existsByNameIgnoreCase no longer exist on the
 *      repository — they were replaced with owner-scoped variants:
 *        findByIdAndOwner, existsByNameIgnoreCaseAndOwner, findByOwner.
 *      All tests updated to use the actual repository methods and supply an owner.
 *
 * FIX: AddressBook.owner is @Column(nullable = false) — every saved book must
 *      have an owner set, otherwise H2 throws a constraint violation.
 */
@DataJpaTest
public class AddressBookRepositoryTest {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @BeforeEach
    void setUp() {
        addressBookRepository.deleteAll();
    }

    // Helper to build a saved AddressBook with an owner
    private AddressBook saveBook(String name, String owner) {
        AddressBook book = new AddressBook();
        book.setName(name);
        book.setOwner(owner); // FIX: owner is non-nullable
        return addressBookRepository.save(book);
    }

    @Test
    void testSaveAndFindAddressBook() {
        // Arrange & Act
        AddressBook saved = saveBook("Friends", "alice");

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Friends", saved.getName());
        assertEquals("alice", saved.getOwner());
    }

    @Test
    void testFindByIdAndOwner() {
        // Arrange
        // FIX: replaced findByNameIgnoreCase (removed) with findByIdAndOwner (actual method)
        AddressBook saved = saveBook("Office", "alice");

        // Act — correct owner finds the book
        Optional<AddressBook> found = addressBookRepository.findByIdAndOwner(saved.getId(), "alice");
        // Wrong owner gets nothing
        Optional<AddressBook> notFound = addressBookRepository.findByIdAndOwner(saved.getId(), "bob");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Office", found.get().getName());
        assertFalse(notFound.isPresent());
    }

    @Test
    void testExistsByNameIgnoreCaseAndOwner() {
        // Arrange
        // FIX: existsByNameIgnoreCase (removed) → existsByNameIgnoreCaseAndOwner(name, owner)
        saveBook("Family", "alice");

        // Assert — same owner, case-insensitive match
        assertTrue(addressBookRepository.existsByNameIgnoreCaseAndOwner("family", "alice"));
        assertTrue(addressBookRepository.existsByNameIgnoreCaseAndOwner("FAMILY", "alice"));
        // Different owner — must NOT find it (ownership isolation)
        assertFalse(addressBookRepository.existsByNameIgnoreCaseAndOwner("family", "bob"));
        // Different name — must NOT find it
        assertFalse(addressBookRepository.existsByNameIgnoreCaseAndOwner("Strangers", "alice"));
    }

    @Test
    void testFindByOwner() {
        // Arrange — two books owned by alice, one by bob
        saveBook("Alice Book 1", "alice");
        saveBook("Alice Book 2", "alice");
        saveBook("Bob Book",   "bob");

        // Act
        List<AddressBook> aliceBooks = addressBookRepository.findByOwner("alice");
        List<AddressBook> bobBooks   = addressBookRepository.findByOwner("bob");

        // Assert
        assertEquals(2, aliceBooks.size());
        assertEquals(1, bobBooks.size());
    }

    @Test
    void testDeleteAddressBook() {
        // Arrange
        AddressBook saved = saveBook("ToDelete", "alice");

        // Act
        addressBookRepository.deleteById(saved.getId());

        // Assert
        assertFalse(addressBookRepository.existsById(saved.getId()));
    }
}
