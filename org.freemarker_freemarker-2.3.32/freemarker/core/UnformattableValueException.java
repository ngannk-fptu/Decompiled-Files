/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateValueFormatException;

public class UnformattableValueException
extends TemplateValueFormatException {
    public UnformattableValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnformattableValueException(String message) {
        super(message);
    }
}

