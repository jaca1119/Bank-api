package com.itvsme.bank.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, Long>
{
    Optional<RegistrationToken> findByValue(String value);
}
