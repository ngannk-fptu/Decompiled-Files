/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations;

public final class DigesterLoadingException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DigesterLoadingException(String message) {
        super(message);
    }

    public DigesterLoadingException(Throwable cause) {
        super(cause);
    }

    public DigesterLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}

