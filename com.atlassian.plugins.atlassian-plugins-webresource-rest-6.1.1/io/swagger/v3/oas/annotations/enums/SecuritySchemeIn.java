/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.enums;

public enum SecuritySchemeIn {
    DEFAULT(""),
    HEADER("header"),
    QUERY("query"),
    COOKIE("cookie");

    private String value;

    private SecuritySchemeIn(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}

