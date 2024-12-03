/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.ApfloatInternalException;

public class TransformLengthExceededException
extends ApfloatInternalException {
    private static final long serialVersionUID = -7022924635011038776L;

    public TransformLengthExceededException() {
    }

    public TransformLengthExceededException(String message) {
        super(message);
    }

    public TransformLengthExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}

