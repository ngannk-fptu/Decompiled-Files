/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.exception;

import org.apache.tika.exception.TikaException;

public class TikaMemoryLimitException
extends TikaException {
    public TikaMemoryLimitException(String msg) {
        super(msg);
    }

    public TikaMemoryLimitException(long triedToAllocate, long maxAllowable) {
        super(TikaMemoryLimitException.msg(triedToAllocate, maxAllowable));
    }

    private static String msg(long triedToAllocate, long maxAllowable) {
        return "Tried to allocate " + triedToAllocate + " bytes, but " + maxAllowable + " is the maximum allowed. Please open an issue https://issues.apache.org/jira/projects/TIKA if you believe this file is not corrupt.";
    }
}

