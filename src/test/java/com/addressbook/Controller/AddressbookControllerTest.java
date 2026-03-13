package com.addressbook.Controller;

import com.addressbook.DTO.ContactDTO;
import com.addressbook.Entity.ContactEntity;
import com.addressbook.Service.AddressbookService;

import tools.jackson.databind.ObjectMapper;               

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest; 
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressbookController.class)
@DisplayName("AddressbookController MockMvc tests")
class AddressbookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressbookService addressBookService;

    @Autowired
    private ObjectMapper objectMapper;

    private ContactDTO sampleDTO;
    private ContactEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleDTO = new ContactDTO();
        sampleDTO.setFirstName("John");
        sampleDTO.setLastName("Doe");
        sampleDTO.setAddress("123 Main St");
        sampleDTO.setCity("Springfield");
        sampleDTO.setState("IL");
        sampleDTO.setZip("62701");
        sampleDTO.setPhoneNumber("2175550100");
        sampleDTO.setEmail("john@example.com");

        sampleEntity = new ContactEntity(
                "John", "Doe", "123 Main St",
                "Springfield", "IL", "62701",
                "2175550100", "john@example.com");
        sampleEntity.setId(1L);
    }

    // ── POST /addcontact ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /addcontact: returns 201 and saved entity")
    void addContact_returns201WithEntity() throws Exception {
        when(addressBookService.addContact(any(ContactDTO.class))).thenReturn(sampleEntity);

        mockMvc.perform(post("/addcontact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.firstName", is("John")))
               .andExpect(jsonPath("$.lastName",  is("Doe")))
               .andExpect(jsonPath("$.id",         is(1)));
    }

    @Test
    @DisplayName("POST /addcontact: delegates to service once")
    void addContact_delegatesToServiceOnce() throws Exception {
        when(addressBookService.addContact(any())).thenReturn(sampleEntity);

        mockMvc.perform(post("/addcontact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)));

        verify(addressBookService, times(1)).addContact(any(ContactDTO.class));
    }

    @Test
    @DisplayName("POST /addcontact: request body fields are forwarded to service")
    void addContact_forwardsBodyToService() throws Exception {
        when(addressBookService.addContact(any())).thenReturn(sampleEntity);

        mockMvc.perform(post("/addcontact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
               .andExpect(status().isCreated());

        verify(addressBookService).addContact(any(ContactDTO.class));
    }

    // ── GET /getcontacts ──────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /getcontacts: returns 200 and list of contacts")
    void getAllContacts_returns200WithList() throws Exception {
        ContactDTO second = new ContactDTO();
        second.setFirstName("Jane");
        second.setLastName("Smith");
        when(addressBookService.getAllContacts()).thenReturn(List.of(sampleDTO, second));

        mockMvc.perform(get("/getcontacts"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$",              hasSize(2)))
               .andExpect(jsonPath("$[0].firstName", is("John")))
               .andExpect(jsonPath("$[1].firstName", is("Jane")));
    }

    @Test
    @DisplayName("GET /getcontacts: returns 200 with empty list when no contacts")
    void getAllContacts_returns200WithEmptyList() throws Exception {
        when(addressBookService.getAllContacts()).thenReturn(List.of());

        mockMvc.perform(get("/getcontacts"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /getcontacts: response contains all expected fields")
    void getAllContacts_responseContainsAllFields() throws Exception {
        when(addressBookService.getAllContacts()).thenReturn(List.of(sampleDTO));

        mockMvc.perform(get("/getcontacts"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].firstName",   is("John")))
               .andExpect(jsonPath("$[0].lastName",    is("Doe")))
               .andExpect(jsonPath("$[0].address",     is("123 Main St")))
               .andExpect(jsonPath("$[0].city",        is("Springfield")))
               .andExpect(jsonPath("$[0].state",       is("IL")))
               .andExpect(jsonPath("$[0].zip",         is("62701")))
               .andExpect(jsonPath("$[0].phoneNumber", is("2175550100")))
               .andExpect(jsonPath("$[0].email",       is("john@example.com")));
    }

    // ── POST /updatecontact/{firstName}/{lastName} ────────────────────────────

    @Test
    @DisplayName("POST /updatecontact: returns 200 and updated DTO when contact exists")
    void editContact_returns200WhenFound() throws Exception {
        when(addressBookService.editContact(eq("John"), eq("Doe"), any(ContactDTO.class)))
                .thenReturn(sampleEntity);

        mockMvc.perform(post("/updatecontact/John/Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    @DisplayName("POST /updatecontact: returns 404 when contact not found")
    void editContact_returns404WhenNotFound() throws Exception {
        when(addressBookService.editContact(eq("Ghost"), eq("User"), any()))
                .thenReturn(null);

        mockMvc.perform(post("/updatecontact/Ghost/User")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /updatecontact: path variables are passed to service")
    void editContact_pathVariablesPassedToService() throws Exception {
        when(addressBookService.editContact(eq("John"), eq("Doe"), any())).thenReturn(sampleEntity);

        mockMvc.perform(post("/updatecontact/John/Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)));

        verify(addressBookService).editContact(eq("John"), eq("Doe"), any(ContactDTO.class));
    }
}