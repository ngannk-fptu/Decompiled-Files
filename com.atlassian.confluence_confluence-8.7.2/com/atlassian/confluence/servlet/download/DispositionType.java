/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.servlet.download;

public enum DispositionType {
    INLINE("inline"),
    ATTACHMENT("attachment");

    private String value;

    private DispositionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}

