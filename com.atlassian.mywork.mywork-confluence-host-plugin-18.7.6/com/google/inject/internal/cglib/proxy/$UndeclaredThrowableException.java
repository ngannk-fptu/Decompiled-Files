/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.core.$CodeGenerationException;

public class $UndeclaredThrowableException
extends $CodeGenerationException {
    public $UndeclaredThrowableException(Throwable t) {
        super(t);
    }

    public Throwable getUndeclaredThrowable() {
        return this.getCause();
    }
}

