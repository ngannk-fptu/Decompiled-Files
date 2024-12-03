/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser;

public class ParseException
extends RuntimeException {
    private static final long serialVersionUID = -2586758177341912116L;

    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}

