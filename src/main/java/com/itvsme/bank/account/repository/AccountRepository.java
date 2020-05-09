package com.itvsme.bank.account.repository;

import com.itvsme.bank.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountNaturalIdRepository
{
}
