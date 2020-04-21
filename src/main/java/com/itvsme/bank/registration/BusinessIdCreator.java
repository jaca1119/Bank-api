package com.itvsme.bank.registration;

public class BusinessIdCreator
{
    private static final int CONTROL_SUM = 99;
    private static final int BANK_CODE = 3333;
    private static final int DEPARTMENT = 7777;


    public static String createBusinessId(Long id)
    {
        int leadingZeros = 16 - String.valueOf(id).length();

        return String.format("%d %d %d %0" + leadingZeros + "d", CONTROL_SUM, BANK_CODE, DEPARTMENT, id);
    }
}
