/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.rest;

import java.io.IOException;

public class RuntimeIOException
extends RuntimeException {
    public RuntimeIOException(IOException e) {
        super(e);
    }

    @Override
    public synchronized IOException getCause() {
        return (IOException)super.getCause();
    }
}

