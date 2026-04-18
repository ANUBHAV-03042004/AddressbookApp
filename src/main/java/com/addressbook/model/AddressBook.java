package com.addressbook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "address_books",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "owner"}))
public class AddressBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Address book name is required")
    @Column(nullable = false)
    private String name;

    /**
     * The username of the user who owns this address book.
     * Populated from the JWT principal on creation.
     * Unique constraint is (name + owner) — the same name is allowed
     * across different users but not within the same user.
     */
    @Column(nullable = false)
    private String owner;

    /**
     * Suppress the full contact list from GET /addressbooks responses.
     * Contacts are fetched via their own endpoint — returning them here
     * causes N+1 queries and bloated payloads.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "addressBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    public AddressBook() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getOwner() { return owner; }
    public List<Contact> getContacts() { return contacts; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setOwner(String owner) { this.owner = owner; }
    public void setContacts(List<Contact> contacts) { this.contacts = contacts; }

    @Override
    public String toString() {
        return "AddressBook{id=" + id + ", name='" + name + "', owner='" + owner + "'}";
    }
}
