package com.addressbook.controller;

import com.addressbook.dto.ResponseDTO;
import com.addressbook.model.AddressBook;
import com.addressbook.service.AddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addressbooks")
@Tag(name = "Address Book", description = "APIs for managing Address Books")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @Operation(summary = "Create a new Address Book")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Address book created"),
        @ApiResponse(responseCode = "409", description = "Name already in use by this user")
    })
    @PostMapping
    public ResponseEntity<ResponseDTO<AddressBook>> createAddressBook(
            @Parameter(description = "Name of the address book", required = true, example = "Friends")
            @RequestParam String name,
            @AuthenticationPrincipal UserDetails principal) {

        AddressBook created = addressBookService.createAddressBook(name, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>("Address book created successfully", created));
    }

    @Operation(summary = "Get all Address Books")
    @GetMapping
    public ResponseEntity<ResponseDTO<List<AddressBook>>> getAllAddressBooks(
            @AuthenticationPrincipal UserDetails principal) {

        List<AddressBook> books = addressBookService.getAllAddressBooks(principal.getUsername());
        return ResponseEntity.ok(new ResponseDTO<>("All address books fetched", books));
    }

    @Operation(summary = "Get Address Book by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AddressBook>> getAddressBookById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {

        AddressBook book = addressBookService.getAddressBookById(id, principal.getUsername());
        return ResponseEntity.ok(new ResponseDTO<>("Address book found", book));
    }

    @Operation(summary = "Delete an Address Book")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteAddressBook(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {

        addressBookService.deleteAddressBook(id, principal.getUsername());
        return ResponseEntity.ok(new ResponseDTO<>("Address book deleted successfully", null));
    }
}
