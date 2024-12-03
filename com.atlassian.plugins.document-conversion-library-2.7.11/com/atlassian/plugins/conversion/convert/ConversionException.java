/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert;

public class ConversionException
extends Exception {
    public ConversionException(Throwable cause) {
        super(cause);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException() {
    }
}

