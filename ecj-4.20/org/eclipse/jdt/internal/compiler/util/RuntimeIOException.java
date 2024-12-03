/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.io.IOException;

final class RuntimeIOException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RuntimeIOException(IOException cause) {
        super(cause);
    }

    @Override
    public synchronized IOException getCause() {
        return (IOException)super.getCause();
    }
}

