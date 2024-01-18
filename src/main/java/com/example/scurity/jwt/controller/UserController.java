package com.example.scurity.jwt.controller;

import com.example.scurity.jwt.dto.request.ChangePasswordRequest;
import com.example.scurity.jwt.dto.request.UserRequest;
import com.example.scurity.jwt.dto.response.MessageResponse;
import com.example.scurity.jwt.model.User;
import com.example.scurity.jwt.repository.UserRepository;
import com.example.scurity.jwt.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserController(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    /**
     * Create a new User
     */
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<User> createUser(@RequestBody UserRequest request) {
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                encoder.encode(request.getPassword())
        );
        user.setRoles(request.getRoles());

        User userCreate = userRepository.save(user);
        return new ResponseEntity<>(userCreate, HttpStatus.CREATED);
    }

    /**
     * Retrieve all users
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:list')")
    public ResponseEntity<List<User>> allUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * Retrieve a User
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<User> oneUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update user information
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<?> updateUser(@RequestBody UserRequest request, @PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User userToUpdate = optionalUser.get();

        userToUpdate.setUsername(request.getUsername());
        userToUpdate.setEmail(request.getEmail());
        userToUpdate.setPassword(request.getPassword());
        userToUpdate.setRoles(request.getRoles());

        User userUpdated = userRepository.save(userToUpdate);

        return ResponseEntity.ok(userUpdated);
    }

    /**
     * Delete a User
     */
    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Change user password
     */
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal connectedUser) {
        UserDetailsImpl userDetails = (UserDetailsImpl) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());

        // check if the current password is correct
        if (!encoder.matches(request.getCurrentPassword(), userDetails.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        optionalUser.ifPresent(u -> {
            u.setPassword(encoder.encode(request.getNewPassword()));
            userRepository.save(u);
        });

        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }
}
