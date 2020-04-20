package com.itvsme.bank.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.itvsme.bank.models.jwt.JwtRefreshToken;
import com.itvsme.bank.models.jwt.JwtTokenRequest;
import com.itvsme.bank.models.jwt.JwtTokenResponse;
import com.itvsme.bank.utils.ApplicationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class JwtAuthenticationService
{
    private AuthenticationManager authenticationManager;

    private final String SECRET_KEY = ApplicationConstants.SECRET_KEY;

    public JwtAuthenticationService(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    public JwtTokenResponse authenticate(JwtTokenRequest tokenRequest, String url) throws AuthenticationException
    {
        UserDetails userDetails = managerAuthentication(tokenRequest.getUsername(), tokenRequest.getPassword());

        String token = generateToken(userDetails.getUsername(), url);

        return new JwtTokenResponse(token);
    }

    public JwtTokenResponse generateRefreshToken(String subject, String url)
    {
        try
        {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            String token = JWT.create()
                    .withIssuer(url)
                    .withSubject(subject)
                    .withIssuedAt(Date.from(Instant.now()))
                    .withExpiresAt(Date.from(Instant.now().plusSeconds(60 * 60)))
                    .sign(algorithm);

            return new JwtTokenResponse(token);
        }
        catch (JWTCreationException e)
        {
            e.printStackTrace();
            throw new JWTCreationException("Exception creating token", e);
        }
    }

    public Optional<JwtTokenResponse> refreshToken(JwtRefreshToken refreshToken, String url)
    {
        JwtTokenResponse response = null;
        try
        {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .build()
                    .verify(refreshToken.getRefreshToken());

            response = new JwtTokenResponse(generateToken(decodedJWT.getSubject(), url));
        }
        catch (JWTVerificationException e)
        {
            response = null;
        }
        return Optional.ofNullable(response);
    }

    private String generateToken(String username, String url)
    {
        try
        {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            String token = JWT.create()
                    .withIssuer(url)
                    .withSubject(username)
                    .withIssuedAt(Date.from(Instant.now()))
                    .withExpiresAt(Date.from(Instant.now().plusSeconds(60 * 10)))
                    .sign(algorithm);

            return token;
        }
        catch (JWTCreationException e)
        {
            e.printStackTrace();
            throw new JWTCreationException("Exception creating token", e);
        }
    }

    private UserDetails managerAuthentication(String username, String password) throws AuthenticationException
    {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        return (UserDetails) authenticate.getPrincipal();
    }
}
