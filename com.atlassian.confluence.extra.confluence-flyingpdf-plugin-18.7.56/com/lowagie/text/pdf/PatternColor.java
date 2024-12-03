/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.PdfPatternPainter;

public class PatternColor
extends ExtendedColor {
    private static final long serialVersionUID = -1185448552860615964L;
    PdfPatternPainter painter;

    public PatternColor(PdfPatternPainter painter) {
        super(4, 0.5f, 0.5f, 0.5f);
        this.painter = painter;
    }

    public PdfPatternPainter getPainter() {
        return this.painter;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return this.painter.hashCode();
    }
}

