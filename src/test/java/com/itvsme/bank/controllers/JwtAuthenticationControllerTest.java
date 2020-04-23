package com.itvsme.bank.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itvsme.bank.models.jwt.JwtTokenRequest;
import com.itvsme.bank.models.jwt.JwtTokenResponse;
import com.itvsme.bank.registration.AccountEnablingException;
import com.itvsme.bank.services.JwtAuthenticationService;
import com.itvsme.bank.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.*;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JwtAuthenticationController.class)
class JwtAuthenticationControllerTest
{
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtAuthenticationService authenticationService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @TestConfiguration()
    static class ServerTimeZoneConfig
    {
        @PostConstruct
        public void init()
        {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        }
    }

    @Test
    void testPostAuthenticate() throws Exception, AccountEnablingException
    {
        ObjectMapper objectMapper = new ObjectMapper();

        JwtTokenRequest tokenRequest = new JwtTokenRequest();
        tokenRequest.setUsername("user");
        tokenRequest.setPassword("pass");

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tokenRequest);

        when(authenticationService.authenticate(any(), any(), any())).thenReturn(new JwtTokenResponse("asd"));
        when(authenticationService.generateRefreshToken(any(), any(), any())).thenReturn(new JwtTokenResponse("asd"));

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "https://affectionate-carson-6417c5.netlify.app")
                .characterEncoding("UTF8")
                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://affectionate-carson-6417c5.netlify.app"))
                .andExpect(content().string(containsString("Authenticated")))
                .andDo(print());
    }

    @Test
    void testWrongSetLocalExpirationTimeJwt()
    {
        Instant fromNowPlusTenMinutes = Instant.now().plusSeconds(60 * 10);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(fromNowPlusTenMinutes, ZoneId.of("Etc/GMT+2"));

        String asd = JWT.create()
                .withExpiresAt(Date.from(fromNowPlusTenMinutes))
                .sign(Algorithm.HMAC256("asd"));

        Date expiresAt = JWT.decode(asd).getExpiresAt();

        assertThat(zonedDateTime.toInstant().toEpochMilli()).isNotEqualTo(expiresAt.toInstant().toEpochMilli());
    }

    @Test
    void testSetLocalExpirationTimeJwt()
    {
        Instant fromNowPlusTenMinutes = Instant.now().plusSeconds(60 * 10);
        TimeZone timeZone = TimeZone.getTimeZone("GMT+2");

        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(fromNowPlusTenMinutes, ZoneId.of(timeZone.getID()));

        String asd = JWT.create()
                .withExpiresAt(Date.from(zonedDateTime.toInstant()))
                .sign(Algorithm.HMAC256("asd"));

        Date expiresAt = JWT.decode(asd).getExpiresAt();

        assertThat(expiresAt.getTime()).isEqualTo(zonedDateTime.toInstant().truncatedTo(ChronoUnit.SECONDS).toEpochMilli());
    }

    @Test
    void testPreflightOptions() throws Exception, AccountEnablingException
    {
        mockMvc.perform(options("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "https://affectionate-carson-6417c5.netlify.app")
                .header("Access-Control-Request-Method", "POST")
                )
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://affectionate-carson-6417c5.netlify.app"))
                .andDo(print());
    }

    @Test
    void testEvilPreflightOptions() throws Exception
    {
        mockMvc.perform(options("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "https://evil.com")
                .header("Access-Control-Request-Method", "POST")
        )
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}