/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.PdfSpotColor;

public class SpotColor
extends ExtendedColor {
    private static final long serialVersionUID = -6257004582113248079L;
    PdfSpotColor spot;
    float tint;

    public SpotColor(PdfSpotColor spot, float tint) {
        super(3, ((float)spot.getAlternativeCS().getRed() / 255.0f - 1.0f) * tint + 1.0f, ((float)spot.getAlternativeCS().getGreen() / 255.0f - 1.0f) * tint + 1.0f, ((float)spot.getAlternativeCS().getBlue() / 255.0f - 1.0f) * tint + 1.0f);
        this.spot = spot;
        this.tint = tint;
    }

    public PdfSpotColor getPdfSpotColor() {
        return this.spot;
    }

    public float getTint() {
        return this.tint;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return this.spot.hashCode() ^ Float.floatToIntBits(this.tint);
    }
}

