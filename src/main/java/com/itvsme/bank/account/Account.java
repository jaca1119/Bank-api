package com.itvsme.bank.account;

import lombok.Data;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;

@Entity
@Data
@NaturalIdCache
public class Account
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long balanceInHundredScale;
    @Column(length = 64)
    private String name;
    private String currency;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String accountBusinessId;
}
