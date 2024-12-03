/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.EnumWithKey;

public enum PricingType implements EnumWithKey
{
    SERVER("server"),
    CLOUD("cloud"),
    DATA_CENTER("datacenter");

    private final String key;

    private PricingType(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

