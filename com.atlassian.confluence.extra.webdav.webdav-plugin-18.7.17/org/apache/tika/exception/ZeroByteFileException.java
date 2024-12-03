/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.exception;

import org.apache.tika.exception.TikaException;

public class ZeroByteFileException
extends TikaException {
    public static IgnoreZeroByteFileException IGNORE_ZERO_BYTE_FILE_EXCEPTION = new IgnoreZeroByteFileException();

    public ZeroByteFileException(String msg) {
        super(msg);
    }

    public static class IgnoreZeroByteFileException {
    }
}

