/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

public enum ResponseMode {
    FORM_POST("form_post"),
    QUERY("query"),
    FRAGMENT("fragment");

    private String responseMode;

    private ResponseMode(String responseMode) {
        this.responseMode = responseMode;
    }

    public String toString() {
        return this.responseMode;
    }
}

