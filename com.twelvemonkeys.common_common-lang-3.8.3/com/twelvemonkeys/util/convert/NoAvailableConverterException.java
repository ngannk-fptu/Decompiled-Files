/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.util.convert.ConversionException;

public class NoAvailableConverterException
extends ConversionException {
    public NoAvailableConverterException() {
        super("Cannot convert, no converter available for given type");
    }

    public NoAvailableConverterException(String string) {
        super(string);
    }
}

