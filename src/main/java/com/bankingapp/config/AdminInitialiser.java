package com.bankingapp.config;

import com.bankingapp.entity.User;
import com.bankingapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitialiser {

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String adminUsername =
                    System.getenv("BANKING_ADMIN_USERNAME");

            String adminPassword =
                    System.getenv("BANKING_ADMIN_PASSWORD");

            if (adminUsername == null ||
                    adminPassword == null ||
                    adminUsername.isBlank() ||
                    adminPassword.isBlank()) {

                System.out.println(
                        "ADMIN initializer skipped: " +
                                "BANKING_ADMIN_USERNAME or " +
                                "BANKING_ADMIN_PASSWORD not configured."
                );

                return;
            }

            if (userRepository.existsByUsername(adminUsername)) {

                User existingUser =
                        userRepository
                                .findByUsername(adminUsername)
                                .orElseThrow();

                if (!"ADMIN".equals(existingUser.getRole())) {
                    existingUser.setRole("ADMIN");
                    userRepository.save(existingUser);
                }

                System.out.println(
                        "ADMIN user already exists: "
                                + adminUsername
                );

                return;
            }

            User admin = new User();

            admin.setUsername(adminUsername);
            admin.setPassword(
                    passwordEncoder.encode(adminPassword)
            );
            admin.setRole("ADMIN");
            admin.setName("System");
            admin.setSurname("Administrator");
            admin.setNationalId(
                    "ADMIN-" + adminUsername
            );
            admin.setCellNumber(
                    "ADMIN-" + adminUsername
            );

            userRepository.save(admin);

            System.out.println(
                    "ADMIN user created successfully: "
                            + adminUsername
            );
        };
    }
}