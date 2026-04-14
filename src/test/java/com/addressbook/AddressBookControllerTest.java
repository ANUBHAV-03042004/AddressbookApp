package com.addressbook;

import com.addressbook.controller.AddressBookController;
import com.addressbook.exception.AddressBookNotFoundException;
import com.addressbook.model.AddressBook;
import com.addressbook.service.AddressBookService;
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
 * Test 5 — Controller layer test for AddressBookController
 * Uses @WebMvcTest: loads only the web layer with MockMvc
 */
@WebMvcTest(AddressBookController.class)
public class AddressBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressBookService addressBookService;

    @Test
    void testCreateAddressBook_Returns201() throws Exception {
        // Arrange
        AddressBook book = new AddressBook();
        book.setId(1L);
        book.setName("Friends");
        when(addressBookService.createAddressBook("Friends")).thenReturn(book);

        // Act & Assert
        mockMvc.perform(post("/api/addressbooks")
                        .param("name", "Friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Address book created successfully"))
                .andExpect(jsonPath("$.data.name").value("Friends"));
    }

    @Test
    void testGetAllAddressBooks_Returns200() throws Exception {
        // Arrange
        AddressBook b1 = new AddressBook(); b1.setId(1L); b1.setName("Friends");
        AddressBook b2 = new AddressBook(); b2.setId(2L); b2.setName("Office");
        when(addressBookService.getAllAddressBooks()).thenReturn(List.of(b1, b2));

        // Act & Assert
        mockMvc.perform(get("/api/addressbooks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetAddressBookById_NotFound_Returns404() throws Exception {
        // Arrange
        when(addressBookService.getAddressBookById(99L))
                .thenThrow(new AddressBookNotFoundException("Address book with ID 99 not found."));

        // Act & Assert
        mockMvc.perform(get("/api/addressbooks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Address book with ID 99 not found."));
    }

    @Test
    void testDeleteAddressBook_Returns200() throws Exception {
        // Arrange
        doNothing().when(addressBookService).deleteAddressBook(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/addressbooks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Address book deleted successfully"));
    }
}
