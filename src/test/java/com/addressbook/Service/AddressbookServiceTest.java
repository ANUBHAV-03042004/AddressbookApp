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

    // ── editContact ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("editContact: updates all fields when contact is found")
    void editContact_updatesAllFields() {
        service.addContact(sampleDTO("Alice", "Smith"));

        ContactDTO update = new ContactDTO();
        update.setFirstName("Alicia");
        update.setLastName("SmithUpdated");
        update.setAddress("456 Oak Ave");
        update.setCity("Chicago");
        update.setState("IL");
        update.setZip("60601");
        update.setPhoneNumber("3125550199");
        update.setEmail("alicia@example.com");

        ContactEntity updated = service.editContact("Alice", "Smith", update);

        assertNotNull(updated);
        assertEquals("Alicia",            updated.getFirstName());
        assertEquals("SmithUpdated",      updated.getLastName());
        assertEquals("456 Oak Ave",       updated.getAddress());
        assertEquals("Chicago",           updated.getCity());
        assertEquals("60601",             updated.getZip());
        assertEquals("3125550199",        updated.getPhoneNumber());
        assertEquals("alicia@example.com",updated.getEmail());
    }

    @Test
    @DisplayName("editContact: returns null when contact is not found")
    void editContact_returnsNullWhenNotFound() {
        ContactEntity result = service.editContact("Ghost", "User", sampleDTO("X", "Y"));
        assertNull(result);
    }

    @Test
    @DisplayName("editContact: match is case-insensitive")
    void editContact_caseInsensitiveMatch() {
        service.addContact(sampleDTO("Alice", "Smith"));
        ContactEntity updated = service.editContact("ALICE", "SMITH", sampleDTO("Alicia", "S"));
        assertNotNull(updated);
    }

    @Test
    @DisplayName("editContact: does not affect other contacts")
    void editContact_doesNotAffectOthers() {
        service.addContact(sampleDTO("Alice", "Smith"));
        service.addContact(sampleDTO("Bob",   "Jones"));

        service.editContact("Alice", "Smith", sampleDTO("Alicia", "NewSmith"));

        List<ContactDTO> all = service.getAllContacts();
        assertEquals("Bob",   all.get(1).getFirstName());
        assertEquals("Jones", all.get(1).getLastName());
    }

    // ── deleteContact ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteContact: returns true and removes contact when found")
    void deleteContact_removesContact() {
        service.addContact(sampleDTO("Alice", "Smith"));
        boolean result = service.deleteContact("Alice", "Smith");

        assertTrue(result);
        assertTrue(service.getAllContacts().isEmpty());
    }

    @Test
    @DisplayName("deleteContact: returns false when contact not found")
    void deleteContact_returnsFalseWhenNotFound() {
        boolean result = service.deleteContact("Ghost", "User");
        assertFalse(result);
    }

    @Test
    @DisplayName("deleteContact: match is case-insensitive")
    void deleteContact_caseInsensitiveMatch() {
        service.addContact(sampleDTO("Alice", "Smith"));
        boolean result = service.deleteContact("ALICE", "SMITH");
        assertTrue(result);
    }

    @Test
    @DisplayName("deleteContact: removes only the target contact, not others")
    void deleteContact_removesOnlyTarget() {
        service.addContact(sampleDTO("Alice", "Smith"));
        service.addContact(sampleDTO("Bob",   "Jones"));

        service.deleteContact("Alice", "Smith");

        List<ContactDTO> remaining = service.getAllContacts();
        assertEquals(1, remaining.size());
        assertEquals("Bob", remaining.get(0).getFirstName());
    }

    @Test
    @DisplayName("deleteContact: size decreases after each deletion")
    void deleteContact_sizeDecreasesAfterDeletion() {
        service.addContact(sampleDTO("Alice", "Smith"));
        service.addContact(sampleDTO("Bob",   "Jones"));
        service.addContact(sampleDTO("Carol", "White"));

        assertEquals(3, service.getAllContacts().size());
        service.deleteContact("Alice", "Smith");
        assertEquals(2, service.getAllContacts().size());
        service.deleteContact("Bob", "Jones");
        assertEquals(1, service.getAllContacts().size());
    }
}
