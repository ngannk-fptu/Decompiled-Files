/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.ApfloatInternalException;

public class ImplementationMismatchException
extends ApfloatInternalException {
    private static final long serialVersionUID = -7022924635011038776L;

    public ImplementationMismatchException() {
    }

    public ImplementationMismatchException(String message) {
        super(message);
    }

    public ImplementationMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}

