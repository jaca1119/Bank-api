package com.itvsme.bank.registration;

import com.itvsme.bank.models.user.UserApp;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class RegistrationToken
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    @OneToOne(cascade = CascadeType.PERSIST)
    private UserApp userApp;
}
