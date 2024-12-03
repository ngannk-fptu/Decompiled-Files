/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.poi.hpsf.HPSFException;

public class UnexpectedPropertySetTypeException
extends HPSFException {
    public UnexpectedPropertySetTypeException() {
    }

    public UnexpectedPropertySetTypeException(String msg) {
        super(msg);
    }

    public UnexpectedPropertySetTypeException(Throwable reason) {
        super(reason);
    }

    public UnexpectedPropertySetTypeException(String msg, Throwable reason) {
        super(msg, reason);
    }
}

