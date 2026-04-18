package com.addressbook.repository;

import com.addressbook.model.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {

    // FIX: All queries now scoped by owner so users only see their own books
    List<AddressBook> findByOwner(String owner);

    Optional<AddressBook> findByIdAndOwner(Long id, String owner);

    boolean existsByNameIgnoreCaseAndOwner(String name, String owner);

    boolean existsByIdAndOwner(Long id, String owner);
}
