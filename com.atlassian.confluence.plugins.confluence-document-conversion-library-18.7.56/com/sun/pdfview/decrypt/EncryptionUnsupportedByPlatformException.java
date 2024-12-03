/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decrypt;

import com.sun.pdfview.decrypt.UnsupportedEncryptionException;

public class EncryptionUnsupportedByPlatformException
extends UnsupportedEncryptionException {
    public EncryptionUnsupportedByPlatformException(String message) {
        super(message);
    }

    public EncryptionUnsupportedByPlatformException(String message, Throwable cause) {
        super(message, cause);
    }
}

