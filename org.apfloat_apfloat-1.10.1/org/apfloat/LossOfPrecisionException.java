/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import org.apfloat.ApfloatRuntimeException;

public class LossOfPrecisionException
extends ApfloatRuntimeException {
    private static final long serialVersionUID = -7022924635011038776L;

    public LossOfPrecisionException() {
    }

    public LossOfPrecisionException(String message) {
        super(message);
    }

    public LossOfPrecisionException(String message, Throwable cause) {
        super(message, cause);
    }
}

