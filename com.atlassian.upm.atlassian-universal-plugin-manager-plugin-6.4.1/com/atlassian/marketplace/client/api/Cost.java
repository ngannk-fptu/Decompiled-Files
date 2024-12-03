/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.EnumWithKey;

public enum Cost implements EnumWithKey
{
    FREE("free"),
    ALL_PAID("paid"),
    PAID_VIA_ATLASSIAN("marketplace");

    private final String key;

    private Cost(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

