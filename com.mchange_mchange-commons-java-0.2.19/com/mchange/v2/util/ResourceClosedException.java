/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.util;

import com.mchange.v2.lang.VersionUtils;

public class ResourceClosedException
extends RuntimeException {
    Throwable rootCause;

    public ResourceClosedException(String string, Throwable throwable) {
        super(string);
        this.setRootCause(throwable);
    }

    public ResourceClosedException(Throwable throwable) {
        this.setRootCause(throwable);
    }

    public ResourceClosedException(String string) {
        super(string);
    }

    public ResourceClosedException() {
    }

    @Override
    public Throwable getCause() {
        return this.rootCause;
    }

    private void setRootCause(Throwable throwable) {
        this.rootCause = throwable;
        if (VersionUtils.isAtLeastJavaVersion14()) {
            this.initCause(throwable);
        }
    }
}

