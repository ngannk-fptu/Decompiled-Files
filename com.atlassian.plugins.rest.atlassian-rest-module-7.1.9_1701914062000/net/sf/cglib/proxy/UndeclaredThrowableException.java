/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import net.sf.cglib.core.CodeGenerationException;

public class UndeclaredThrowableException
extends CodeGenerationException {
    public UndeclaredThrowableException(Throwable t) {
        super(t);
    }

    public Throwable getUndeclaredThrowable() {
        return this.getCause();
    }
}

