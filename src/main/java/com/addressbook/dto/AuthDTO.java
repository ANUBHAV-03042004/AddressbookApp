package com.addressbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDTO {

    public static class RegisterRequest {

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be 3–50 characters")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        public RegisterRequest() {}
        public String getUsername() { return username; }
        public void setUsername(String u) { this.username = u; }
        public String getEmail() { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
    }

    public static class LoginRequest {

        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        public LoginRequest() {}
        public String getUsername() { return username; }
        public void setUsername(String u) { this.username = u; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
    }

    public static class AuthResponse {

        private String token;
        private String tokenType = "Bearer";
        private long   expiresInMs;
        private String username;

        public AuthResponse() {}

        public AuthResponse(String token, long expiresInMs, String username) {
            this.token       = token;
            this.expiresInMs = expiresInMs;
            this.username    = username;
        }

        public String getToken()         { return token; }
        public void setToken(String t)   { this.token = t; }
        public String getTokenType()     { return tokenType; }
        public void setTokenType(String tt) { this.tokenType = tt; }
        public long getExpiresInMs()     { return expiresInMs; }
        public void setExpiresInMs(long ms) { this.expiresInMs = ms; }
        public String getUsername()      { return username; }
        public void setUsername(String u){ this.username = u; }
    }
}
