package com.itvsme.bank.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itvsme.bank.models.jwt.JwtTokenRequest;
import com.itvsme.bank.models.user.UserDTO;
import com.itvsme.bank.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.mock.mockito.SpyBeans;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest
{
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegistrationService registrationService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testFilterDontTriggerWhenEnablingAccount() throws Exception, AccountEnablingException
    {
        String tokenValue = "1234";

        Mockito.doThrow(new AccountEnablingException("Something went wrong with account enabling")).when(registrationService).activateUser(isA(String.class));

        mockMvc.perform(get("/token?value=" + tokenValue).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print())
                .andExpect(content().string(containsString("Something")));
    }

    @Test
    void testAccountEnablingByToken() throws Exception
    {
        String tokenValue = "1234";

        mockMvc.perform(get("/token?value=" + tokenValue)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "https://affectionate-carson-6417c5.netlify.app")
        ).andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString("Account enabled")));
    }

    @Test
    void testRegistrationCORS() throws Exception, AccountEnablingException
    {
        ObjectMapper objectMapper = new ObjectMapper();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("user");
        userDTO.setPassword("pass");
        userDTO.setEmail("asd@asd.com");

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userDTO);

        Mockito.doNothing().when(registrationService).register(isA(UserDTO.class));

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "https://affectionate-carson-6417c5.netlify.app")
                .content(json))
                    .andExpect(status().isOk()).andDo(print())
                    .andExpect(content().string(containsString("Registered")));
    }
}