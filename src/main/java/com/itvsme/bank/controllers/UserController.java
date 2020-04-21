package com.itvsme.bank.controllers;

import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.repositories.UserAppRepository;
import com.itvsme.bank.services.UserDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Optional;

@RestController
public class UserController
{
    private UserDataService userDataService;

    public UserController(UserDataService userDataService)
    {
        this.userDataService = userDataService;
    }

    @GetMapping("/user-data")
    public ResponseEntity<UserApp> getUserData(Principal principal)
    {
        return ResponseEntity.ok(userDataService.getUserData(principal));
    }
}
