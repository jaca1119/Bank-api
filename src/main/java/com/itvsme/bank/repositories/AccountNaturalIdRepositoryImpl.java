package com.itvsme.bank.repositories;

import com.itvsme.bank.models.account.Account;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Optional;

//Naming is important for custom repository interfaces
public class AccountNaturalIdRepositoryImpl implements AccountNaturalIdRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Optional<Account> findByNaturalId(String naturalId)
    {
        return entityManager.unwrap(Session.class)
                .byNaturalId(Account.class)
                .using("accountBusinessId", naturalId)
                .loadOptional();
    }
}
