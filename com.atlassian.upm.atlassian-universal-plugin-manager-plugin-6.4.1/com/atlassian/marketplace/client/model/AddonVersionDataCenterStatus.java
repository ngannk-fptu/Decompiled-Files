/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.EnumWithKey;

public enum AddonVersionDataCenterStatus implements EnumWithKey
{
    COMPATIBLE("compatible"),
    PENDING("pending"),
    REJECTED("rejected");

    private final String key;

    private AddonVersionDataCenterStatus(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

