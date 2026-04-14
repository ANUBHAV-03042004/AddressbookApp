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
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/addressbooks")
@Tag(name = "Address Book", description = "APIs for managing Address Books")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @Operation(summary = "Create a new Address Book", description = "Creates a new address book with the given name")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Address book created successfully"),
        @ApiResponse(responseCode = "409", description = "Address book with this name already exists")
    })
    @PostMapping
    public ResponseEntity<ResponseDTO<AddressBook>> createAddressBook(
            @Parameter(description = "Name of the address book", required = true, example = "Friends")
            @RequestParam String name) {
        AddressBook created = addressBookService.createAddressBook(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>("Address book created successfully", created));
    }

    @Operation(summary = "Get all Address Books", description = "Returns a list of all address books")
    @ApiResponse(responseCode = "200", description = "List of address books returned")
    @GetMapping
    public ResponseEntity<ResponseDTO<List<AddressBook>>> getAllAddressBooks() {
        List<AddressBook> books = addressBookService.getAllAddressBooks();
        return ResponseEntity.ok(new ResponseDTO<>("All address books fetched", books));
    }

    @Operation(summary = "Get Address Book by ID", description = "Returns a single address book by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Address book found"),
        @ApiResponse(responseCode = "404", description = "Address book not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AddressBook>> getAddressBookById(
            @Parameter(description = "ID of the address book", required = true, example = "1")
            @PathVariable Long id) {
        AddressBook book = addressBookService.getAddressBookById(id);
        return ResponseEntity.ok(new ResponseDTO<>("Address book found", book));
    }

    @Operation(summary = "Delete an Address Book", description = "Deletes the address book and all its contacts")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Address book deleted"),
        @ApiResponse(responseCode = "404", description = "Address book not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteAddressBook(
            @Parameter(description = "ID of the address book to delete", required = true, example = "1")
            @PathVariable Long id) {
        addressBookService.deleteAddressBook(id);
        return ResponseEntity.ok(new ResponseDTO<>("Address book deleted successfully", null));
    }
}
