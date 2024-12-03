/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfObject;

public class PdfNull
extends PdfObject {
    public static final PdfNull PDFNULL = new PdfNull();
    private static final String CONTENT = "null";

    public PdfNull() {
        super(8, CONTENT);
    }

    @Override
    public String toString() {
        return CONTENT;
    }
}

