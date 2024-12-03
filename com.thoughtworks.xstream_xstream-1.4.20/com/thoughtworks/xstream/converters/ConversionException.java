/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.converters.ErrorWritingException;

public class ConversionException
extends ErrorWritingException {
    public ConversionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ConversionException(String msg) {
        super(msg);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }
}

