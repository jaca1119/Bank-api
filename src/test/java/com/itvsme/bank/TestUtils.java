package com.itvsme.bank;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.utils.ApplicationConstants;

import javax.servlet.http.Cookie;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

public class TestUtils
{
    private static final String secretKey = ApplicationConstants.SECRET_KEY;

    public static Cookie createAuthenticationJwtCookie()
    {
        UserApp userApp = new UserApp();
        userApp.setUsername("User");

        Instant now = Instant.now();
        ZonedDateTime zonedDateTimeNow = ZonedDateTime.ofInstant(now, TimeZone.getTimeZone("GMT+2").toZoneId());

        String jwt = JWT.create()
                .withSubject(userApp.getUsername())
                .withIssuedAt(Date.from(zonedDateTimeNow.toInstant()))
                .withExpiresAt(Date.from(zonedDateTimeNow.plusMinutes(10).toInstant()))
                .sign(Algorithm.HMAC256(secretKey));

        Cookie cookie = new Cookie("accessToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(10 * 60);
        cookie.setPath("/");

        return cookie;
    }
}
