package com.itvsme.bank.utils;

import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.repositories.UserAppRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Startup
{
    public Startup(UserAppRepository userAppRepository, PasswordEncoder passwordEncoder)
    {
        UserApp userApp = new UserApp();
        userApp.setUsername("User");
        userApp.setPassword(passwordEncoder.encode("user"));
        userApp.setEnabled(true);
        userApp.setRole("ADMIN");

        UserApp user = new UserApp();
        user.setUsername("us");
        user.setPassword(passwordEncoder.encode("us"));
        user.setEnabled(false);
        user.setRole("USER");

        userAppRepository.save(userApp);
        userAppRepository.save(user);
    }
}
