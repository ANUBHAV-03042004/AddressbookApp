package com.addressbook.repository;

import com.addressbook.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    // All contacts in a given address book
    List<Contact> findByAddressBookId(Long addressBookId);

    // Find contact by name within an address book
    Optional<Contact> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(
            String firstName, String lastName, Long addressBookId);

    // Search by city across all books
    List<Contact> findByCityIgnoreCase(String city);

    // Search by state across all books
    List<Contact> findByStateIgnoreCase(String state);

    // Count by city
    long countByCityIgnoreCase(String city);

    // Count by state
    long countByStateIgnoreCase(String state);

    // Find contacts in a city within a specific address book
    List<Contact> findByAddressBookIdAndCityIgnoreCase(Long addressBookId, String city);

    // Find contacts in a state within a specific address book
    List<Contact> findByAddressBookIdAndStateIgnoreCase(Long addressBookId, String state);

    // Sort alphabetically by firstName then lastName within an address book
    List<Contact> findByAddressBookIdOrderByFirstNameAscLastNameAsc(Long addressBookId);

    // Sort by city, state, zip within an address book
    List<Contact> findByAddressBookIdOrderByCityAscStateAscZipAsc(Long addressBookId);

    // Check for duplicate (same firstName + lastName) within address book
    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndAddressBookId(
            String firstName, String lastName, Long addressBookId);

    // Full-text search across all address books by name
    @Query("SELECT c FROM Contact c WHERE " +
           "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Contact> findByFullNameContaining(@Param("name") String name);
}
