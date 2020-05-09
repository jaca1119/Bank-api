package com.itvsme.bank.registration.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class BusinessIdCreator
{
    private static final int CONTROL_SUM = 99;
    private static final int BANK_CODE = 3333;
    private static final int DEPARTMENT = 7777;

    private static AtomicInteger id = new AtomicInteger(1);

    public static String createBusinessId()
    {
        int leadingZeros = 16 - String.valueOf(id.get()).length();

        return String.format("%d %d %d %0" + leadingZeros + "d", CONTROL_SUM, BANK_CODE, DEPARTMENT, id.getAndIncrement());
    }
}
