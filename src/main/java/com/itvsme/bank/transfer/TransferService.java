package com.itvsme.bank.transfer;

import com.itvsme.bank.models.account.Account;
import com.itvsme.bank.repositories.AccountRepository;
import com.itvsme.bank.repositories.UserAppRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransferService
{
    private AccountRepository accountRepository;
    private UserAppRepository userAppRepository;

    public TransferService(AccountRepository accountRepository, UserAppRepository userAppRepository)
    {
        this.accountRepository = accountRepository;
        this.userAppRepository = userAppRepository;
    }

    public boolean makeTransfer(TransferDTO transferDTO)
    {
        Optional<Account> accountToOptional = accountRepository.findByNaturalId(transferDTO.getTo());
        Optional<Account> accountFromOptional = accountRepository.findByNaturalId(transferDTO.getFrom());

        if (accountFromOptional.isPresent() && accountToOptional.isPresent())
        {
            Account accountTo = accountToOptional.get();
            Account accountFrom = accountFromOptional.get();

            if (isTransferPossible(accountFrom, transferDTO.getAmountInHundredScale()))
            {
                accountFrom.setBalanceInHundredScale(accountFrom.getBalanceInHundredScale() - transferDTO.getAmountInHundredScale());
                accountTo.setBalanceInHundredScale(accountTo.getBalanceInHundredScale() + transferDTO.getAmountInHundredScale());

                accountRepository.save(accountFrom);
                accountRepository.save(accountTo);

                return true;
            }
        }

        return false;
    }

    private boolean isTransferPossible(Account accountFrom, long amountInHundredScale)
    {
        return accountFrom.getBalanceInHundredScale() > amountInHundredScale;
    }
}
