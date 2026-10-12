package com.quickfix.backend.config;

import com.quickfix.backend.entity.ServiceCategory;
import com.quickfix.backend.entity.User;
import com.quickfix.backend.entity.UserRole;
import com.quickfix.backend.repository.ServiceCategoryRepository;
import com.quickfix.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabaseData(
            ServiceCategoryRepository categoryRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed standard service categories if missing
            seedCategoryIfMissing(categoryRepository, "Plumbing", "Pipes, taps and water leaks");
            seedCategoryIfMissing(categoryRepository, "Electrical", "Wiring, lights and electrical repairs");
            seedCategoryIfMissing(categoryRepository, "Carpentry", "Doors, furniture and wood repairs");
            seedCategoryIfMissing(categoryRepository, "Appliance Repair", "Repair household appliances");
            seedCategoryIfMissing(categoryRepository, "Cleaning", "Home and room cleaning");
            seedCategoryIfMissing(categoryRepository, "Painting", "Interior and exterior wall painting");
            seedCategoryIfMissing(categoryRepository, "Pest Control", "Termite and pest extermination");

            // Seed default admin account if missing
            if (!userRepository.existsByEmailIgnoreCase("admin@quickfix.com")) {
                User admin = new User();
                admin.setName("QuickFix Admin");
                admin.setEmail("admin@quickfix.com");
                admin.setPassword(passwordEncoder.encode("admin12345"));
                admin.setPhone("9999999999");
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
            }
        };
    }

    private void seedCategoryIfMissing(
            ServiceCategoryRepository repository,
            String name,
            String description) {
        if (!repository.existsByNameIgnoreCase(name)) {
            ServiceCategory category = new ServiceCategory();
            category.setName(name);
            category.setDescription(description);
            repository.save(category);
        }
    }
}
