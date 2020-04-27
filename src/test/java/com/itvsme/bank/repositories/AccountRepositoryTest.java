package com.itvsme.bank.repositories;

import com.itvsme.bank.models.account.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepositoryTest
{
    @Autowired
    AccountRepository accountRepository;

    @Test
    void findByNaturalId()
    {
        Account account = new Account();
        account.setBalanceInHundredScale(100001);
        account.setCurrency("EUR");;
        account.setAccountBusinessId("123t");

        accountRepository.save(account);

        Optional<Account> byNaturalId = accountRepository.findByNaturalId(account.getAccountBusinessId());

        if (byNaturalId.isPresent())
        {
            assertThat(byNaturalId.get()).isEqualTo(account);
        }
        else
        {
            fail("Account is not present.");
        }
    }
}