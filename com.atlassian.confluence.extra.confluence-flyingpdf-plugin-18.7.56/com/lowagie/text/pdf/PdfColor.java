/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfNumber;
import java.awt.Color;

class PdfColor
extends PdfArray {
    PdfColor(int red, int green, int blue) {
        super(new PdfNumber((double)(red & 0xFF) / 255.0));
        this.add(new PdfNumber((double)(green & 0xFF) / 255.0));
        this.add(new PdfNumber((double)(blue & 0xFF) / 255.0));
    }

    PdfColor(Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue());
    }
}

