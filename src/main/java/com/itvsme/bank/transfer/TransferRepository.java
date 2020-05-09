package com.itvsme.bank.transfer;

import com.itvsme.bank.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<TransferDTO, Long>
{
    Page<TransferDTO> findAllByAccount(Account account, Pageable pageable);
}
