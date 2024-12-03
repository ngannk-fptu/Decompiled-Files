/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.exception;

import org.supercsv.exception.SuperCsvException;

public class SuperCsvReflectionException
extends SuperCsvException {
    private static final long serialVersionUID = 1L;

    public SuperCsvReflectionException(String msg) {
        super(msg);
    }

    public SuperCsvReflectionException(String msg, Throwable t) {
        super(msg, null, t);
    }
}

