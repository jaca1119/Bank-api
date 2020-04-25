package com.itvsme.bank.registration.email;

import org.springframework.mail.MailException;

public interface EmailService
{
    void sendMail(String to, String subject, String text) throws MailException;
}
