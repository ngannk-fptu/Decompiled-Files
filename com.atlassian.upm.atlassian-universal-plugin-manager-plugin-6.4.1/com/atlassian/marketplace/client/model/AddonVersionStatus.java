/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.EnumWithKey;

public enum AddonVersionStatus implements EnumWithKey
{
    PUBLIC("public"),
    PRIVATE("private"),
    SUBMITTED("submitted");

    private final String key;

    private AddonVersionStatus(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

