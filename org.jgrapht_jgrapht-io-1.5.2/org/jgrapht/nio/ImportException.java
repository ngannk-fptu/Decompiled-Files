/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.nio;

public class ImportException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ImportException() {
    }

    public ImportException(String message) {
        super(message);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportException(Throwable cause) {
        super(cause);
    }
}

