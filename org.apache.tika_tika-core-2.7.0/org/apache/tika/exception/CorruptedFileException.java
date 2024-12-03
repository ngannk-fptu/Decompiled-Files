/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.exception;

import org.apache.tika.exception.TikaException;

public class CorruptedFileException
extends TikaException {
    public CorruptedFileException(String msg) {
        super(msg);
    }

    public CorruptedFileException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

