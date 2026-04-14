package com.addressbook;

import com.addressbook.dto.ContactDTO;
import com.addressbook.exception.ContactNotFoundException;
import com.addressbook.exception.DuplicateContactException;
import com.addressbook.model.AddressBook;
import com.addressbook.model.Contact;
import com.addressbook.repository.ContactRepository;
import com.addressbook.service.AddressBookService;
import com.addressbook.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
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
 * Test 4 — Service layer unit test for ContactService
 * Uses Mockito — no DB needed
 */
@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private AddressBookService addressBookService;

    @InjectMocks
    private ContactService contactService;

    private AddressBook mockBook;
    private ContactDTO sampleDTO;

    @BeforeEach
    void setUp() {
        mockBook = new AddressBook();
        mockBook.setId(1L);
        mockBook.setName("TestBook");

        sampleDTO = new ContactDTO();
        sampleDTO.setFirstName("Anubhav");
        sampleDTO.setLastName("Sharma");
        sampleDTO.setAddress("123 MG Road");
        sampleDTO.setCity("Mathura");
        sampleDTO.setState("UP");
        sampleDTO.setZip("281001");
        sampleDTO.setPhoneNumber("9876543210");
        sampleDTO.setEmail("anubhav@test.com");
    }

    @Test
    void testAddContact_Success() {
        // Arrange
        when(addressBookService.getAddressBookById(1L)).thenReturn(mockBook);
        when(contactRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(
                "Anubhav", "Sharma", 1L)).thenReturn(false);

        Contact savedContact = new Contact();
        savedContact.setId(10L);
        savedContact.setFirstName("Anubhav");
        savedContact.setLastName("Sharma");
        savedContact.setAddress("123 MG Road");
        savedContact.setCity("Mathura");
        savedContact.setState("UP");
        savedContact.setZip("281001");
        savedContact.setPhoneNumber("9876543210");
        savedContact.setEmail("anubhav@test.com");
        savedContact.setAddressBook(mockBook);
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        // Act
        ContactDTO result = contactService.addContact(1L, sampleDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Anubhav", result.getFirstName());
        assertEquals("Sharma", result.getLastName());
        assertEquals(10L, result.getId());
    }

    @Test
    void testAddContact_ThrowsDuplicateException() {
        // Arrange
        when(addressBookService.getAddressBookById(1L)).thenReturn(mockBook);
        when(contactRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(
                "Anubhav", "Sharma", 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateContactException.class,
                () -> contactService.addContact(1L, sampleDTO));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void testGetContactById_NotFound() {
        // Arrange
        when(contactRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ContactNotFoundException.class,
                () -> contactService.getContactById(999L));
    }

    @Test
    void testDeleteContact_Success() {
        // Arrange
        when(addressBookService.getAddressBookById(1L)).thenReturn(mockBook);
        Contact c = new Contact();
        c.setId(5L);
        c.setFirstName("Anubhav");
        c.setLastName("Sharma");
        c.setAddressBook(mockBook);
        when(contactRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(
                "Anubhav", "Sharma", 1L)).thenReturn(Optional.of(c));

        // Act
        contactService.deleteContact(1L, "Anubhav", "Sharma");

        // Assert
        verify(contactRepository, times(1)).delete(c);
    }

    @Test
    void testGetAllContacts_ReturnsCorrectSize() {
        // Arrange
        when(addressBookService.getAddressBookById(1L)).thenReturn(mockBook);

        Contact c1 = new Contact(); c1.setFirstName("A"); c1.setLastName("B");
        c1.setAddress("x"); c1.setCity("y"); c1.setState("z");
        c1.setZip("123456"); c1.setPhoneNumber("1234567890");
        c1.setEmail("a@b.com"); c1.setAddressBook(mockBook);

        Contact c2 = new Contact(); c2.setFirstName("C"); c2.setLastName("D");
        c2.setAddress("x"); c2.setCity("y"); c2.setState("z");
        c2.setZip("123456"); c2.setPhoneNumber("1234567890");
        c2.setEmail("c@d.com"); c2.setAddressBook(mockBook);

        when(contactRepository.findByAddressBookId(1L)).thenReturn(List.of(c1, c2));

        // Act
        List<ContactDTO> result = contactService.getAllContacts(1L);

        // Assert
        assertEquals(2, result.size());
    }
}
