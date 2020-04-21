package com.itvsme.bank.models.account;

import com.itvsme.bank.models.user.UserApp;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Data
public class Account
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal balance;
    private String currency;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String accountBusinessId;
}
