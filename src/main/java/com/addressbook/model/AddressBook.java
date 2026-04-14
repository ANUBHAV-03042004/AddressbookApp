package com.addressbook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "address_books",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class AddressBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Address book name is required")
    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "addressBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    public AddressBook() {}

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Contact> getContacts() { return contacts; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setContacts(List<Contact> contacts) { this.contacts = contacts; }

    @Override
    public String toString() {
        return "AddressBook{id=" + id + ", name='" + name + "'}";
    }
}
