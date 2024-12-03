/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import org.apfloat.ApfloatRuntimeException;

public class ApfloatConfigurationException
extends ApfloatRuntimeException {
    private static final long serialVersionUID = -7022924635011038776L;

    public ApfloatConfigurationException() {
    }

    public ApfloatConfigurationException(String message) {
        super(message);
    }

    public ApfloatConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

