package com.itvsme.bank.registration;


import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.models.user.UserDTO;
import com.itvsme.bank.repositories.UserAppRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService
{
    private UserAppRepository userAppRepository;
    private PasswordEncoder passwordEncoder;
    private RegistrationTokenRepository tokenRepository;
    private EmailService emailService;

    public RegistrationService(UserAppRepository userAppRepository, PasswordEncoder passwordEncoder, RegistrationTokenRepository tokenRepository, EmailService emailService)
    {
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    public void register(UserDTO userDTO) throws Exception
    {
        if (userNotExists(userDTO.getUsername()))
        {
            UserApp userApp = new UserApp();
            userApp.setUsername(userDTO.getUsername());
            userApp.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userApp.setEmail(userDTO.getEmail());
            userApp.setEnabled(false);
            userApp.setRole("USER");

            userAppRepository.save(userApp);

            sendTokenInEmail(userApp, userDTO.getEmail());
        }
        else
        {
            throw new Exception("User exist");
        }
    }

    private void sendTokenInEmail(UserApp userApp, String userEmail)
    {
        String tokenValue = UUID.randomUUID().toString();
        RegistrationToken token = new RegistrationToken();
        token.setValue(tokenValue);
        token.setUserApp(userApp);

        tokenRepository.save(token);

        String url = "http://localhost:8080/token?value=" + tokenValue;

        emailService.sendMail(userEmail, "Registration", url);
    }

    private boolean userNotExists(@NotNull @NotEmpty String username)
    {
        Optional<UserApp> optionalUserApp = userAppRepository.findByUsername(username);

        return optionalUserApp.isEmpty();
    }
}
