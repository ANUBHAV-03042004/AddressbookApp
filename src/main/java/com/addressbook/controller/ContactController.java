package com.addressbook.controller;

import com.addressbook.dto.ContactDTO;
import com.addressbook.dto.ResponseDTO;
import com.addressbook.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addressbooks")
@Tag(name = "Contact", description = "APIs for managing Contacts within an Address Book")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Operation(summary = "Add a contact", description = "Adds a new contact to the specified address book")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Contact added successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Address book not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate contact")
    })
    @PostMapping("/{bookId}/contacts")
    public ResponseEntity<ResponseDTO<ContactDTO>> addContact(
            @Parameter(description = "ID of the address book", required = true, example = "1")
            @PathVariable Long bookId,
            @Valid @RequestBody ContactDTO contactDTO) {
        ContactDTO saved = contactService.addContact(bookId, contactDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>("Contact added successfully", saved));
    }

    @Operation(summary = "Get all contacts", description = "Returns all contacts in the specified address book")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contacts fetched successfully"),
        @ApiResponse(responseCode = "404", description = "Address book not found")
    })
    @GetMapping("/{bookId}/contacts")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> getAllContacts(
            @Parameter(description = "ID of the address book", required = true, example = "1")
            @PathVariable Long bookId) {
        List<ContactDTO> contacts = contactService.getAllContacts(bookId);
        return ResponseEntity.ok(new ResponseDTO<>("Contacts fetched successfully", contacts));
    }

    @Operation(summary = "Get contact by ID", description = "Returns a single contact by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contact found"),
        @ApiResponse(responseCode = "404", description = "Contact not found")
    })
    @GetMapping("/contacts/{contactId}")
    public ResponseEntity<ResponseDTO<ContactDTO>> getContactById(
            @Parameter(description = "ID of the contact", required = true, example = "1")
            @PathVariable Long contactId) {
        ContactDTO contact = contactService.getContactById(contactId);
        return ResponseEntity.ok(new ResponseDTO<>("Contact found", contact));
    }

    @Operation(summary = "Edit a contact", description = "Updates fields of an existing contact by first and last name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contact updated successfully"),
        @ApiResponse(responseCode = "404", description = "Contact or address book not found")
    })
    @PutMapping("/{bookId}/contacts/{firstName}/{lastName}")
    public ResponseEntity<ResponseDTO<ContactDTO>> editContact(
            @Parameter(description = "ID of the address book", required = true, example = "1") @PathVariable Long bookId,
            @Parameter(description = "First name of the contact", required = true, example = "John") @PathVariable String firstName,
            @Parameter(description = "Last name of the contact", required = true, example = "Doe") @PathVariable String lastName,
            @RequestBody ContactDTO updatedDTO) {
        ContactDTO updated = contactService.editContact(bookId, firstName, lastName, updatedDTO);
        return ResponseEntity.ok(new ResponseDTO<>("Contact updated successfully", updated));
    }

    @Operation(summary = "Delete a contact", description = "Deletes a contact from an address book by first and last name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contact deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Contact or address book not found")
    })
    @DeleteMapping("/{bookId}/contacts/{firstName}/{lastName}")
    public ResponseEntity<ResponseDTO<String>> deleteContact(
            @Parameter(description = "ID of the address book", required = true, example = "1") @PathVariable Long bookId,
            @Parameter(description = "First name of the contact", required = true, example = "John") @PathVariable String firstName,
            @Parameter(description = "Last name of the contact", required = true, example = "Doe") @PathVariable String lastName) {
        contactService.deleteContact(bookId, firstName, lastName);
        return ResponseEntity.ok(new ResponseDTO<>("Contact deleted successfully", null));
    }

    @Operation(summary = "Sort contacts by name", description = "Returns contacts sorted alphabetically by first and last name")
    @GetMapping("/{bookId}/contacts/sorted/name")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> getSortedByName(
            @Parameter(description = "ID of the address book", required = true, example = "1")
            @PathVariable Long bookId) {
        List<ContactDTO> sorted = contactService.getSortedByName(bookId);
        return ResponseEntity.ok(new ResponseDTO<>("Contacts sorted by name", sorted));
    }

    @Operation(summary = "Sort contacts by location", description = "Returns contacts sorted by city, state, then ZIP code")
    @GetMapping("/{bookId}/contacts/sorted/location")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> getSortedByLocation(
            @Parameter(description = "ID of the address book", required = true, example = "1")
            @PathVariable Long bookId) {
        List<ContactDTO> sorted = contactService.getSortedByCityStateZip(bookId);
        return ResponseEntity.ok(new ResponseDTO<>("Contacts sorted by city, state, zip", sorted));
    }

    @Operation(summary = "Search contacts by name", description = "Searches for contacts by full name across all address books")
    @GetMapping("/contacts/search")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> searchByName(
            @Parameter(description = "Full or partial name to search", required = true, example = "John Doe")
            @RequestParam String name) {
        List<ContactDTO> results = contactService.searchByName(name);
        return ResponseEntity.ok(new ResponseDTO<>("Search results for: " + name, results));
    }

    @Operation(summary = "Get contacts by city", description = "Returns all contacts from a given city across all address books")
    @GetMapping("/contacts/city/{city}")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> getByCity(
            @Parameter(description = "City name", required = true, example = "Mumbai")
            @PathVariable String city) {
        List<ContactDTO> contacts = contactService.searchByCity(city);
        return ResponseEntity.ok(new ResponseDTO<>("Contacts in city: " + city, contacts));
    }

    @Operation(summary = "Get contacts by state", description = "Returns all contacts from a given state across all address books")
    @GetMapping("/contacts/state/{state}")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> getByState(
            @Parameter(description = "State name", required = true, example = "Maharashtra")
            @PathVariable String state) {
        List<ContactDTO> contacts = contactService.searchByState(state);
        return ResponseEntity.ok(new ResponseDTO<>("Contacts in state: " + state, contacts));
    }

    @Operation(summary = "Count contacts by city or state", description = "Returns the count of contacts for a given city and/or state")
    @GetMapping("/contacts/count")
    public ResponseEntity<ResponseDTO<Map<String, Long>>> getCount(
            @Parameter(description = "City name", example = "Mumbai") @RequestParam(required = false) String city,
            @Parameter(description = "State name", example = "Maharashtra") @RequestParam(required = false) String state) {

        Map<String, Long> result = new java.util.HashMap<>();
        if (city != null) result.put("countByCity_" + city, contactService.countByCity(city));
        if (state != null) result.put("countByState_" + state, contactService.countByState(state));

        return ResponseEntity.ok(new ResponseDTO<>("Count results", result));
    }
}
