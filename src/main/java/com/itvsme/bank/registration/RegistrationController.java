package com.itvsme.bank.registration;


import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.models.user.UserDTO;
import com.itvsme.bank.repositories.UserAppRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RegistrationController
{
    private RegistrationService registrationService;
    private RegistrationTokenRepository tokenRepository;
    private UserAppRepository userAppRepository;

    public RegistrationController(RegistrationService registrationService, RegistrationTokenRepository tokenRepository, UserAppRepository userAppRepository)
    {
        this.registrationService = registrationService;
        this.tokenRepository = tokenRepository;
        this.userAppRepository = userAppRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO)
    {
        try
        {
            registrationService.register(userDTO);
            return ResponseEntity.ok("Registered");

        } catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/token")
    public String signUp(@RequestParam String value)
    {
        Optional<RegistrationToken> registrationToken = tokenRepository.findByValue(value);

        if (registrationToken.isPresent())
        {
            UserApp userApp = registrationToken.get().getUserApp();
            userApp.setEnabled(true);
            userAppRepository.save(userApp);

            return "Account enabled!";
        }
        else
        {
            return "Something went wrong with account enabling.";
        }
    }
}
