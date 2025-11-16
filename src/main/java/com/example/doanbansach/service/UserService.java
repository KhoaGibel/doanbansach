package com.example.doanbansach.service;

import com.example.doanbansach.dto.RegistrationDTO;
import com.example.doanbansach.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void registerNewUser(RegistrationDTO registrationDto) throws Exception, PasswordMismatchException;

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User saveUser(User user);

    User updateUser(Long id, User userDetails);

    void deleteUser(Long id);
}