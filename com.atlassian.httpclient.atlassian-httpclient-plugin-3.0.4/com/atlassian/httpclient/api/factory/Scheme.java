/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api.factory;

public enum Scheme {
    HTTP("http"),
    HTTPS("https");

    private final String schemeName;

    public String schemeName() {
        return this.schemeName;
    }

    private Scheme(String name) {
        this.schemeName = name;
    }
}

