/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import java.io.IOException;

public class PDFParseException
extends IOException {
    public PDFParseException(String msg) {
        super(msg);
    }

    public PDFParseException(String msg, Throwable cause) {
        this(msg);
        this.initCause(cause);
    }
}

