package com.itvsme.bank.registration.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BusinessIdCreator
{
    private final int CONTROL_SUM = 99;
    private final int BANK_CODE = 3333;
    private final int DEPARTMENT = 7777;

    private AtomicInteger id = new AtomicInteger(11111);

    public String createBusinessId()
    {
        String idWithLeadingZeros = idWithLeadingZeros(id.getAndIncrement());

//      example of first value: 99 3333 7777 0000 0000 0001 1111
        return String.format("%d %d %d %s", CONTROL_SUM, BANK_CODE, DEPARTMENT, idWithLeadingZeros);
    }

    private String idWithLeadingZeros(int id)
    {
        int leadingZeros = 16;

        String idWithLeadingZeros = String.format("%0" + leadingZeros + "d", id);
        String charsBetweenSpace = "4";
        String result = idWithLeadingZeros.replaceAll("(.{" + charsBetweenSpace + "})", "$1 ").trim();

        return result;
    }
}
