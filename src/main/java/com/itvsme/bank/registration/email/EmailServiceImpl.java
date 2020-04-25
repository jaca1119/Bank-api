package com.itvsme.bank.registration.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService
{
    @Value("${spring.mail.username}")
    private String emailFrom;
    private JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMail(String to, String subject, String text) throws MailException
    {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

        try
        {
            messageHelper.setFrom(emailFrom);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(text);

            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException e)
        {
            e.printStackTrace();
            throw new MailSendException(e.getMessage());
        }
    }
}
