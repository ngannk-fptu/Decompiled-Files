/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.ole2;

import java.io.IOException;

public class CorruptDocumentException
extends IOException {
    public CorruptDocumentException() {
        this("Corrupt OLE 2 Compound Document");
    }

    public CorruptDocumentException(String string) {
        super(string);
    }

    public CorruptDocumentException(Throwable throwable) {
        super(throwable.getMessage());
        this.initCause(throwable);
    }
}

