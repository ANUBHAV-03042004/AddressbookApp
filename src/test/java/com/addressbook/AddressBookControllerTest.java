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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test 5 — Controller layer test for AddressBookController
 * Uses @WebMvcTest: loads only the web layer with MockMvc.
 *
 * FIX: @WithMockUser was unresolved — missing spring-security-test on the
 *      test classpath. Import is:
 *        org.springframework.security.test.context.support.WithMockUser
 *      Add to pom.xml if absent:
 *        <dependency>
 *          <groupId>org.springframework.security</groupId>
 *          <artifactId>spring-security-test</artifactId>
 *          <scope>test</scope>
 *        </dependency>
 *
 * FIX: All endpoints use @AuthenticationPrincipal — tests now annotated with
 *      @WithMockUser(username = "testuser") to supply a principal, preventing
 *      401 responses from the security filter chain.
 *
 * FIX: POST and DELETE require a CSRF token in the @WebMvcTest slice —
 *      .with(csrf()) added to those requests.
 *
 * FIX: Service stubs updated — createAddressBook / getAllAddressBooks /
 *      deleteAddressBook all now take an owner String. Stubs use anyString()
 *      so they match whatever the controller extracts from the principal.
 *
 * FIX: getAddressBookById stub updated to the owner-scoped overload.
 */
@WebMvcTest(AddressBookController.class)
public class AddressBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressBookService addressBookService;

    @Test
    @WithMockUser(username = "testuser") // FIX: supply authenticated principal
    void testCreateAddressBook_Returns201() throws Exception {
        // Arrange
        AddressBook book = new AddressBook();
        book.setId(1L);
        book.setName("Friends");
        book.setOwner("testuser");
        // FIX: createAddressBook(name, owner) — use anyString() for owner from principal
        when(addressBookService.createAddressBook(eq("Friends"), anyString())).thenReturn(book);

        // Act & Assert
        mockMvc.perform(post("/api/addressbooks")
                        .with(csrf()) // FIX: CSRF token required for POST
                        .param("name", "Friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Address book created successfully"))
                .andExpect(jsonPath("$.data.name").value("Friends"));
    }

    @Test
    @WithMockUser(username = "testuser") // FIX: supply authenticated principal
    void testGetAllAddressBooks_Returns200() throws Exception {
        // Arrange
        AddressBook b1 = new AddressBook(); b1.setId(1L); b1.setName("Friends"); b1.setOwner("testuser");
        AddressBook b2 = new AddressBook(); b2.setId(2L); b2.setName("Office");  b2.setOwner("testuser");
        // FIX: getAllAddressBooks(owner) — use anyString() for owner from principal
        when(addressBookService.getAllAddressBooks(anyString())).thenReturn(List.of(b1, b2));

        // Act & Assert
        mockMvc.perform(get("/api/addressbooks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(username = "testuser") // FIX: supply authenticated principal
    void testGetAddressBookById_NotFound_Returns404() throws Exception {
        // Arrange
        // FIX: getAddressBookById(id, owner) — owner-scoped overload; use anyString()
        when(addressBookService.getAddressBookById(eq(99L), anyString()))
                .thenThrow(new AddressBookNotFoundException("Address book with ID 99 not found."));

        // Act & Assert
        mockMvc.perform(get("/api/addressbooks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Address book with ID 99 not found."));
    }

    @Test
    @WithMockUser(username = "testuser") // FIX: supply authenticated principal
    void testDeleteAddressBook_Returns200() throws Exception {
        // Arrange
        // FIX: deleteAddressBook(id, owner) — use anyString() for owner from principal
        doNothing().when(addressBookService).deleteAddressBook(eq(1L), anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/addressbooks/1")
                        .with(csrf())) // FIX: CSRF token required for DELETE
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Address book deleted successfully"));
    }
}
