package com.itvsme.bank.utils;

import com.itvsme.bank.models.account.Account;
import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.repositories.AccountRepository;
import com.itvsme.bank.repositories.UserAppRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Component
public class Startup
{
    public Startup(UserAppRepository userAppRepository, PasswordEncoder passwordEncoder, AccountRepository accountRepository)
    {
        UserApp userApp = new UserApp();
        userApp.setUsername("User");
        userApp.setPassword(passwordEncoder.encode("user"));
        userApp.setEnabled(true);
        userApp.setRole("ADMIN");


        Account account = new Account();
//        BigDecimal.valueOf(1000.01234);
        account.setBalance(new BigDecimal("1000.01234"));
        account.setCurrency("EUR");;
        account.setAccountBusinessId("123t");

        accountRepository.save(account);

        Account secondAccount = new Account();
        secondAccount.setBalance(new BigDecimal("1000.1234"));
        secondAccount.setCurrency("EUR");
        secondAccount.setAccountBusinessId("321t");

        accountRepository.save(secondAccount);

        userApp.setAccounts(List.of(account, secondAccount));

        userAppRepository.save(userApp);


        UserApp user = new UserApp();
        user.setUsername("us");
        user.setPassword(passwordEncoder.encode("us"));
        user.setEnabled(false);
        user.setRole("USER");

        userAppRepository.save(user);
    }
}
