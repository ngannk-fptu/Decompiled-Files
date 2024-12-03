/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

public enum VendorExternalLinkType {
    HOME_PAGE("homePage"),
    SLA("sla");

    private final String key;

    private VendorExternalLinkType(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

