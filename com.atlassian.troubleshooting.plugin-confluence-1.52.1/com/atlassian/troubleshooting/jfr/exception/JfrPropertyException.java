/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.exception;

import com.atlassian.troubleshooting.jfr.exception.TranslatableException;

public class JfrPropertyException
extends TranslatableException {
    public JfrPropertyException(String i18nKey, String message) {
        super(i18nKey, message);
    }
}

