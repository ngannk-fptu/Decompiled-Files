/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.InvalidFormatStringException;

public class UndefinedCustomFormatException
extends InvalidFormatStringException {
    public UndefinedCustomFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndefinedCustomFormatException(String message) {
        super(message);
    }
}

