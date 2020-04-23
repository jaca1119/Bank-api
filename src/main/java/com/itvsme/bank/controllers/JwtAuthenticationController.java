package com.itvsme.bank.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.itvsme.bank.models.jwt.JwtTokenRequest;
import com.itvsme.bank.models.jwt.JwtTokenResponse;
import com.itvsme.bank.services.JwtAuthenticationService;
import com.itvsme.bank.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.TimeZone;

@RestController
@Slf4j
public class JwtAuthenticationController
{
    private JwtAuthenticationService authenticationService;

    public JwtAuthenticationController(JwtAuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> createJwtAuthenticationToken(@RequestBody JwtTokenRequest tokenRequest, HttpServletRequest request, HttpServletResponse response, TimeZone timeZone)
    {
        log.info("Request TimeZone is: {}, user current date time: {}", timeZone.getID(), ZonedDateTime.ofInstant(Instant.now(), timeZone.toZoneId()));
        try
        {
            JwtTokenResponse accessToken = authenticationService.authenticate(tokenRequest, String.valueOf(request.getRequestURL()), timeZone);
            JwtTokenResponse refreshToken = authenticationService.generateRefreshToken(tokenRequest.getUsername(), String.valueOf(request.getRequestURL()), timeZone);

            log.info(Arrays.toString(request.getCookies()));

            Cookie accessTokenCookie = createCookieWithToken("accessToken", accessToken.getToken(), 10 * 60);

            Cookie refreshTokenCookie = createCookieWithToken("refreshToken", refreshToken.getToken(), 60 * 60);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok("Authenticated");
        }
        catch (AuthenticationException e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<String> refreshJWT(HttpServletRequest request, HttpServletResponse response, TimeZone timeZone)
    {
        Optional<Cookie> refreshCookie = getRefreshTokenCookieFromRequest(request);

        if (refreshCookie.isEmpty())
        {
            return ResponseEntity.badRequest().body("No refresh token");
        }

        try
        {
            JwtTokenResponse accessToken = authenticationService.refreshAccessToken(refreshCookie.get(), String.valueOf(request.getRequestURL()), timeZone);

            Cookie accessTokenCookie = createCookieWithToken("accessToken", accessToken.getToken(), 10 * 60);

            Cookie refreshTokenCookie = createCookieWithToken("refreshToken",
                    authenticationService.generateRefreshToken(JwtUtils.getSubjectFromToken(accessToken.getToken()), request.getRequestURI(), timeZone).getToken(),
                    60 * 60);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok("Token refreshed");
        } catch (JWTVerificationException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    private Optional<Cookie> getRefreshTokenCookieFromRequest(HttpServletRequest request)
    {
        Cookie tokenCookie = null;

        if (request.getCookies() != null)
        {
            for (Cookie cookie : request.getCookies())
            {
                if (cookie.getName().equals("refreshToken"))
                {
                    tokenCookie = cookie;
                    break;
                }
            }
        }
        return Optional.ofNullable(tokenCookie);
    }

    private Cookie createCookieWithToken(String name, String token, int maxAge)
    {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");

        return cookie;
    }
}
