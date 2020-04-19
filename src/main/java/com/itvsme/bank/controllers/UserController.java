package com.itvsme.bank.controllers;

import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.repositories.UserAppRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController
{
    private UserAppRepository userAppRepository;

    public UserController(UserAppRepository userAppRepository)
    {
        this.userAppRepository = userAppRepository;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserApp> getUser(@PathVariable Long id)
    {
        System.out.println(id);

        Optional<UserApp> optionalUserApp = userAppRepository.findById(id);
        System.out.println(optionalUserApp.get());
        return optionalUserApp.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
