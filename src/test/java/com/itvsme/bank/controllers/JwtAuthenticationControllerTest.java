package com.itvsme.bank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itvsme.bank.models.jwt.JwtTokenRequest;
import com.itvsme.bank.models.jwt.JwtTokenResponse;
import com.itvsme.bank.registration.AccountEnablingException;
import com.itvsme.bank.registration.RegistrationService;
import com.itvsme.bank.services.JwtAuthenticationService;
import com.itvsme.bank.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JwtAuthenticationController.class)
class JwtAuthenticationControllerTest
{
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtAuthenticationService authenticationService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testPreflightOptions() throws Exception, AccountEnablingException
    {
        ObjectMapper objectMapper = new ObjectMapper();

        JwtTokenRequest tokenRequest = new JwtTokenRequest();
        tokenRequest.setUsername("user");
        tokenRequest.setPassword("pass");

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tokenRequest);

        when(authenticationService.authenticate(any(), any())).thenReturn(new JwtTokenResponse("asd"));
        when(authenticationService.generateRefreshToken(any(), any())).thenReturn(new JwtTokenResponse("asd"));

        mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF8")
                .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}