/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.ex;

import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;

public class ConversionException
extends ConfigurationRuntimeException {
    private static final long serialVersionUID = -5167943099293540392L;

    public ConversionException() {
    }

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}

