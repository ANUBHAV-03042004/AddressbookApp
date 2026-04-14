package com.addressbook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Objects;

@Entity
@Table(name = "contacts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"first_name", "last_name", "address_book_id"}))
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @Pattern(regexp = "\\d{6}", message = "ZIP code must be exactly 6 digits")
    private String zip;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_book_id", nullable = false)
    private AddressBook addressBook;

    public Contact() {}

    // Getters
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZip() { return zip; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public AddressBook getAddressBook() { return addressBook; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZip(String zip) { this.zip = zip; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAddressBook(AddressBook addressBook) { this.addressBook = addressBook; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Contact contact = (Contact) obj;
        return firstName.equalsIgnoreCase(contact.firstName) &&
               lastName.equalsIgnoreCase(contact.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName.toLowerCase(), lastName.toLowerCase());
    }

    @Override
    public String toString() {
        return "Contact{id=" + id + ", firstName='" + firstName + "', lastName='" + lastName +
               "', city='" + city + "', state='" + state + "'}";
    }
}
