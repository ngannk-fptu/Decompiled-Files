/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import org.apfloat.ApfloatRuntimeException;

public class InfiniteExpansionException
extends ApfloatRuntimeException {
    private static final long serialVersionUID = -7022924635011038776L;

    public InfiniteExpansionException() {
    }

    public InfiniteExpansionException(String message) {
        super(message);
    }

    public InfiniteExpansionException(String message, Throwable cause) {
        super(message, cause);
    }
}

