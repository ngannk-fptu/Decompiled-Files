/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.enums;

public enum ParameterIn {
    DEFAULT(""),
    HEADER("header"),
    QUERY("query"),
    PATH("path"),
    COOKIE("cookie");

    private String value;

    private ParameterIn(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}

