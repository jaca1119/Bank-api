package com.itvsme.bank.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.itvsme.bank.models.jwt.JwtRefreshToken;
import com.itvsme.bank.models.jwt.JwtTokenRequest;
import com.itvsme.bank.models.jwt.JwtTokenResponse;
import com.itvsme.bank.services.JwtAuthenticationService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Arrays;
import java.util.Optional;

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
    public ResponseEntity<?> createJwtAuthenticationToken(@RequestBody JwtTokenRequest tokenRequest, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            JwtTokenResponse accessToken = authenticationService.authenticate(tokenRequest, String.valueOf(request.getRequestURL()));
            JwtTokenResponse refreshToken = authenticationService.generateRefreshToken(tokenRequest.getUsername(), String.valueOf(request.getRequestURL()));

            log.info(Arrays.toString(request.getCookies()));

            Cookie accessTokenCookie = createCookieWithToken("accessToken", accessToken.getToken(), 3 * 60 * 60);

            Cookie refreshTokenCookie = createCookieWithToken("refreshToken", refreshToken.getToken(), 5 * 60 * 60);

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
    public ResponseEntity<?> refreshJWT(@RequestBody JwtRefreshToken refreshToken, HttpServletRequest request, HttpServletResponse response)
    {
        Optional<JwtTokenResponse> accessToken = authenticationService.refreshAccessToken(refreshToken, String.valueOf(request.getRequestURL()));

        if (accessToken.isPresent())
        {
            Cookie accessTokenCookie = createCookieWithToken("accessToken", accessToken.get().getToken(), 3 * 60 * 60);

            Cookie refreshTokenCookie = createCookieWithToken("refreshToken",
                    authenticationService.generateRefreshToken(refreshToken.getSubject(), request.getRequestURI()).getToken(),
                    5 * 60 * 60);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok().build();
        }
        else
        {
            throw new JWTVerificationException("Refresh token expired");
        }
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
