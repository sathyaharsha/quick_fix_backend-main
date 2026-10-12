

package com.quickfix.backend.service;

import com.quickfix.backend.dto.AuthResponse;
import com.quickfix.backend.dto.LoginRequest;
import com.quickfix.backend.dto.RegisterRequest;
import com.quickfix.backend.entity.*;
import com.quickfix.backend.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final WorkerSkillRepository workerSkillRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            WorkerRepository workerRepository,
            ServiceCategoryRepository categoryRepository,
            WorkerSkillRepository workerSkillRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.workerRepository = workerRepository;
        this.categoryRepository = categoryRepository;
        this.workerSkillRepository = workerSkillRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register customer or worker
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Public registration cannot create admin accounts
        if (request.role() != UserRole.CUSTOMER
                && request.role() != UserRole.WORKER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only customer and worker registration is allowed"
            );
        }

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Check duplicate email
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        // BCrypt has a 72-byte input limit
        if (request.password()
                .getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must not exceed 72 UTF-8 bytes"
            );
        }

        // Validate worker categories
        if (request.role() == UserRole.WORKER) {

            if (request.categoryIds() == null
                    || request.categoryIds().isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Select at least one service category"
                );
            }

            if (request.categoryIds().stream().distinct().count()
                    != request.categoryIds().size()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate service categories are not allowed"
                );
            }

            for (Long categoryId : request.categoryIds()) {
                if (!categoryRepository.existsById(categoryId)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid service category ID: " + categoryId
                    );
                }
            }
        }

        // Create user account
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);

        user.setPhone(
                request.phone() == null || request.phone().isBlank()
                        ? null
                        : request.phone().trim()
        );

        // Never store a plain-text password
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setRole(request.role());

        user = userRepository.saveAndFlush(user);

        Long workerId = null;

        // Create worker profile and save selected skills
        if (request.role() == UserRole.WORKER) {

            double radius = request.serviceRadiusKm() == null
                    ? 5.0
                    : request.serviceRadiusKm();

            Worker worker = new Worker();
            worker.setUser(user);
            worker.setServiceRadiusKm(radius);

            worker = workerRepository.saveAndFlush(worker);
            workerId = worker.getId();

            for (Long categoryId : request.categoryIds()) {

                ServiceCategory category =
                        categoryRepository.findById(categoryId)
                                .orElseThrow(() ->
                                        new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Invalid service category"
                                        )
                                );

                WorkerSkill skill =
                        new WorkerSkill(worker, category);

                workerSkillRepository.save(skill);
            }
        }

        // Return safe profile data only
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                workerId
        );
    }

    // Login customer or worker
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid email or password"
                        )
                );

        // Compare the submitted password with its BCrypt hash
        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        // Efficiently find worker profile by user ID
        Long workerId = workerRepository
                .findByUserId(user.getId())
                .map(Worker::getId)
                .orElse(null);

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                workerId
        );
    }
}
