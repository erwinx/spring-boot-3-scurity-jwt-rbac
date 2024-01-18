package com.example.scurity.jwt.controller;

import com.example.scurity.jwt.dto.request.RoleRequest;
import com.example.scurity.jwt.model.Role;
import com.example.scurity.jwt.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Manage roles
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleRepository roleRepository;

    public RoleController(RoleRepository RoleRepository) {
        this.roleRepository = RoleRepository;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<Role> createTask(@RequestBody RoleRequest roleRequest) {
        Role RoleCreated = roleRepository.save(roleRequest.toRole());

        return new ResponseEntity<>(RoleCreated, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role:list')")
    public ResponseEntity<List<Role>> allRoles() {
        List<Role> roles = roleRepository.findAll();

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    /**
     * Retrieve a Role
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<Role> oneTask(@PathVariable("id") long id) {
        Optional<Role> optionalRole = roleRepository.findById(id);

        return optionalRole.map(Role -> new ResponseEntity<>(Role, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)
                );
    }

    /**
     * Update Role information
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<Role> updateRole(@PathVariable("id") long id, @RequestBody RoleRequest roleRequest) {
        Optional<Role> optionaRole = roleRepository.findById(id);

        if (optionaRole.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Role roleToUpdate = optionaRole.get();
        roleToUpdate.setName(roleRequest.name());

        Role roleUpdated = roleRepository.save(roleToUpdate);
        return new ResponseEntity<>(roleUpdated, HttpStatus.OK);
    }

    /**
     * Delete a task
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") long id) {
        roleRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
