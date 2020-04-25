package com.itvsme.bank.registration;


import com.itvsme.bank.models.user.UserDTO;
import com.itvsme.bank.registration.exceptions.AccountEnablingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistrationController
{
    private RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService)
    {
        this.registrationService = registrationService;
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
    public String enableAccount(@RequestParam String value)
    {
        try
        {
            registrationService.activateUser(value);

            return "Account enabled. You can log in now";
        } catch (AccountEnablingException e)
        {
            return e.getMessage();
        }

    }
}
