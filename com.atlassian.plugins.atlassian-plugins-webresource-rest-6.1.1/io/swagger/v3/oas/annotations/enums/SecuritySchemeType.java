/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.enums;

public enum SecuritySchemeType {
    DEFAULT(""),
    APIKEY("apiKey"),
    HTTP("http"),
    OPENIDCONNECT("openIdConnect"),
    OAUTH2("oauth2");

    private String value;

    private SecuritySchemeType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}

