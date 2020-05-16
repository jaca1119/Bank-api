package com.itvsme.bank.utils;

import com.itvsme.bank.account.Account;
import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.account.repository.AccountRepository;
import com.itvsme.bank.registration.utils.BusinessIdCreator;
import com.itvsme.bank.repositories.UserAppRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Startup implements CommandLineRunner
{
    private final UserAppRepository userAppRepository;
    private final PasswordEncoder passwordEncoder;

    public Startup(UserAppRepository userAppRepository, PasswordEncoder passwordEncoder)
    {
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception
    {
        UserApp userApp = new UserApp();
        userApp.setUsername("User");
        userApp.setPassword(passwordEncoder.encode("user"));
        userApp.setEnabled(true);
        userApp.setRole("ADMIN");


        Account account = new Account();
        account.setName("First account");
        account.setBalanceInHundredScale(100001);
        account.setCurrency("EUR");;
        account.setAccountBusinessId("123t");

        Account secondAccount = new Account();
        secondAccount.setName("Second Account");
        secondAccount.setBalanceInHundredScale(100012);
        secondAccount.setCurrency("EUR");
        secondAccount.setAccountBusinessId("321t");

        userApp.setAccounts(List.of(account, secondAccount));

        userAppRepository.save(userApp);

        UserApp pizzeria = new UserApp();
        pizzeria.setUsername("pizzeria");
        pizzeria.setPassword(passwordEncoder.encode("pizzeria"));
        pizzeria.setEnabled(true);
        pizzeria.setRole("USER");

        Account pizzeriaAccount = new Account();
        pizzeriaAccount.setName("Pizzeria");
        pizzeriaAccount.setBalanceInHundredScale(0);
        pizzeriaAccount.setCurrency("EUR");
        pizzeriaAccount.setAccountBusinessId(BusinessIdCreator.createBusinessId());

        pizzeria.setAccounts(List.of(pizzeriaAccount));

        userAppRepository.save(pizzeria);
    }
}
