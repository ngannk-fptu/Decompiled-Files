/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.EnumWithKey;

public enum TopVendorStatus implements EnumWithKey
{
    APPROVED("approved"),
    REQUESTED("requested"),
    NOT_REQUESTED("not-requested"),
    FLAGGED("flagged"),
    REJECTED("rejected"),
    NEEDS_APPROVAL("needs-approval");

    private final String key;

    private TopVendorStatus(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

