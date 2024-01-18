package com.example.scurity.jwt.dto.request;

import com.example.scurity.jwt.model.Privilege;

public record PrivilegeRequest(String name) {
    public Privilege toPrivilege() {
        Privilege privilege = new Privilege();

        privilege.setName(name);

        return privilege;
    }
}
