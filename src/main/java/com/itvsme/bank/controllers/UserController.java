package com.itvsme.bank.controllers;

import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.services.UserDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> getUserData(Principal principal)
    {
        Optional<UserApp> userData = userDataService.getUser(principal);

        if (userData.isPresent())
        {
            return ResponseEntity.ok(userData.get());
        }
        else
        {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
}
