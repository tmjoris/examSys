package com.examSys.examApp.controllers;

import com.examSys.examApp.models.User;
import com.examSys.examApp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/createUser")
    public Map<String, Object> createUser(@RequestBody Map<String, String> userDetails) {
        String email = userDetails.get("email");
        String password = userDetails.get("password");
        String name = userDetails.get("name");
        String role = userDetails.get("role");

        if (email == null || password == null || name == null || role == null) {
            return Map.of("success", false, "message", "All fields are required.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return Map.of("success", false, "message", "User with this email already exists.");
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setName(name);
        newUser.setRole(role);

        userRepository.save(newUser);

        return Map.of("success", true, "message", "User created successfully.");
    }
}
