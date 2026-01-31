package com.hcl.ewallet.user.service;

import com.hcl.ewallet.user.dto.request.LoginRequest;
import com.hcl.ewallet.user.dto.request.RegisterUser;
import com.hcl.ewallet.user.dto.request.UpdateUser;
import com.hcl.ewallet.user.dto.response.UserResponse;
import com.hcl.ewallet.user.dto.response.common.AuthResponse;

import java.util.List;

public interface UserService {

    public UserResponse createUser(RegisterUser user);

    public List<UserResponse> getAllUsers();

    public UserResponse getUserById(Long userId);

    public UserResponse updateUser(Long userId, UpdateUser updateUser);

    public void deleteUser(Long userId);

    public AuthResponse login(LoginRequest user);
}
