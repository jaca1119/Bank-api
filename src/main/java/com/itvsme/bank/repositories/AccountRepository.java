package com.itvsme.bank.repositories;

import com.itvsme.bank.models.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>
{
}
