/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

public class TooManyFailuresException
extends RuntimeException {
    private static final long serialVersionUID = 7986467967127358163L;

    public TooManyFailuresException() {
    }

    public TooManyFailuresException(String message) {
        super(message);
    }

    public TooManyFailuresException(String message, Throwable cause) {
        super(message, cause);
    }
}

