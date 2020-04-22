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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
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

    @Test
    void testPostAuthenticate() throws Exception, AccountEnablingException
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