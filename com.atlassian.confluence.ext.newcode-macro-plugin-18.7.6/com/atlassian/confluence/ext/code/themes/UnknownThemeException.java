/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.themes;

public final class UnknownThemeException
extends Exception {
    private static final long serialVersionUID = 1L;
    private String name;

    public UnknownThemeException(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

