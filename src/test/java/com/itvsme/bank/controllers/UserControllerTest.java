package com.itvsme.bank.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.itvsme.bank.services.UserDataService;
import com.itvsme.bank.services.UserDetailsServiceImpl;
import com.itvsme.bank.utils.ApplicationConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    @Test
    void testCORSRequestWithJwtCookieAuthentication() throws Exception
    {
        String secretKey = ApplicationConstants.SECRET_KEY;
        Instant now = Instant.now();

        ZonedDateTime zonedDateTimeNow = ZonedDateTime.ofInstant(now, TimeZone.getTimeZone("GMT+2").toZoneId());

        String jwt = JWT.create()
                .withSubject("User")
                .withIssuedAt(Date.from(zonedDateTimeNow.toInstant()))
                .withExpiresAt(Date.from(zonedDateTimeNow.plusMinutes(10).toInstant()))
                .sign(Algorithm.HMAC256(secretKey));

        Cookie cookie = new Cookie("accessToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(10 * 60);
        cookie.setPath("/");

        mockMvc.perform(get("/user-data")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .header("Access-Control-Request-Method", "GET")
                .header("Origin", "https://affectionate-carson-6417c5.netlify.app"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://affectionate-carson-6417c5.netlify.app"))
                .andDo(print());
    }

    @Test
    void CORSFromEvilSiteShouldFail() throws Exception
    {
        mockMvc.perform(get("/user-data")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Access-Control-Request-Method", "GET")
                .header("Origin", "https://evil-site.app"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}