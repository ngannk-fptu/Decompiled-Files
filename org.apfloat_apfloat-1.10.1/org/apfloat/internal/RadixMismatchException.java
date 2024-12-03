/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.ApfloatInternalException;

public class RadixMismatchException
extends ApfloatInternalException {
    private static final long serialVersionUID = -7022924635011038776L;

    public RadixMismatchException() {
    }

    public RadixMismatchException(String message) {
        super(message);
    }

    public RadixMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}

