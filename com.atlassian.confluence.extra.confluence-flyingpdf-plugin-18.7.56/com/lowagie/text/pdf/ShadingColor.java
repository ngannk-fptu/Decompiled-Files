/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.PdfShadingPattern;

public class ShadingColor
extends ExtendedColor {
    private static final long serialVersionUID = 4817929454941328671L;
    PdfShadingPattern shadingPattern;

    public ShadingColor(PdfShadingPattern shadingPattern) {
        super(5, 0.5f, 0.5f, 0.5f);
        this.shadingPattern = shadingPattern;
    }

    public PdfShadingPattern getPdfShadingPattern() {
        return this.shadingPattern;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return this.shadingPattern.hashCode();
    }
}

