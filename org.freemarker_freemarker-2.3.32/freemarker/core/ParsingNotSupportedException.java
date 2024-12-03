/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateValueFormatException;

public class ParsingNotSupportedException
extends TemplateValueFormatException {
    public ParsingNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsingNotSupportedException(String message) {
        this(message, null);
    }
}

