package com.itvsme.bank.services;

import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.repositories.UserAppRepository;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.AccessControlException;
import java.security.Principal;
import java.util.Optional;

@Service
public class UserDataService
{
    private UserAppRepository userAppRepository;

    public UserDataService(UserAppRepository userAppRepository)
    {
        this.userAppRepository = userAppRepository;
    }

    public UserApp getUserData(Principal principal)
    {
        UserApp currentUser = (UserApp) principal;

        return userAppRepository.getOne(currentUser.getId());
    }
}
