/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDashPattern;
import com.lowagie.text.pdf.PdfNumber;

public class PdfBorderArray
extends PdfArray {
    public PdfBorderArray(float hRadius, float vRadius, float width) {
        this(hRadius, vRadius, width, null);
    }

    public PdfBorderArray(float hRadius, float vRadius, float width, PdfDashPattern dash) {
        super(new PdfNumber(hRadius));
        this.add(new PdfNumber(vRadius));
        this.add(new PdfNumber(width));
        if (dash != null) {
            this.add(dash);
        }
    }
}

