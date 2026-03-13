package com.addressbook;

import com.addressbook.DTO.ContactDTO;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = AddressbookApp.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Addressbook integration tests (full Spring context)")
class AddressbookAppTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ContactDTO makeDTO(String first, String last) {
        ContactDTO dto = new ContactDTO();
        dto.setFirstName(first);
        dto.setLastName(last);
        dto.setAddress("100 Test Rd");
        dto.setCity("TestCity");
        dto.setState("TC");
        dto.setZip("00000");
        dto.setPhoneNumber("0000000000");
        dto.setEmail(first.toLowerCase() + "@test.com");
        return dto;
    }

    @Test
    @Order(1)
    @DisplayName("Spring application context loads without errors")
    void contextLoads() {
    }

    @Test
    @Order(2)
    @DisplayName("POST /addcontact: adds contact and returns 201")
    void addContact_returns201() throws Exception {
        mockMvc.perform(post("/addcontact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makeDTO("Alice", "Smith"))))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.firstName", is("Alice")))
               .andExpect(jsonPath("$.lastName",  is("Smith")))
               .andExpect(jsonPath("$.id",         is(1)));
    }

    @Test
    @Order(3)
    @DisplayName("POST /addcontact: second contact gets id 2")
    void addContact_secondContactId2() throws Exception {
        mockMvc.perform(post("/addcontact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makeDTO("Bob", "Jones"))))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id", is(2)));
    }

    @Test
    @Order(4)
    @DisplayName("GET /getcontacts: returns all added contacts")
    void getContacts_returnsAllContacts() throws Exception {
        mockMvc.perform(get("/getcontacts"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }



}
