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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
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

    @Test
    @Order(5)
    @DisplayName("POST /updatecontact: updates existing contact and returns 200")
    void updateContact_returns200() throws Exception {
        ContactDTO update = makeDTO("AliceUpdated", "SmithUpdated");

        mockMvc.perform(post("/updatecontact/Alice/Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName", is("AliceUpdated")));
    }

    @Test
    @Order(6)
    @DisplayName("POST /updatecontact: returns 404 for non-existent contact")
    void updateContact_returns404ForMissing() throws Exception {
        mockMvc.perform(post("/updatecontact/Ghost/User")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makeDTO("X", "Y"))))
               .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    @DisplayName("DELETE /deletecontact: deletes existing contact and returns 200")
    void deleteContact_returns200() throws Exception {
        mockMvc.perform(delete("/deletecontact/Bob/Jones"))
               .andExpect(status().isOk())
               .andExpect(content().string("Contact deleted."));
    }

    @Test
    @Order(8)
    @DisplayName("DELETE /deletecontact: returns 404 for non-existent contact")
    void deleteContact_returns404ForMissing() throws Exception {
        mockMvc.perform(delete("/deletecontact/Nobody/Here"))
               .andExpect(status().isNotFound())
               .andExpect(content().string("Contact not found"));
    }
}
