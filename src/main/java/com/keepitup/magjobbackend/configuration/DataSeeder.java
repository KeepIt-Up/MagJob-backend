package com.keepitup.magjobbackend.configuration;

import com.keepitup.magjobbackend.organization.controller.impl.OrganizationDefaultController;
import com.keepitup.magjobbackend.organization.dto.PostOrganizationRequest;
import com.keepitup.magjobbackend.organization.repository.api.OrganizationRepository;
import com.keepitup.magjobbackend.user.entity.User;
import com.keepitup.magjobbackend.user.repository.api.UserRepository;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {
    private final KeycloakController keycloakController;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationDefaultController organizationDefaultController;

    public DataSeeder(
            KeycloakController keycloakController,
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            OrganizationDefaultController organizationDefaultController) {
        this.keycloakController = keycloakController;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.organizationDefaultController = organizationDefaultController;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, String> users = Map.of(
                "john.test@gmail.com", "John Test",
                "jane.test@gmail.com", "Jane Test",
                "tom.test@gmail.com", "Tom Shelby"
        );

        Map<String, String> organizations = Map.of(
                "John Organization", "john.test@gmail.com",
                "Jane Organization", "jane.test@gmail.com",
                "Tom Organization", "tom.test@gmail.com"
        );

        Map<String, User> createdUsers = seedUsers(users);
        seedOrganizations(organizations, createdUsers);
    }

    private Map<String, User> seedUsers(Map<String, String> users) {
        Map<String, User> createdUsers = new HashMap<>();
        List<User> usersToInsert = new ArrayList<>();

        for (var entry : users.entrySet()) {
            String email = entry.getKey();
            String fullName = entry.getValue();
            String[] nameParts = fullName.split(" ");
            String firstName = nameParts[0];
            String lastName = nameParts[1];

            userRepository.findByEmail(email).ifPresentOrElse(
                    user -> createdUsers.put(email, user),
                    () -> {
                        User newUser = new User();
                        newUser.setFirstname(firstName);
                        newUser.setLastname(lastName);
                        newUser.setEmail(email);
                        UserRepresentation userRepresentation = KeycloakController.mapUserToUserRepresentation(newUser);
                        String id = keycloakController.createUser(userRepresentation);
                        newUser.setId(UUID.fromString(id));
                        usersToInsert.add(newUser);
                        createdUsers.put(email, newUser);
                    }
            );
        }

        if (!usersToInsert.isEmpty()) {
            userRepository.saveAll(usersToInsert);
        }

        return createdUsers;
    }

    private void seedOrganizations(Map<String, String> organizations, Map<String, User> users) {
        for (var entry : organizations.entrySet()) {
            String orgName = entry.getKey();
            String userEmail = entry.getValue();

            organizationRepository.findByName(orgName).ifPresentOrElse(
                    org -> {},
                    () -> {
                        User user = users.get(userEmail);
                        PostOrganizationRequest postOrganizationRequest = new PostOrganizationRequest();
                        postOrganizationRequest.setName(orgName);
                        postOrganizationRequest.setUserId(user.getId());
                        organizationDefaultController.createOrganization(postOrganizationRequest);
                    }
            );
        }
    }
}