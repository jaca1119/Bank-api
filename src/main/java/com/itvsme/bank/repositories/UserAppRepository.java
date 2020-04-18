package com.itvsme.bank.repositories;


import com.itvsme.bank.models.user.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAppRepository extends JpaRepository<UserApp, Long>
{
    Optional<UserApp> findByUsername(String username);
}
