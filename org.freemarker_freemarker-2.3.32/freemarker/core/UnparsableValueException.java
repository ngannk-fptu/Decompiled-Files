/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateValueFormatException;

public class UnparsableValueException
extends TemplateValueFormatException {
    public UnparsableValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnparsableValueException(String message) {
        this(message, null);
    }
}

