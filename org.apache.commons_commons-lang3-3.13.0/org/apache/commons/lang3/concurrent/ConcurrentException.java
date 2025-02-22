/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.concurrent;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

public class ConcurrentException
extends Exception {
    private static final long serialVersionUID = 6622707671812226130L;

    protected ConcurrentException() {
    }

    public ConcurrentException(Throwable cause) {
        super(ConcurrentUtils.checkedException(cause));
    }

    public ConcurrentException(String msg, Throwable cause) {
        super(msg, ConcurrentUtils.checkedException(cause));
    }
}

