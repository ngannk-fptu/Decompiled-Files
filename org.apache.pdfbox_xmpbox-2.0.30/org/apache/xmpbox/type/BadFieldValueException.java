/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

public class BadFieldValueException
extends Exception {
    private static final long serialVersionUID = 8100277682314632644L;

    public BadFieldValueException(String message) {
        super(message);
    }

    public BadFieldValueException(String message, Throwable cause) {
        super(message, cause);
    }
}

