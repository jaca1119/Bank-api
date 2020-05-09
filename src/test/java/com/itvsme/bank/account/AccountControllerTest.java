package com.itvsme.bank.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itvsme.bank.TestUtils;
import com.itvsme.bank.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AccountController.class)
class AccountControllerTest
{
    @Autowired
    MockMvc mvc;
    @MockBean
    AccountService accountService;
    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    void testCreatingAccount() throws Exception
    {
        AccountCreateRequestDTO accountCreateRequestDTO = new AccountCreateRequestDTO();
        accountCreateRequestDTO.setAccountName("Test name");
        accountCreateRequestDTO.setCurrency("EUR");

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(accountCreateRequestDTO);

        mvc.perform(post("/account/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)
            .cookie(TestUtils.createAuthenticationJwtCookie())
            .header("Origin", "https://affectionate-carson-6417c5.netlify.app"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account created"));
    }

    @Test
    void testPreflightOptionsWhenCreatingAccount() throws Exception
    {
        mvc.perform(options("/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(TestUtils.createAuthenticationJwtCookie())
                .header("Access-Control-Request-Method", "POST")
                .header("Origin", "https://affectionate-carson-6417c5.netlify.app"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreatingAccountWithMaximumAccountLimit() throws Exception, CreatingAccountException
    {
        AccountCreateRequestDTO accountCreateRequestDTO = new AccountCreateRequestDTO();
        accountCreateRequestDTO.setAccountName("Test name");
        accountCreateRequestDTO.setCurrency("EUR");

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(accountCreateRequestDTO);

        Mockito.doThrow(new CreatingAccountException("Test")).when(accountService).createAccount(any(), any());

        mvc.perform(post("/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(TestUtils.createAuthenticationJwtCookie())
                .header("Origin", "https://affectionate-carson-6417c5.netlify.app"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Test"));
    }
}