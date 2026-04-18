package com.addressbook.dto;

import jakarta.validation.constraints.*;

public class ContactDTO {

    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    /**
     * FIX: @Pattern does NOT allow null by default — it only validates non-null values
     * when combined with the constraint. But if the client sends an empty string ""
     * the pattern \d{6} fails. Added explicit @Size(min=0) hint is unnecessary;
     * the real fix is: these fields are optional so they MUST allow null.
     * @Pattern already allows null (skips the check), so the bug was that
     * the Angular form sent an empty string "" for blank fields — which fails
     * the pattern. Backend must treat "" the same as null.
     * Solution: strip empty strings to null in setters.
     */
    @Pattern(regexp = "^\\d{6}$", message = "ZIP code must be exactly 6 digits")
    private String zip;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    /**
     * FIX: @Email also allows null (skips check for null), but rejects empty string.
     * Same empty-string-to-null treatment applied in setter.
     */
    @Email(message = "Invalid email format")
    private String email;

    public ContactDTO() {}

    public ContactDTO(Long id, String firstName, String lastName, String address,
                      String city, String state, String zip, String phoneNumber, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        setZip(zip);
        setPhoneNumber(phoneNumber);
        setEmail(email);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZip() { return zip; }
    /** Coerce empty string → null so @Pattern validation is skipped for blank optional field. */
    public void setZip(String zip) {
        this.zip = (zip != null && zip.isBlank()) ? null : zip;
    }

    public String getPhoneNumber() { return phoneNumber; }
    /** Coerce empty string → null so @Pattern validation is skipped for blank optional field. */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = (phoneNumber != null && phoneNumber.isBlank()) ? null : phoneNumber;
    }

    public String getEmail() { return email; }
    /** Coerce empty string → null so @Email validation is skipped for blank optional field. */
    public void setEmail(String email) {
        this.email = (email != null && email.isBlank()) ? null : email;
    }

    @Override
    public String toString() {
        return "ContactDTO{id=" + id + ", firstName='" + firstName + "', lastName='" + lastName + "'}";
    }
}
