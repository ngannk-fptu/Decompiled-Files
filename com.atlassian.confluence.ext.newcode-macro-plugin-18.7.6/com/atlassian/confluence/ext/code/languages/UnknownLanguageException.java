/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.languages;

public final class UnknownLanguageException
extends Exception {
    private static final long serialVersionUID = 1L;
    private String alias;

    public UnknownLanguageException(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return this.alias;
    }
}

