/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.poi.hpsf.HPSFRuntimeException;

public class NoFormatIDException
extends HPSFRuntimeException {
    public NoFormatIDException() {
    }

    public NoFormatIDException(String msg) {
        super(msg);
    }

    public NoFormatIDException(Throwable reason) {
        super(reason);
    }

    public NoFormatIDException(String msg, Throwable reason) {
        super(msg, reason);
    }
}

