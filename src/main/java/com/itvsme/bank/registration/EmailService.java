package com.itvsme.bank.registration;

public interface EmailService
{
    void sendMail(String to, String subject, String text);
}
