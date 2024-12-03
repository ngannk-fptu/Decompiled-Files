/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.exception;

public abstract class TranslatableException
extends RuntimeException {
    private final String i18nKey;

    protected TranslatableException(String i18nKey, String message) {
        super(message);
        this.i18nKey = i18nKey;
    }

    public String getI18nKey() {
        return this.i18nKey;
    }
}

