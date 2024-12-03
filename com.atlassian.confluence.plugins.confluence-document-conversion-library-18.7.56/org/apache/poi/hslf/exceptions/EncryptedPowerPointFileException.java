/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.exceptions;

import org.apache.poi.EncryptedDocumentException;

public final class EncryptedPowerPointFileException
extends EncryptedDocumentException {
    public EncryptedPowerPointFileException(String s) {
        super(s);
    }

    public EncryptedPowerPointFileException(String s, Throwable t) {
        super(s, t);
    }

    public EncryptedPowerPointFileException(Throwable t) {
        super(t);
    }
}

