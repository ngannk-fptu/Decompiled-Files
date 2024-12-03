/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel;

import org.springframework.expression.spel.SpelParseException;

public class InternalParseException
extends RuntimeException {
    public InternalParseException(SpelParseException cause) {
        super(cause);
    }

    @Override
    public SpelParseException getCause() {
        return (SpelParseException)super.getCause();
    }
}

