
package com.quickfix.backend.controller;

import com.quickfix.backend.entity.ServiceCategory;
import com.quickfix.backend.repository.ServiceCategoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ServiceCategoryController {

    private final ServiceCategoryRepository repository;

    public ServiceCategoryController(
            ServiceCategoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ServiceCategory> getAllCategories() {
        return repository.findAll();
    }

    @PostMapping("/seed")
    public List<ServiceCategory> seedCategories() {
        seedCategoryIfMissing("Plumbing", "Pipes, taps and water leaks");
        seedCategoryIfMissing("Electrical", "Wiring, lights and electrical repairs");
        seedCategoryIfMissing("Carpentry", "Doors, furniture and wood repairs");
        seedCategoryIfMissing("Appliance Repair", "Repair household appliances");
        seedCategoryIfMissing("Cleaning", "Home and room cleaning");
        seedCategoryIfMissing("Painting", "Interior and exterior wall painting");
        seedCategoryIfMissing("Pest Control", "Termite and pest extermination");
        return repository.findAll();
    }

    private void seedCategoryIfMissing(String name, String description) {
        if (!repository.existsByNameIgnoreCase(name)) {
            ServiceCategory category = new ServiceCategory();
            category.setName(name);
            category.setDescription(description);
            repository.save(category);
        }
    }
}
