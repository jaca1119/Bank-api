package com.itvsme.bank.registration;


import com.itvsme.bank.models.account.Account;
import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.models.user.UserDTO;
import com.itvsme.bank.registration.email.EmailService;
import com.itvsme.bank.registration.exceptions.AccountEnablingException;
import com.itvsme.bank.registration.utils.BusinessIdCreator;
import com.itvsme.bank.repositories.account.AccountRepository;
import com.itvsme.bank.repositories.UserAppRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService
{
    @Value("${registration.url}")
    private String registrationUrl;

    private UserAppRepository userAppRepository;
    private PasswordEncoder passwordEncoder;
    private RegistrationTokenRepository tokenRepository;
    private EmailService emailService;
    private AccountRepository accountRepository;

    public RegistrationService(UserAppRepository userAppRepository, PasswordEncoder passwordEncoder, RegistrationTokenRepository tokenRepository, EmailService emailService, AccountRepository accountRepository)
    {
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.accountRepository = accountRepository;
    }

    public void register(UserDTO userDTO) throws Exception
    {
        if (userNotExists(userDTO.getUsername()))
        {
            UserApp userApp = createUser(userDTO);

            userAppRepository.save(userApp);

            Account account = new Account();
            account.setCurrency("EUR");
            account.setBalanceInHundredScale(1000 * 100);
            account.setAccountBusinessId(BusinessIdCreator.createBusinessId(userApp.getId()));

            accountRepository.save(account);

            List<Account> accounts = new ArrayList<>();
            accounts.add(account);

            userApp.setAccounts(accounts);

            userAppRepository.save(userApp);

            sendRegistrationTokenInEmail(userApp, userDTO.getEmail());
        }
        else
        {
            throw new Exception("User exist");
        }
    }

    public void activateUser(String token) throws AccountEnablingException
    {
        Optional<RegistrationToken> registrationToken = tokenRepository.findByValue(token);

        if (registrationToken.isPresent())
        {
            UserApp userApp = registrationToken.get().getUserApp();
            userApp.setEnabled(true);

            userAppRepository.save(userApp);
        }
        else
        {
            throw new AccountEnablingException("Something went wrong with account enabling");
        }
    }

    private void sendRegistrationTokenInEmail(UserApp userApp, String userEmail) throws MailException
    {
        String tokenValue = UUID.randomUUID().toString();
        RegistrationToken token = new RegistrationToken();
        token.setValue(tokenValue);
        token.setUserApp(userApp);

        tokenRepository.save(token);

        String urlWithToken = registrationUrl + "/token?value=" + tokenValue;

        emailService.sendMail(userEmail, "Registration", urlWithToken);
    }

    private boolean userNotExists(@NotNull @NotEmpty String username)
    {
        Optional<UserApp> optionalUserApp = userAppRepository.findByUsername(username);

        return optionalUserApp.isEmpty();
    }

    private UserApp createUser(UserDTO userDTO)
    {
        UserApp userApp = new UserApp();
        userApp.setUsername(userDTO.getUsername());
        userApp.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userApp.setEmail(userDTO.getEmail());
        userApp.setEnabled(false);
        userApp.setRole("USER");

        return userApp;
    }
}
