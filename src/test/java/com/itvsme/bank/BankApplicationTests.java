package com.itvsme.bank;

import com.itvsme.bank.account.Account;
import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.repositories.UserAppRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class BankApplicationTests {

	@Autowired
	UserAppRepository userAppRepository;

	@Test
	void contextLoads() {
	}

	@Transactional
	@Test
	void testPizzeriaAccountExistOnInit()
	{
		Optional<UserApp> pizzeria = userAppRepository.findByUsername("pizzeria");

		if (pizzeria.isPresent())
		{
			Account account = pizzeria.get().getAccounts().get(0);

			assertThat(account.getBalanceInHundredScale()).isEqualTo(0);
			assertThat(account.getName()).isEqualTo("Pizzeria");
			assertThat(account.getAccountBusinessId()).isEqualTo("99 3333 7777 0000 0000 0001 1112");
		}
		else
		{
			fail();
		}
	}

}
