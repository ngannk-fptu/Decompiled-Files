/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.poi.hpsf.HPSFRuntimeException;

public class MissingSectionException
extends HPSFRuntimeException {
    public MissingSectionException() {
    }

    public MissingSectionException(String msg) {
        super(msg);
    }

    public MissingSectionException(Throwable reason) {
        super(reason);
    }

    public MissingSectionException(String msg, Throwable reason) {
        super(msg, reason);
    }
}

