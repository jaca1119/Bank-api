package com.itvsme.bank.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.itvsme.bank.utils.ApplicationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Slf4j
public class JwtFilter extends OncePerRequestFilter
{
    private final String SECRET_KEY = ApplicationConstants.SECRET_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        System.out.println("FILTER");

        Cookie tokenCookie = null;
        if (request.getCookies() != null)
        {
            for (Cookie cookie : request.getCookies())
            {
                if (cookie.getName().equals("accessToken"))
                {
                    tokenCookie = cookie;
                    break;
                }
            }
        }

        if (tokenCookie != null)
        {
            cookieAuthorization(tokenCookie);
        }

        chain.doFilter(request, response);
        log.info("chain: {}, request: {}, response: {}", chain, request, response);

    }

    private void cookieAuthorization(Cookie cookie) throws IOException
    {
        UsernamePasswordAuthenticationToken auth = getTokenAuthentication(cookie.getValue());

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private UsernamePasswordAuthenticationToken getTokenAuthentication(String token) throws IOException
    {
        Map<String, Claim> claims = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token)
                .getClaims();


        String name = claims.get("sub").asString();
//        String role = claims.get("role").asString();
        Set<SimpleGrantedAuthority> simpleGrantedAuthority = Collections.singleton(new SimpleGrantedAuthority("USER"));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(name, null, simpleGrantedAuthority);
        System.out.println(usernamePasswordAuthenticationToken);

        return usernamePasswordAuthenticationToken;
    }
}
