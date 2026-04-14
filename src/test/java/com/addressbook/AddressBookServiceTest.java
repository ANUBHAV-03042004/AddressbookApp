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
 * Uses Mockito — no DB, no Spring context needed
 */
@ExtendWith(MockitoExtension.class)
public class AddressBookServiceTest {

    @Mock
    private AddressBookRepository addressBookRepository;

    @InjectMocks
    private AddressBookService addressBookService;

    @Test
    void testCreateAddressBook_Success() {
        // Arrange
        when(addressBookRepository.existsByNameIgnoreCase("Friends")).thenReturn(false);
        AddressBook saved = new AddressBook();
        saved.setId(1L);
        saved.setName("Friends");
        when(addressBookRepository.save(any(AddressBook.class))).thenReturn(saved);

        // Act
        AddressBook result = addressBookService.createAddressBook("Friends");

        // Assert
        assertNotNull(result);
        assertEquals("Friends", result.getName());
        verify(addressBookRepository, times(1)).save(any(AddressBook.class));
    }

    @Test
    void testCreateAddressBook_ThrowsDuplicateException() {
        // Arrange
        when(addressBookRepository.existsByNameIgnoreCase("Friends")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateAddressBookException.class,
                () -> addressBookService.createAddressBook("Friends"));
        verify(addressBookRepository, never()).save(any());
    }

    @Test
    void testGetAddressBookById_NotFound() {
        // Arrange
        when(addressBookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AddressBookNotFoundException.class,
                () -> addressBookService.getAddressBookById(99L));
    }

    @Test
    void testGetAllAddressBooks() {
        // Arrange
        AddressBook b1 = new AddressBook(); b1.setName("A");
        AddressBook b2 = new AddressBook(); b2.setName("B");
        when(addressBookRepository.findAll()).thenReturn(List.of(b1, b2));

        // Act
        List<AddressBook> result = addressBookService.getAllAddressBooks();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteAddressBook_NotFound() {
        // Arrange
        when(addressBookRepository.existsById(55L)).thenReturn(false);

        // Act & Assert
        assertThrows(AddressBookNotFoundException.class,
                () -> addressBookService.deleteAddressBook(55L));
        verify(addressBookRepository, never()).deleteById(any());
    }
}
