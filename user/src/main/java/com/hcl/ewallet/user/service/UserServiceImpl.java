package com.hcl.ewallet.user.service;

import com.hcl.ewallet.user.config.JwtUtil;
import com.hcl.ewallet.user.dto.request.LoginRequest;
import com.hcl.ewallet.user.dto.request.RegisterUser;
import com.hcl.ewallet.user.dto.request.UpdateUser;
import com.hcl.ewallet.user.dto.response.UserResponse;
import com.hcl.ewallet.user.dto.response.common.AuthResponse;
import com.hcl.ewallet.user.enums.UserStatus;
import com.hcl.ewallet.user.model.User;
import com.hcl.ewallet.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public List<UserResponse> getAllUsers() {
        List<User> users=userRepository.findAll();
        List<UserResponse> userResponses=users.stream()
                .map(user -> {
                    UserResponse response = new UserResponse();
                    response.setId(user.getId());
                    response.setName(user.getName());
                    response.setEmail(user.getEmail());
                    response.setCountry(user.getCountry());
                    response.setCurrency(user.getCurrency());
                    response.setUserStatus(user.getUserStatus());
                    response.setAge(user.getAge());
                    return response;
                })
                .toList();
        log.info("UserServiceImpl: All users are: {}",userResponses);
        return userResponses;
    }

    public UserResponse getUserById(Long id) {
        User user=userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse userResponse=new UserResponse();
        BeanUtils.copyProperties(user,userResponse);
        return userResponse;
    }

    public UserResponse updateUser(Long id, UpdateUser user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());
        User saved=userRepository.save(existingUser);
        UserResponse userResponse=new UserResponse();
        BeanUtils.copyProperties(saved,userResponse);
        return userResponse;
    }

    @Override
    public UserResponse createUser(RegisterUser registerUser) {

        if (userRepository.existsByEmail(registerUser.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        log.info("UserServiceImpl: All users are: {}",registerUser);
        User user=new User();
        BeanUtils.copyProperties(registerUser,user);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setRole(registerUser.getRole());
        user.setUserType(registerUser.getUserType());
        User savedUser=userRepository.save(user);

        UserResponse response=new UserResponse();
        BeanUtils.copyProperties(savedUser,response);
        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }

        String token = jwtUtil.generateToken(user.getEmail(),user.getRole());

        return new AuthResponse(token, user.getEmail(), user.getRole().toString());
    }
    @Override
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setUserStatus(UserStatus.INACTIVE);
        userRepository.save(existingUser);
    }
}
