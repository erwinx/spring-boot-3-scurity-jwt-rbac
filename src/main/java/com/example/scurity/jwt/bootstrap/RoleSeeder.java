package com.example.scurity.jwt.bootstrap;

import com.example.scurity.jwt.model.Privilege;
import com.example.scurity.jwt.model.Role;
import com.example.scurity.jwt.repository.PrivilegeRepository;
import com.example.scurity.jwt.repository.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order(2)
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    public RoleSeeder(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    private void loadRoles() {
        List<Privilege> privileges = privilegeRepository.findAll();
        List<Role> roles = roleRepository.findAll();

        if (!privileges.isEmpty() && roles.isEmpty()) {
            Set<String> roleNames = new HashSet<String>() {
                {
                    add("ROLE_USER");
                    add("ROLE_ADMIN");
                    add("ROLE_SUPER_ADMIN");
                }
            };

            roleNames.forEach((name) -> {
                Role role = new Role();
                role.setName(name);
                switch (name) {
                    case "ROLE_ADMIN" -> {
                        role.setPrivileges(privilegeRepository.findAll()
                                .stream()
                                .filter(privilege -> privilege.getName().contains("user:"))
                                .collect(Collectors.toList())
                        );
                    }
                    case "ROLE_SUPER_ADMIN" -> {
                        role.setPrivileges(privilegeRepository.findAll()
                                .stream()
                                .filter(privilege -> privilege.getName().contains("privilege:")
                                        || privilege.getName().contains("role:")
                                )
                                .collect(Collectors.toList())
                        );
                    }
                }

                roleRepository.save(role);
            });
        }
    }
}
