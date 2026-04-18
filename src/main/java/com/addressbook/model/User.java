package com.addressbook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email    = email;
        this.password = password;
    }

    public Long   getId()       { return id; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    public void setId(Long id)        { this.id = id; }
    public void setUsername(String u) { this.username = u; }
    public void setEmail(String e)    { this.email = e; }
    public void setPassword(String p) { this.password = p; }
}
