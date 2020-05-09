package com.itvsme.bank.account;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class AccountCreateRequestDTO
{
    private String accountName;
    private String currency;
}
