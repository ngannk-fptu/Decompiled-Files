/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.ApfloatInternalException;

public class BackingStorageException
extends ApfloatInternalException {
    private static final long serialVersionUID = -7022924635011038776L;

    public BackingStorageException() {
    }

    public BackingStorageException(String message) {
        super(message);
    }

    public BackingStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

