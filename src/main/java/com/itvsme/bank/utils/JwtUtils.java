package com.itvsme.bank.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtils
{
    private static final String SECRET_KEY = ApplicationConstants.SECRET_KEY;

    public static String getSubjectFromToken(String token)
    {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token);

        return decodedJWT.getSubject();
    }
}


