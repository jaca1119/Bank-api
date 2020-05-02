package com.itvsme.bank.transfer;

import com.itvsme.bank.models.account.Account;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Transfer")
public class TransferDTO
{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "from_account")
    private String from;
    @Column(name = "to_account")
    private String to;
    private String message;

    private long amountInHundredScale;

    private Timestamp transferDateTime;
    private String zone;

    private TransferType transferType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;
}
