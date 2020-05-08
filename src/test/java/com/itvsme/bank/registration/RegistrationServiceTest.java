package com.itvsme.bank.registration;

import com.itvsme.bank.models.user.UserDTO;
import com.itvsme.bank.registration.email.EmailService;
import com.itvsme.bank.repositories.UserAppRepository;
import com.itvsme.bank.repositories.account.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
class RegistrationServiceTest
{
    @SpyBean
    RegistrationService registrationService;
    @Autowired
    AccountRepository accountRepository;
    @MockBean
    EmailService emailService;
    @MockBean
    UserAppRepository userAppRepository;


    @Test
    void testRegisterAccountName() throws Exception
    {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("test");

        when(userAppRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        doNothing().when(emailService).sendMail(any(), any(), any());

        registrationService.register(userDTO);

        assertThat(accountRepository.getOne(1L).getName()).isEqualTo("First account");
    }
}