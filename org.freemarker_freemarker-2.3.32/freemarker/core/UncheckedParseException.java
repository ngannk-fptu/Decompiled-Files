/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ParseException;

final class UncheckedParseException
extends RuntimeException {
    private final ParseException parseException;

    public UncheckedParseException(ParseException parseException) {
        this.parseException = parseException;
    }

    public ParseException getParseException() {
        return this.parseException;
    }
}

