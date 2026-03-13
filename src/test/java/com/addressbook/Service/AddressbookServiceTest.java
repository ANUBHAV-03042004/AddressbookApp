package com.addressbook.Service;

import com.addressbook.DTO.ContactDTO;
import com.addressbook.Entity.ContactEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AddressbookService unit tests")
class AddressbookServiceTest {

    private AddressbookService service;

    @BeforeEach
    void setUp() {
        service = new AddressbookService();
    }

    private ContactDTO sampleDTO(String first, String last) {
        ContactDTO dto = new ContactDTO();
        dto.setFirstName(first);
        dto.setLastName(last);
        dto.setAddress("123 Main St");
        dto.setCity("Springfield");
        dto.setState("IL");
        dto.setZip("62701");
        dto.setPhoneNumber("2175550100");
        dto.setEmail(first.toLowerCase() + "@example.com");
        return dto;
    }

    // ── addContact ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("addContact: returns entity with all fields set correctly")
    void addContact_setsAllFields() {
        ContactDTO dto = sampleDTO("John", "Doe");
        ContactEntity result = service.addContact(dto);

        assertEquals("John",             result.getFirstName());
        assertEquals("Doe",              result.getLastName());
        assertEquals("123 Main St",      result.getAddress());
        assertEquals("Springfield",      result.getCity());
        assertEquals("IL",               result.getState());
        assertEquals("62701",            result.getZip());
        assertEquals("2175550100",       result.getPhoneNumber());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    @DisplayName("addContact: first contact gets id 1")
    void addContact_firstContactGetsId1() {
        ContactEntity result = service.addContact(sampleDTO("Alice", "Smith"));
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("addContact: second contact gets id 2")
    void addContact_secondContactGetsId2() {
        service.addContact(sampleDTO("Alice", "Smith"));
        ContactEntity second = service.addContact(sampleDTO("Bob", "Jones"));
        assertEquals(2L, second.getId());
    }

    @Test
    @DisplayName("addContact: contact appears in getAllContacts after being added")
    void addContact_appearsInGetAll() {
        service.addContact(sampleDTO("Alice", "Smith"));
        List<ContactDTO> all = service.getAllContacts();
        assertEquals(1, all.size());
        assertEquals("Alice", all.get(0).getFirstName());
    }

    // ── getAllContacts ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllContacts: returns empty list when no contacts added")
    void getAllContacts_emptyInitially() {
        List<ContactDTO> result = service.getAllContacts();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllContacts: returns all added contacts in order")
    void getAllContacts_returnsAllContacts() {
        service.addContact(sampleDTO("Alice", "Smith"));
        service.addContact(sampleDTO("Bob",   "Jones"));
        service.addContact(sampleDTO("Carol", "White"));

        List<ContactDTO> all = service.getAllContacts();
        assertEquals(3, all.size());
        assertEquals("Alice", all.get(0).getFirstName());
        assertEquals("Bob",   all.get(1).getFirstName());
        assertEquals("Carol", all.get(2).getFirstName());
    }

    @Test
    @DisplayName("getAllContacts: returned DTOs have all fields mapped correctly")
    void getAllContacts_dtoFieldsMappedCorrectly() {
        service.addContact(sampleDTO("Alice", "Smith"));
        ContactDTO dto = service.getAllContacts().get(0);

        assertEquals("Alice",             dto.getFirstName());
        assertEquals("Smith",             dto.getLastName());
        assertEquals("123 Main St",       dto.getAddress());
        assertEquals("Springfield",       dto.getCity());
        assertEquals("IL",                dto.getState());
        assertEquals("62701",             dto.getZip());
        assertEquals("2175550100",        dto.getPhoneNumber());
        assertEquals("alice@example.com", dto.getEmail());
    }

}
