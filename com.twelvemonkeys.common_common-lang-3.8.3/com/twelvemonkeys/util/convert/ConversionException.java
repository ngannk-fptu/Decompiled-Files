/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

public class ConversionException
extends IllegalArgumentException {
    public ConversionException(String string) {
        super(string);
    }

    public ConversionException(Throwable throwable) {
        super(throwable != null ? throwable.getMessage() : null, throwable);
    }

    public ConversionException(String string, Throwable throwable) {
        super(string, throwable);
    }
}

