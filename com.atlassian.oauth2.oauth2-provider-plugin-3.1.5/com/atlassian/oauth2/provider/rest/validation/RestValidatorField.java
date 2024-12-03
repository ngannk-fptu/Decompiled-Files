/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.validation;

public enum RestValidatorField {
    NAME("name"),
    REDIRECTS("redirects"),
    SCOPE("scope");

    private final String value;

    public String toString() {
        return this.value;
    }

    public String getValue() {
        return this.value;
    }

    private RestValidatorField(String value) {
        this.value = value;
    }
}

