/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.enums;

public enum ParameterStyle {
    DEFAULT(""),
    MATRIX("matrix"),
    LABEL("label"),
    FORM("form"),
    SPACEDELIMITED("spaceDelimited"),
    PIPEDELIMITED("pipeDelimited"),
    DEEPOBJECT("deepObject"),
    SIMPLE("simple");

    private String value;

    private ParameterStyle(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}

