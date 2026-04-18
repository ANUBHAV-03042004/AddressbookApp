package com.addressbook.controller;

import com.addressbook.dto.AuthDTO;
import com.addressbook.dto.ResponseDTO;
import com.addressbook.model.User;
import com.addressbook.repository.UserRepository;
import com.addressbook.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Register a new user",
               description = "Creates a new user account. Returns a JWT on success.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "409", description = "Username or email already taken")
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<AuthDTO.AuthResponse>> register(
            @Valid @RequestBody AuthDTO.RegisterRequest req) {

        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>("Username '" + req.getUsername() + "' is already taken.", null));
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>("Email '" + req.getEmail() + "' is already registered.", null));
        }

        User user = new User(
                req.getUsername(),
                req.getEmail(),
                passwordEncoder.encode(req.getPassword())
        );
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>("User registered successfully",
                        new AuthDTO.AuthResponse(token, jwtUtil.getExpirationMs(), user.getUsername())));
    }

    @Operation(summary = "Login",
               description = "Authenticates the user and returns a JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<AuthDTO.AuthResponse>> login(
            @Valid @RequestBody AuthDTO.LoginRequest req) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getUsername(), req.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(req.getUsername());
            return ResponseEntity.ok(new ResponseDTO<>("Login successful",
                    new AuthDTO.AuthResponse(token, jwtUtil.getExpirationMs(), req.getUsername())));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>("Invalid username or password", null));
        }
    }
}
