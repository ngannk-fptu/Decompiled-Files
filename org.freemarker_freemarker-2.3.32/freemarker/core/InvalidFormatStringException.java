/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateValueFormatException;

public abstract class InvalidFormatStringException
extends TemplateValueFormatException {
    public InvalidFormatStringException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFormatStringException(String message) {
        this(message, null);
    }
}

