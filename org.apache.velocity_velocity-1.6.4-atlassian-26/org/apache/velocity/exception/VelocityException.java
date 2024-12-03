/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.exception;

import org.apache.velocity.util.ExceptionUtils;

public class VelocityException
extends RuntimeException {
    private static final long serialVersionUID = 1251243065134956045L;
    private final Throwable wrapped;

    public VelocityException(String exceptionMessage) {
        super(exceptionMessage);
        this.wrapped = null;
    }

    public VelocityException(String exceptionMessage, Throwable wrapped) {
        super(exceptionMessage);
        this.wrapped = wrapped;
        ExceptionUtils.setCause(this, wrapped);
    }

    public VelocityException(Throwable wrapped) {
        this.wrapped = wrapped;
        ExceptionUtils.setCause(this, wrapped);
    }

    public Throwable getWrappedThrowable() {
        return this.wrapped;
    }
}

