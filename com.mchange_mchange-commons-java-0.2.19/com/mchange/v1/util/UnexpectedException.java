/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.lang.PotentiallySecondaryRuntimeException;

public class UnexpectedException
extends PotentiallySecondaryRuntimeException {
    public UnexpectedException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public UnexpectedException(Throwable throwable) {
        super(throwable);
    }

    public UnexpectedException(String string) {
        super(string);
    }

    public UnexpectedException() {
    }

    public UnexpectedException(Throwable throwable, String string) {
        this(string, throwable);
    }
}

