/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.application;

public enum InternalApplicationAttributes {
    ACCESS_DENIED("accessDenied");

    private final String attribute;

    private InternalApplicationAttributes(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return this.attribute;
    }
}

