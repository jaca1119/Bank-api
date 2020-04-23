package com.itvsme.bank.controllers;

import com.itvsme.bank.services.UserDataService;
import com.itvsme.bank.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest
{
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private UserDataService userDataService;

    @WithMockUser("User")
    @Test
    void testCORSRequest() throws Exception
    {
        mockMvc.perform(get("/user-data")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Access-Control-Request-Method", "GET")
                .header("Origin", "https://affectionate-carson-6417c5.netlify.app"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://affectionate-carson-6417c5.netlify.app"))
                .andDo(print());
    }

    @WithMockUser("User")
    @Test
    void CORSFromEvilSiteShouldFail() throws Exception
    {
        mockMvc.perform(get("/user-data")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Access-Control-Request-Method", "GET")
                .header("Origin", "https://evil-site.app/"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}