/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.scanning;

public class ScannerException
extends RuntimeException {
    public ScannerException() {
    }

    public ScannerException(String message) {
        super(message);
    }

    public ScannerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScannerException(Throwable cause) {
        super(cause);
    }
}

