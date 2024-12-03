/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

public class DocumentException
extends RuntimeException {
    private static final long serialVersionUID = -2191131489390840739L;

    public DocumentException(Exception ex) {
        super(ex);
    }

    public DocumentException() {
    }

    public DocumentException(String message) {
        super(message);
    }
}

