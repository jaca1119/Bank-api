package com.itvsme.bank;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.itvsme.bank.account.Account;
import com.itvsme.bank.models.user.UserApp;
import com.itvsme.bank.transfer.TransferDTO;
import com.itvsme.bank.transfer.TransferType;
import com.itvsme.bank.utils.ApplicationConstants;

import javax.servlet.http.Cookie;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public static Account createAccount(long balanceInHundredScale, String currency, String businessId)
    {
        Account account = new Account();
        account.setBalanceInHundredScale(balanceInHundredScale);
        account.setCurrency(currency);
        account.setAccountBusinessId(businessId);

        return account;
    }

    public static TransferDTO createTransferDTO(String toKey, String fromKey, long transferValue)
    {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setTo(toKey);
        transferDTO.setFrom(fromKey);
        transferDTO.setMessage("Tested transfer");
        transferDTO.setAmountInHundredScale(transferValue);
        transferDTO.setTransferDateTime(Timestamp.from(Instant.now()));
        transferDTO.setZone("GMT+3");
        transferDTO.setTransferType(TransferType.INTERNAL);

        return transferDTO;
    }

    public static UserApp createUserApp(int numberOfAccounts)
    {
        UserApp userApp = new UserApp();
        userApp.setUsername("Test");
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < numberOfAccounts; i++)
        {
            accounts.add(createAccount(1000, "EUR", "test" + i));
        }

        userApp.setAccounts(accounts);

        return userApp;
    }
}
