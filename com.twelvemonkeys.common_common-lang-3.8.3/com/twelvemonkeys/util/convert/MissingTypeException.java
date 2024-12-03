/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.util.convert.ConversionException;

public class MissingTypeException
extends ConversionException {
    public MissingTypeException() {
        super("Cannot convert, missing type");
    }

    public MissingTypeException(String string) {
        super(string);
    }
}

