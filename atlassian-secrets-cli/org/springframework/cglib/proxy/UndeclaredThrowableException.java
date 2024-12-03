/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.proxy;

import org.springframework.cglib.core.CodeGenerationException;

public class UndeclaredThrowableException
extends CodeGenerationException {
    public UndeclaredThrowableException(Throwable t) {
        super(t);
    }

    public Throwable getUndeclaredThrowable() {
        return this.getCause();
    }
}

