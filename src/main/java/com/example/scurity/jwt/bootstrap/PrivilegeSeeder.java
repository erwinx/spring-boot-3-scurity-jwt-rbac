package com.example.scurity.jwt.bootstrap;

import com.example.scurity.jwt.model.Privilege;
import com.example.scurity.jwt.repository.PrivilegeRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Component
@Order(1)
public class PrivilegeSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final PrivilegeRepository privilegeRepository;

    public PrivilegeSeeder(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadPrivileges();
    }

    private void loadPrivileges() {
        List<Privilege> privileges = privilegeRepository.findAll();
        if (privileges.isEmpty()) {
            Collection<String> privilegeNames = new HashSet<>() {
                {
                    add("user:list");
                    add("user:read");
                    add("user:create");
                    add("user:update");
                    add("user:delete");

                    add("privilege:list");
                    add("privilege:read");
                    add("privilege:create");
                    add("privilege:update");
                    add("privilege:delete");

                    add("role:list");
                    add("role:read");
                    add("role:create");
                    add("role:update");
                    add("role:delete");
                }
            };

            privilegeNames.forEach((name) -> {
                Privilege privilege = new Privilege();
                privilege.setName(name);
                privilegeRepository.save(privilege);
            });
        }
    }
}
