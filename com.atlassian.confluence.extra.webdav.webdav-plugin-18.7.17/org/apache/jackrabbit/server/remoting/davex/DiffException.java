/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.remoting.davex;

import java.io.IOException;

class DiffException
extends IOException {
    private Throwable cause;

    public DiffException(String message) {
        super(message);
    }

    public DiffException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

