package com.itvsme.bank.account;

import com.itvsme.bank.TestUtils;
import com.itvsme.bank.account.repository.AccountRepository;
import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.registration.utils.BusinessIdCreator;
import com.itvsme.bank.repositories.UserAppRepository;
import com.itvsme.bank.services.UserDataService;
import com.sun.security.auth.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
class AccountServiceTest
{
    @SpyBean
    AccountService accountService;
    @MockBean
    UserDataService userDataService;
    @SpyBean
    BusinessIdCreator businessIdCreator;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserAppRepository userAppRepository;

    @AfterEach
    void tearDown()
    {
        accountRepository.deleteAll();
        userAppRepository.deleteAll();
    }

    @Test
    void testCreatingAccount() throws UserPrincipalNotFoundException, CreatingAccountException
    {
        userAppRepository.save(TestUtils.createUserApp(1));

        AccountCreateRequestDTO accountCreateRequestDTO = new AccountCreateRequestDTO();
        accountCreateRequestDTO.setAccountName("Test name");
        accountCreateRequestDTO.setCurrency("EUR");

        when(userDataService.getUser(any())).thenReturn(userAppRepository.findByUsername("Test"));

        accountService.createAccount(accountCreateRequestDTO, new UserPrincipal("user"));

        Optional<UserApp> userApp = userAppRepository.findByUsername("Test");

        if (userApp.isPresent())
        {
            assertThat(userApp.get().getAccounts().size()).isEqualTo(2);
            assertThat(userApp.get().getAccounts().get(1).getName()).isEqualTo("Test name");
            assertThat(userApp.get().getAccounts().get(1).getBalanceInHundredScale()).isEqualTo(1_000_000);
        }
        else
        {
            fail();
        }
    }

    @Test
    void testCreatingAccountWithMaximumAccountLimit() throws UserPrincipalNotFoundException
    {
        userAppRepository.save(TestUtils.createUserApp(3));

        AccountCreateRequestDTO accountCreateRequestDTO = new AccountCreateRequestDTO();
        accountCreateRequestDTO.setAccountName("Test name");
        accountCreateRequestDTO.setCurrency("EUR");

        when(userDataService.getUser(any())).thenReturn(Optional.of(userAppRepository.getOne(1L)));

        try
        {
            accountService.createAccount(accountCreateRequestDTO, new UserPrincipal("user"));
            fail();
        } catch (CreatingAccountException e)
        {
            assertThat(e.getMessage()).isEqualTo("Accounts limit reached");
        }
    }

}