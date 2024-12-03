/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.EnumWithKey;

public enum PaymentModel implements EnumWithKey
{
    FREE("free"),
    PAID_VIA_VENDOR("vendor"),
    PAID_VIA_ATLASSIAN("atlassian");

    private final String key;

    private PaymentModel(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

