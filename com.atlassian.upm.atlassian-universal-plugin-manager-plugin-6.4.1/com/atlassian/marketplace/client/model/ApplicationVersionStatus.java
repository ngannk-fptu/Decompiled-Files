/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.EnumWithKey;

public enum ApplicationVersionStatus implements EnumWithKey
{
    PUBLISHED("published"),
    UNPUBLISHED("unpublished");

    private final String key;

    private ApplicationVersionStatus(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

