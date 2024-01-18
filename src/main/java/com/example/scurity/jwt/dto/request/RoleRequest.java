package com.example.scurity.jwt.dto.request;

import com.example.scurity.jwt.model.Role;

public record RoleRequest(String name) {
    public Role toRole() {
        Role Role = new Role();

        Role.setName(name);

        return Role;
    }
}
