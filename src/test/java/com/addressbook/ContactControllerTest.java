package com.addressbook;

import com.addressbook.controller.ContactController;
import com.addressbook.dto.ContactDTO;
import com.addressbook.exception.ContactNotFoundException;
import com.addressbook.exception.DuplicateContactException;
import com.addressbook.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test 6 — Controller layer test for ContactController
 * Uses @WebMvcTest + MockMvc to test all REST endpoints
 */
@WebMvcTest(ContactController.class)
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    private ContactDTO sampleDTO;

    @BeforeEach
    void setUp() {
        sampleDTO = new ContactDTO();
        sampleDTO.setId(1L);
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
    void testAddContact_Returns201() throws Exception {
        // Arrange
        when(contactService.addContact(eq(1L), any(ContactDTO.class))).thenReturn(sampleDTO);

        // Act & Assert
        mockMvc.perform(post("/api/addressbooks/1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Contact added successfully"))
                .andExpect(jsonPath("$.data.firstName").value("Anubhav"))
                .andExpect(jsonPath("$.data.lastName").value("Sharma"));
    }

    @Test
    void testAddContact_DuplicateReturns409() throws Exception {
        // Arrange
        when(contactService.addContact(eq(1L), any(ContactDTO.class)))
                .thenThrow(new DuplicateContactException("Contact 'Anubhav Sharma' already exists in this address book."));

        // Act & Assert
        mockMvc.perform(post("/api/addressbooks/1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Contact 'Anubhav Sharma' already exists in this address book."));
    }

    @Test
    void testGetAllContacts_Returns200() throws Exception {
        // Arrange
        when(contactService.getAllContacts(1L)).thenReturn(List.of(sampleDTO));

        // Act & Assert
        mockMvc.perform(get("/api/addressbooks/1/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].city").value("Mathura"));
    }

    @Test
    void testGetContactById_NotFound_Returns404() throws Exception {
        // Arrange
        when(contactService.getContactById(999L))
                .thenThrow(new ContactNotFoundException("Contact with ID 999 not found."));

        // Act & Assert
        mockMvc.perform(get("/api/addressbooks/contacts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Contact with ID 999 not found."));
    }

    @Test
    void testDeleteContact_Returns200() throws Exception {
        // Arrange
        doNothing().when(contactService).deleteContact(1L, "Anubhav", "Sharma");

        // Act & Assert
        mockMvc.perform(delete("/api/addressbooks/1/contacts/Anubhav/Sharma"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contact deleted successfully"));
    }

    @Test
    void testSearchByCity_Returns200() throws Exception {
        // Arrange
        when(contactService.searchByCity("Mathura")).thenReturn(List.of(sampleDTO));

        // Act & Assert
        mockMvc.perform(get("/api/addressbooks/contacts/city/Mathura"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].city").value("Mathura"));
    }
}
