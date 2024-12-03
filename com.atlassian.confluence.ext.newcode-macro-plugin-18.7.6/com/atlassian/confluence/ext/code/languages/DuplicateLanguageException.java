/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.languages;

public final class DuplicateLanguageException
extends Exception {
    private static final long serialVersionUID = 1L;
    private String languageNameInError;

    public DuplicateLanguageException(String msg, String languageNameInError) {
        super(msg);
        this.languageNameInError = languageNameInError;
    }

    public String getLanguageNameInError() {
        return this.languageNameInError;
    }
}

