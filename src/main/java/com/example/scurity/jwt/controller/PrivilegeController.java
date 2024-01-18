package com.example.scurity.jwt.controller;

import com.example.scurity.jwt.dto.request.PrivilegeRequest;
import com.example.scurity.jwt.model.Privilege;
import com.example.scurity.jwt.repository.PrivilegeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Manage privileges
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/privileges")
public class PrivilegeController {
    private final PrivilegeRepository privilegeRepository;

    public PrivilegeController(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('privilege:create')")
    public ResponseEntity<Privilege> createTask(@RequestBody PrivilegeRequest privilegeRequest) {
        Privilege privilegeCreated = privilegeRepository.save(privilegeRequest.toPrivilege());

        return new ResponseEntity<>(privilegeCreated, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('privilege:list')")
    public ResponseEntity<List<Privilege>> allPrivileges() {
        List<Privilege> privileges = privilegeRepository.findAll();

        return new ResponseEntity<>(privileges, HttpStatus.OK);
    }

    /**
     * Retrieve a Privilege
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('privilege:read')")
    public ResponseEntity<Privilege> oneTask(@PathVariable("id") long id) {
        Optional<Privilege> optionalPrivilege = privilegeRepository.findById(id);

        return optionalPrivilege.map(privilege -> new ResponseEntity<>(privilege, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)
                );
    }

    /**
     * Update privilege information
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('privilege:update')")
    public ResponseEntity<Privilege> updateTask(@PathVariable("id") long id, @RequestBody PrivilegeRequest privilegeRequest) {
        Optional<Privilege> optionalTask = privilegeRepository.findById(id);

        if (optionalTask.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Privilege privilegeToUpdate = optionalTask.get();
        privilegeToUpdate.setName(privilegeRequest.name());

        Privilege taskUpdated = privilegeRepository.save(privilegeToUpdate);
        return new ResponseEntity<>(taskUpdated, HttpStatus.OK);
    }

    /**
     * Delete a task
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('privilege:delete')")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") long id) {
        privilegeRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
