/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;

public class ApfloatInternalException
extends ApfloatRuntimeException {
    private static final long serialVersionUID = -7022924635011038776L;

    public ApfloatInternalException() {
    }

    public ApfloatInternalException(String message) {
        super(message);
    }

    public ApfloatInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}

