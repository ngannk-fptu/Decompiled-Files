/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.lang.PotentiallySecondaryException;

public class UnreliableIteratorException
extends PotentiallySecondaryException {
    public UnreliableIteratorException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public UnreliableIteratorException(Throwable throwable) {
        super(throwable);
    }

    public UnreliableIteratorException(String string) {
        super(string);
    }

    public UnreliableIteratorException() {
    }
}

