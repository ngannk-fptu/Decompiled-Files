/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.languages;

public final class InvalidLanguageException
extends Exception {
    private static final long serialVersionUID = 1L;
    private String errorMsgKey;
    private Object[] params;

    public InvalidLanguageException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMsgKey = errorMessage;
    }

    public InvalidLanguageException(String errorMsgKey, Object ... params) {
        this.errorMsgKey = errorMsgKey;
        this.params = params;
    }

    public String getErrorMsgKey() {
        return this.errorMsgKey;
    }

    public Object[] getParams() {
        return this.params;
    }
}

