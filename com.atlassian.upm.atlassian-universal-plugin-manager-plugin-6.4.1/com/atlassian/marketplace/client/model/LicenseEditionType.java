/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.EnumWithKey;

public enum LicenseEditionType implements EnumWithKey
{
    USER_TIER("user-tier"),
    ROLE_TIER("role-tier"),
    REMOTE_AGENT_COUNT("remote-agent-count");

    private final String key;

    private LicenseEditionType(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public static LicenseEditionType getDefault() {
        return USER_TIER;
    }
}

