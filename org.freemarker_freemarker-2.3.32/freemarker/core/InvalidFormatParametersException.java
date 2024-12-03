/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.InvalidFormatStringException;

public final class InvalidFormatParametersException
extends InvalidFormatStringException {
    public InvalidFormatParametersException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFormatParametersException(String message) {
        this(message, null);
    }
}

