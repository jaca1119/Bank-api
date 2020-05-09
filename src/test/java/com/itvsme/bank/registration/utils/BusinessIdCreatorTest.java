package com.itvsme.bank.registration.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessIdCreatorTest
{
    @Test
    void testCreateBusinessIdCreatingDifferentIds()
    {
        String businessId = BusinessIdCreator.createBusinessId();
        String secondBusinessId = BusinessIdCreator.createBusinessId();

        assertThat(businessId).isNotEqualTo(secondBusinessId);
    }
}