/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.poi.hpsf.HPSFException;

public class NoPropertySetStreamException
extends HPSFException {
    public NoPropertySetStreamException() {
    }

    public NoPropertySetStreamException(String msg) {
        super(msg);
    }

    public NoPropertySetStreamException(Throwable reason) {
        super(reason);
    }

    public NoPropertySetStreamException(String msg, Throwable reason) {
        super(msg, reason);
    }
}

