package com.itvsme.bank.account;

import com.itvsme.bank.account.repository.AccountRepository;
import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.registration.utils.BusinessIdCreator;
import com.itvsme.bank.repositories.UserAppRepository;
import com.itvsme.bank.services.UserDataService;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService
{
    private final long INITIAL_ACCOUNT_BALANCE = 1_000_000;
    private final int LIMIT_ACCOUNTS_SIZE = 3;

    private UserAppRepository userAppRepository;
    private UserDataService userDataService;
    private AccountRepository accountRepository;
    private BusinessIdCreator businessIdCreator;

    public AccountService(UserAppRepository userAppRepository, UserDataService userDataService, AccountRepository accountRepository, BusinessIdCreator businessIdCreator)
    {
        this.userAppRepository = userAppRepository;
        this.userDataService = userDataService;
        this.accountRepository = accountRepository;
        this.businessIdCreator = businessIdCreator;
    }

    public void createAccount(AccountCreateRequestDTO accountCreateRequestDTO, Principal user) throws UserPrincipalNotFoundException, CreatingAccountException
    {
        Optional<UserApp> userAppOptional = userDataService.getUser(user);

        if (userAppOptional.isEmpty())
        {
            throw new UserPrincipalNotFoundException(user.getName());
        }

        UserApp userApp = userAppOptional.get();
        List<Account> accounts = userApp.getAccounts();

        if (isPossibleToAddAccount(accounts))
        {
            Account accountFromDTO = createAccountFromDTO(accountCreateRequestDTO);
            accountRepository.save(accountFromDTO);

            ArrayList<Account> newAccounts = new ArrayList<>(accounts);
            newAccounts.add(accountFromDTO);

            userApp.setAccounts(newAccounts);
            userAppRepository.save(userApp);
        }
        else
        {
            throw new CreatingAccountException("Accounts limit reached");
        }
    }

    private boolean isPossibleToAddAccount(List<Account> accounts)
    {
        return accounts.size() < LIMIT_ACCOUNTS_SIZE;
    }

    private Account createAccountFromDTO(AccountCreateRequestDTO accountCreateRequestDTO)
    {
        Account account = new Account();
        account.setName(accountCreateRequestDTO.getAccountName());
        account.setCurrency(accountCreateRequestDTO.getCurrency());
        account.setAccountBusinessId(businessIdCreator.createBusinessId());
        account.setBalanceInHundredScale(INITIAL_ACCOUNT_BALANCE);

        return account;
    }
}
