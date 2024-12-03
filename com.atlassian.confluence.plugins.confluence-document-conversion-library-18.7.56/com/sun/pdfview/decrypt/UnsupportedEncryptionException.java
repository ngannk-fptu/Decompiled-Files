/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decrypt;

public abstract class UnsupportedEncryptionException
extends Exception {
    protected UnsupportedEncryptionException(String message) {
        super(message);
    }

    protected UnsupportedEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

