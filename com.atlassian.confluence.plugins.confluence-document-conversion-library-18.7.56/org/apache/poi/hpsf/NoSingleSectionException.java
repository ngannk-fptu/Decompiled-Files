/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.poi.hpsf.HPSFRuntimeException;

public class NoSingleSectionException
extends HPSFRuntimeException {
    public NoSingleSectionException() {
    }

    public NoSingleSectionException(String msg) {
        super(msg);
    }

    public NoSingleSectionException(Throwable reason) {
        super(reason);
    }

    public NoSingleSectionException(String msg, Throwable reason) {
        super(msg, reason);
    }
}

