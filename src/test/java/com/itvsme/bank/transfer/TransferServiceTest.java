package com.itvsme.bank.transfer;

import com.itvsme.bank.TestUtils;
import com.itvsme.bank.account.Account;
import com.itvsme.bank.account.repository.AccountRepository;
import com.itvsme.bank.repositories.UserAppRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
    TransferRepository transferRepository;
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

            assertThat(transferRepository.findAllByAccount(accountFromOptional.get(), PageRequest.of(0, 2))).contains(transferRepository.getOne(1L));
            assertThat(transferRepository.findAllByAccount(accountToOptional.get(), PageRequest.of(0, 2))).contains(transferRepository.getOne(2L));

            assertThat(accountRepository.count()).isEqualTo(2);
            assertThat(transferRepository.count()).isEqualTo(2);
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

    @Test
    void testFindingTransfersByAccount()
    {
        Account account = TestUtils.createAccount(10000, "EUR", "test");
        Account secondAccount = new Account();
        secondAccount.setAccountBusinessId("TestKey");
        accountRepository.saveAll(List.of(account, secondAccount));

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAccount(account);

        TransferDTO transferToSecondAccount = new TransferDTO();
        transferToSecondAccount.setAccount(secondAccount);

        transferRepository.saveAll(List.of(transferDTO, transferToSecondAccount));

        Page<TransferDTO> allByAccount = transferRepository.findAllByAccount(account, PageRequest.of(0, 2));

        assertThat(allByAccount).containsExactlyInAnyOrder(transferDTO);
    }
}
