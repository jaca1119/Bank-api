package com.itvsme.bank.models.jwt;


import com.itvsme.bank.utils.JwtUtils;

public class JwtRefreshToken
{
    private String refreshToken;

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public String getSubject()
    {
        return JwtUtils.getSubjectFromToken(this.refreshToken);
    }
}
