package com.itvsme.bank.account.repository;

import com.itvsme.bank.account.Account;

import java.util.Optional;

//Naming is important for custom repository interfaces
public interface AccountNaturalIdRepository
{
    Optional<Account> findByNaturalId(String naturalId);
}
