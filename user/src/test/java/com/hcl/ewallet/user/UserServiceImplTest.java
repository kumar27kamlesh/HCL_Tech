package com.hcl.ewallet.user;

import com.hcl.ewallet.user.config.JwtUtil;
import com.hcl.ewallet.user.dto.request.LoginRequest;
import com.hcl.ewallet.user.dto.request.RegisterUser;
import com.hcl.ewallet.user.dto.request.UpdateUser;
import com.hcl.ewallet.user.dto.response.UserResponse;
import com.hcl.ewallet.user.dto.response.common.AuthResponse;
import com.hcl.ewallet.user.enums.Role;
import com.hcl.ewallet.user.enums.UserStatus;
import com.hcl.ewallet.user.enums.UserType;
import com.hcl.ewallet.user.model.User;
import com.hcl.ewallet.user.repository.UserRepository;
import com.hcl.ewallet.user.service.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(30);
        user.setPassword("encodedPassword");
        user.setRole(Role.INDIVIDUAL);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserType(UserType.INDIVIDUAL);
    }

    // -------------------- getAllUsers --------------------

    @Test
    void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> responses = userService.getAllUsers();

        assertEquals(1, responses.size());
        assertEquals("test@example.com", responses.get(0).getEmail());
        verify(userRepository).findAll();
    }

    // -------------------- getUserById --------------------

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertEquals("Test User", response.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.getUserById(1L)
        );

        assertEquals("User not found", ex.getMessage());
    }

    // -------------------- updateUser --------------------

    @Test
    void updateUser_success() {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setName("Updated Name");
        updateUser.setEmail("updated@example.com");
        updateUser.setAge(35);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.updateUser(1L, updateUser);

        assertEquals("Updated Name", response.getName());
        assertEquals("updated@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    // -------------------- createUser --------------------

    @Test
    void createUser_success() {
        RegisterUser registerUser = new RegisterUser();
        registerUser.setName("New User");
        registerUser.setEmail("new@example.com");
        registerUser.setAge(25);
        registerUser.setUserType(UserType.INDIVIDUAL);

        when(userRepository.existsByEmail(registerUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.createUser(registerUser);

        assertEquals("new@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_emailAlreadyExists() {
        RegisterUser registerUser = new RegisterUser();
        registerUser.setEmail("test@example.com");

        when(userRepository.existsByEmail(registerUser.getEmail())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(registerUser)
        );

        assertEquals("Email already registered", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // -------------------- login --------------------

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("plainPassword");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
//                .thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail(), user.getRole()))
                .thenReturn("jwt-token");

        AuthResponse response = userService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(Role.INDIVIDUAL.toString(), response.getRole());
    }

    // -------------------- deleteUser --------------------

    @Test
    void deleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.deleteUser(1L);

        assertEquals(UserStatus.INACTIVE, user.getUserStatus());
        verify(userRepository).save(user);
    }
}
