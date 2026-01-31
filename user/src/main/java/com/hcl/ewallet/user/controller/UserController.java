package com.hcl.ewallet.user.controller;

import com.hcl.ewallet.user.dto.request.LoginRequest;
import com.hcl.ewallet.user.dto.request.RegisterUser;
import com.hcl.ewallet.user.dto.request.UpdateUser;
import com.hcl.ewallet.user.dto.response.UserResponse;
import com.hcl.ewallet.user.dto.response.common.AuthResponse;
import com.hcl.ewallet.user.model.User;
import com.hcl.ewallet.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "User API", description = "CRUD operations for Users")
public class UserController {

    @Autowired
    private  UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Create a new user")
    public UserResponse register(@Valid @RequestBody RegisterUser request) {
        log.info("AuthController: Register User with details: {}",request);
        return userService.createUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("AuthController: Login User with details: {}",request);
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    @Operation(summary = "Get user by ID")
    public UserResponse getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public UserResponse updateUser(@PathVariable long id, @RequestBody UpdateUser user) {
        return userService.updateUser(id, user);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public String deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }
}

