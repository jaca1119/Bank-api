package com.itvsme.bank.models.account;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Data
public class Account
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long balanceInHundredScale;
    private String currency;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String accountBusinessId;
}
