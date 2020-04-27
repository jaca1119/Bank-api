package com.itvsme.bank.transfer;

import com.itvsme.bank.TestUtils;
import com.itvsme.bank.models.account.Account;
import com.itvsme.bank.repositories.AccountRepository;
import com.itvsme.bank.repositories.UserAppRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
public class TransferServiceTest
{
    @SpyBean
    TransferService transferService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserAppRepository userAppRepository;

    private String toKey = "ToKey";
    private String fromKey = "FromKey";
    private String currency = "EUR";
    private long balanceInHundredScale = 100000;

    @BeforeEach
    void setUp()
    {
        Account accountTo = TestUtils.createAccount(balanceInHundredScale, currency, toKey);
        Account accountFrom = TestUtils.createAccount(balanceInHundredScale, currency, fromKey);

        accountRepository.saveAll(List.of(accountTo, accountFrom));
    }

    @AfterEach
    void tearDown()
    {
        accountRepository.deleteAll();
    }

    @Test
    void testMakeTransferWithValidValues()
    {
        long transferValue = 100 * 100;
        TransferDTO transferDTO = TestUtils.createTransferDTO(toKey, fromKey, transferValue);

        boolean transferStatus = transferService.makeTransfer(transferDTO);

        Optional<Account> accountToOptional = accountRepository.findByNaturalId(toKey);
        Optional<Account> accountFromOptional = accountRepository.findByNaturalId(fromKey);

        assertTrue(transferStatus);

        if (accountFromOptional.isPresent() && accountToOptional.isPresent())
        {
            assertThat(accountToOptional.get().getBalanceInHundredScale()).isEqualTo(balanceInHundredScale + transferValue);
            assertThat(accountFromOptional.get().getBalanceInHundredScale()).isEqualTo(balanceInHundredScale - transferValue);

            assertThat(accountRepository.count()).isEqualTo(2);
        }
        else
        {
            fail();
        }
    }

    @Test
    void testMakeTransferWhenNotEnoughBalance()
    {
        long transferValue = 10000 * 100;

        TransferDTO transferDTO = TestUtils.createTransferDTO(toKey, fromKey, transferValue);

        boolean transferStatus = transferService.makeTransfer(transferDTO);

        Optional<Account> accountToOptional = accountRepository.findByNaturalId(toKey);
        Optional<Account> accountFromOptional = accountRepository.findByNaturalId(fromKey);

        assertFalse(transferStatus);

        if (accountFromOptional.isPresent() && accountToOptional.isPresent())
        {
            assertThat(accountToOptional.get().getBalanceInHundredScale()).isEqualTo(balanceInHundredScale);
            assertThat(accountFromOptional.get().getBalanceInHundredScale()).isEqualTo(balanceInHundredScale);

            assertThat(accountRepository.count()).isEqualTo(2);
        }
        else
        {
            fail();
        }
    }
}
