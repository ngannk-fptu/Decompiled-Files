/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import org.apfloat.ApfloatRuntimeException;

public class OverflowException
extends ApfloatRuntimeException {
    private static final long serialVersionUID = -7022924635011038776L;

    public OverflowException() {
    }

    public OverflowException(String message) {
        super(message);
    }

    public OverflowException(String message, Throwable cause) {
        super(message, cause);
    }
}

