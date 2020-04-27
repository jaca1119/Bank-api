package com.itvsme.bank.repositories;

import com.itvsme.bank.models.account.Account;

import java.util.Optional;

//Naming is important for custom repository interfaces
public interface AccountNaturalIdRepository
{
    Optional<Account> findByNaturalId(String naturalId);
}
