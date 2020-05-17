package com.itvsme.bank.registration.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessIdCreatorTest
{
    BusinessIdCreator businessIdCreator;

    @BeforeEach
    void setUp()
    {
        businessIdCreator = new BusinessIdCreator();
    }

    @Test
    void testFirstBusinessId()
    {
        assertThat(businessIdCreator.createBusinessId()).isEqualTo("99 3333 7777 0000 0000 0001 1111");
    }
}