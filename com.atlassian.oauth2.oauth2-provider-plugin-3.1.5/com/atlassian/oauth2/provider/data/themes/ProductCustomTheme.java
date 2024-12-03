/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.data.themes;

public class ProductCustomTheme {
    private final String headerColor;
    private final String logoUrl;

    public ProductCustomTheme(String headerColor, String logoUrl) {
        this.headerColor = headerColor;
        this.logoUrl = logoUrl;
    }

    public String getHeaderColor() {
        return this.headerColor;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }
}

