package com.itvsme.bank.transfer;

import com.itvsme.bank.account.Account;
import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.account.repository.AccountRepository;
import com.itvsme.bank.repositories.UserAppRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class TransferService
{
    private AccountRepository accountRepository;
    private UserAppRepository userAppRepository;
    private TransferRepository transferRepository;

    public TransferService(AccountRepository accountRepository, UserAppRepository userAppRepository, TransferRepository transferRepository)
    {
        this.accountRepository = accountRepository;
        this.userAppRepository = userAppRepository;
        this.transferRepository = transferRepository;
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

                saveTransferToAccounts(transferDTO, accountFrom, accountTo);

                return true;
            }
        }

        return false;
    }

    public Page<TransferDTO> getAccountTransfersPage(Integer accountId, Principal principal)
    {
        Optional<UserApp> userById = userAppRepository.findByUsername(principal.getName());

        Account searchedAccount = null;
        if (userById.isPresent())
        {
            for (Account account : userById.get().getAccounts())
            {
                if (account.getId().equals(Long.valueOf(accountId)))
                {
                    searchedAccount = account;
                    break;
                }
            }
        }

        return transferRepository.findAllByAccount(searchedAccount, PageRequest.of(0, 5));
    }

    private boolean isTransferPossible(Account accountFrom, long amountInHundredScale)
    {
        return accountFrom.getBalanceInHundredScale() > amountInHundredScale;
    }

    private void saveTransferToAccounts(TransferDTO transferFrom, Account from, Account to)
    {
        transferFrom.setAccount(from);

        transferRepository.save(transferFrom);

        TransferDTO transferTo = copyTransfer(transferFrom);
        transferTo.setAccount(to);

        transferRepository.save(transferTo);
    }

    private TransferDTO copyTransfer(TransferDTO transferDTO)
    {
        TransferDTO swap = new TransferDTO();
        swap.setTo(transferDTO.getTo());
        swap.setFrom(transferDTO.getFrom());
        swap.setAmountInHundredScale(transferDTO.getAmountInHundredScale());
        swap.setMessage(transferDTO.getMessage());
        swap.setTransferDateTime(transferDTO.getTransferDateTime());
        swap.setZone(transferDTO.getZone());
        swap.setTransferType(transferDTO.getTransferType());

        return swap;
    }

}
