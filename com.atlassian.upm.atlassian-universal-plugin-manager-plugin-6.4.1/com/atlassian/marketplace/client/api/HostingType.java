/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.EnumWithKey;

public enum HostingType implements EnumWithKey
{
    SERVER("server"),
    DATA_CENTER("datacenter"),
    CLOUD("cloud");

    private final String key;

    private HostingType(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

