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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addressbooks")
@Tag(name = "Contact", description = "APIs for managing Contacts within an Address Book")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Operation(summary = "Add a contact")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Contact added"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Address book not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate contact")
    })
    @PostMapping("/{bookId}/contacts")
    public ResponseEntity<ResponseDTO<ContactDTO>> addContact(
            @PathVariable Long bookId,
            @Valid @RequestBody ContactDTO contactDTO,
            @AuthenticationPrincipal UserDetails principal) {

        ContactDTO saved = contactService.addContact(bookId, contactDTO, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>("Contact added successfully", saved));
    }

    @Operation(summary = "Get all contacts")
    @GetMapping("/{bookId}/contacts")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> getAllContacts(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails principal) {

        List<ContactDTO> contacts = contactService.getAllContacts(bookId, principal.getUsername());
        return ResponseEntity.ok(new ResponseDTO<>("Contacts fetched successfully", contacts));
    }

    @Operation(summary = "Get contact by ID")
    @GetMapping("/contacts/{contactId}")
    public ResponseEntity<ResponseDTO<ContactDTO>> getContactById(
            @PathVariable Long contactId) {

        ContactDTO contact = contactService.getContactById(contactId);
        return ResponseEntity.ok(new ResponseDTO<>("Contact found", contact));
    }

    /**
     * FIX: Added @Valid so that ContactDTO constraints (e.g. @Pattern for zip/phone,
     * @Email) are enforced on edit as well as on add.
     */
    @Operation(summary = "Edit a contact")
    @PutMapping("/{bookId}/contacts/{firstName}/{lastName}")
    public ResponseEntity<ResponseDTO<ContactDTO>> editContact(
            @PathVariable Long bookId,
            @PathVariable String firstName,
            @PathVariable String lastName,
            @Valid @RequestBody ContactDTO updatedDTO,
            @AuthenticationPrincipal UserDetails principal) {

        ContactDTO updated = contactService.editContact(
                bookId, firstName, lastName, updatedDTO, principal.getUsername());
        return ResponseEntity.ok(new ResponseDTO<>("Contact updated successfully", updated));
    }

    @Operation(summary = "Delete a contact")
    @DeleteMapping("/{bookId}/contacts/{firstName}/{lastName}")
    public ResponseEntity<ResponseDTO<String>> deleteContact(
            @PathVariable Long bookId,
            @PathVariable String firstName,
            @PathVariable String lastName,
            @AuthenticationPrincipal UserDetails principal) {

        contactService.deleteContact(bookId, firstName, lastName, principal.getUsername());
        return ResponseEntity.ok(new ResponseDTO<>("Contact deleted successfully", null));
    }

    @Operation(summary = "Sort contacts by name")
    @GetMapping("/{bookId}/contacts/sorted/name")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> getSortedByName(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails principal) {

        List<ContactDTO> sorted = contactService.getSortedByName(bookId, principal.getUsername());
        return ResponseEntity.ok(new ResponseDTO<>("Contacts sorted by name", sorted));
    }

    @Operation(summary = "Sort contacts by location")
    @GetMapping("/{bookId}/contacts/sorted/location")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> getSortedByLocation(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails principal) {

        List<ContactDTO> sorted = contactService.getSortedByCityStateZip(bookId, principal.getUsername());
        return ResponseEntity.ok(new ResponseDTO<>("Contacts sorted by city, state, zip", sorted));
    }

    @Operation(summary = "Search contacts by name")
    @GetMapping("/contacts/search")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> searchByName(
            @RequestParam String name) {

        List<ContactDTO> results = contactService.searchByName(name);
        return ResponseEntity.ok(new ResponseDTO<>("Search results for: " + name, results));
    }

    @Operation(summary = "Get contacts by city")
    @GetMapping("/contacts/city/{city}")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> getByCity(
            @PathVariable String city) {

        List<ContactDTO> contacts = contactService.searchByCity(city);
        return ResponseEntity.ok(new ResponseDTO<>("Contacts in city: " + city, contacts));
    }

    @Operation(summary = "Get contacts by state")
    @GetMapping("/contacts/state/{state}")
    public ResponseEntity<ResponseDTO<List<ContactDTO>>> getByState(
            @PathVariable String state) {

        List<ContactDTO> contacts = contactService.searchByState(state);
        return ResponseEntity.ok(new ResponseDTO<>("Contacts in state: " + state, contacts));
    }

    /**
     * FIX: Return 400 if neither city nor state is provided — previously returned
     * an empty map with 200 OK which is misleading and wastes a round-trip.
     */
    @Operation(summary = "Count contacts by city or state")
    @GetMapping("/contacts/count")
    public ResponseEntity<ResponseDTO<Map<String, Long>>> getCount(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state) {

        if (city == null && state == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least one of 'city' or 'state' query parameters is required.");
        }

        Map<String, Long> result = new HashMap<>();
        if (city  != null) result.put("countByCity_"  + city,  contactService.countByCity(city));
        if (state != null) result.put("countByState_" + state, contactService.countByState(state));

        return ResponseEntity.ok(new ResponseDTO<>("Count results", result));
    }
}
