package com.addressbook;

import com.addressbook.exception.AddressBookNotFoundException;
import com.addressbook.exception.DuplicateAddressBookException;
import com.addressbook.model.AddressBook;
import com.addressbook.repository.AddressBookRepository;
import com.addressbook.service.AddressBookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test 3 — Service layer unit test for AddressBookService
 * Uses Mockito — no DB, no Spring context needed.
 *
 * FIX: existsByNameIgnoreCase(name) no longer exists — replaced with
 *      existsByNameIgnoreCaseAndOwner(name, owner). All stubs updated.
 *
 * FIX: createAddressBook / getAllAddressBooks / deleteAddressBook all now require
 *      an owner string. Tests updated to pass OWNER and use owner-scoped stubs.
 *
 * FIX: getAddressBookById(id) internal overload still exists (used by ContactService),
 *      but the public API version is getAddressBookById(id, owner).
 *      Tests that exercised the no-owner overload now use the owner-scoped one.
 *
 * FIX: findAll() / existsById() no longer used by the service — stubs updated to
 *      match the actual repository calls (findByOwner, findByIdAndOwner).
 */
@ExtendWith(MockitoExtension.class)
public class AddressBookServiceTest {

    @Mock
    private AddressBookRepository addressBookRepository;

    @InjectMocks
    private AddressBookService addressBookService;

    private static final String OWNER = "testuser";

    @Test
    void testCreateAddressBook_Success() {
        // Arrange
        // FIX: existsByNameIgnoreCaseAndOwner(name, owner) — owner-scoped duplicate check
        when(addressBookRepository.existsByNameIgnoreCaseAndOwner("Friends", OWNER)).thenReturn(false);
        AddressBook saved = new AddressBook();
        saved.setId(1L);
        saved.setName("Friends");
        saved.setOwner(OWNER);
        when(addressBookRepository.save(any(AddressBook.class))).thenReturn(saved);

        // Act — FIX: createAddressBook(name, owner)
        AddressBook result = addressBookService.createAddressBook("Friends", OWNER);

        // Assert
        assertNotNull(result);
        assertEquals("Friends", result.getName());
        assertEquals(OWNER, result.getOwner());
        verify(addressBookRepository, times(1)).save(any(AddressBook.class));
    }

    @Test
    void testCreateAddressBook_ThrowsDuplicateException() {
        // Arrange
        // FIX: existsByNameIgnoreCaseAndOwner — per-user duplicate check
        when(addressBookRepository.existsByNameIgnoreCaseAndOwner("Friends", OWNER)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateAddressBookException.class,
                () -> addressBookService.createAddressBook("Friends", OWNER));
        verify(addressBookRepository, never()).save(any());
    }

    @Test
    void testGetAddressBookById_NotFound() {
        // Arrange
        // FIX: getAddressBookById(id, owner) — owner-scoped lookup via findByIdAndOwner
        when(addressBookRepository.findByIdAndOwner(99L, OWNER)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AddressBookNotFoundException.class,
                () -> addressBookService.getAddressBookById(99L, OWNER));
    }

    @Test
    void testGetAllAddressBooks() {
        // Arrange
        // FIX: getAllAddressBooks(owner) calls findByOwner, not findAll
        AddressBook b1 = new AddressBook(); b1.setName("A"); b1.setOwner(OWNER);
        AddressBook b2 = new AddressBook(); b2.setName("B"); b2.setOwner(OWNER);
        when(addressBookRepository.findByOwner(OWNER)).thenReturn(List.of(b1, b2));

        // Act
        List<AddressBook> result = addressBookService.getAllAddressBooks(OWNER);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteAddressBook_NotFound() {
        // Arrange
        // FIX: deleteAddressBook(id, owner) calls findByIdAndOwner, not existsById
        when(addressBookRepository.findByIdAndOwner(55L, OWNER)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AddressBookNotFoundException.class,
                () -> addressBookService.deleteAddressBook(55L, OWNER));
        verify(addressBookRepository, never()).deleteById(any());
        verify(addressBookRepository, never()).delete(any());
    }
}
