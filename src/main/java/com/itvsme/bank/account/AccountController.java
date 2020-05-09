package com.itvsme.bank.account;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;

@RestController
public class AccountController
{
    private AccountService accountService;

    public AccountController(AccountService accountService)
    {
        this.accountService = accountService;
    }

    @PostMapping("/account/create")
    public ResponseEntity<String> createAccount(@RequestBody AccountCreateRequestDTO accountCreateRequestDTO, Principal user)
    {
        try
        {
            this.accountService.createAccount(accountCreateRequestDTO, user);
            return ResponseEntity.ok("Account created");
        } catch (UserPrincipalNotFoundException | CreatingAccountException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
