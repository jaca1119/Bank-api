package com.itvsme.bank.repositories;

import com.itvsme.bank.account.Account;
import com.itvsme.bank.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

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

    @Test
    void testSavingAccountWithName()
    {
        Account account = new Account();
        account.setName("Account name");
        account.setAccountBusinessId("test");

        accountRepository.save(account);

        assertThat(account.getName()).isEqualTo("Account name");
    }

    @Test
    void testSavingAccountWithNameLengthEqualsMaxLength()
    {
        Account account = new Account();
        account.setName("A".repeat(64));
        account.setAccountBusinessId("test");

        System.out.println(account);
        accountRepository.save(account);

        assertThat(account.getName()).isEqualTo("A".repeat(64));
    }

    @Test()
    void testSavingAccountWithNameLengthAboveAboveMaxLength()
    {
        try
        {
            Account account = new Account();
            account.setName("A".repeat(65));
            account.setAccountBusinessId("test");

            System.out.println(account);
            accountRepository.save(account);

            assertThat(account.getName()).isEqualTo("A".repeat(65));
            fail();
        } catch (DataIntegrityViolationException e)
        {
            assertThat(e.getMessage()).startsWith("could not execute statement");
        }
    }
}