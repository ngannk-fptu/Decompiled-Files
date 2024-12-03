/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;

class PdfFont
implements Comparable {
    private BaseFont font;
    private float size;
    protected Image image;
    protected float hScale = 1.0f;

    PdfFont(BaseFont bf, float size) {
        this.size = size;
        this.font = bf;
    }

    public int compareTo(Object object) {
        if (this.image != null) {
            return 0;
        }
        if (object == null) {
            return -1;
        }
        try {
            PdfFont pdfFont = (PdfFont)object;
            if (this.font != pdfFont.font) {
                return 1;
            }
            if (this.size() != pdfFont.size()) {
                return 2;
            }
            return 0;
        }
        catch (ClassCastException cce) {
            return -2;
        }
    }

    float size() {
        if (this.image == null) {
            return this.size;
        }
        return this.image.getScaledHeight();
    }

    float width() {
        return this.width(32);
    }

    float width(int character) {
        if (this.image == null) {
            return this.font.getWidthPoint(character, this.size) * this.hScale;
        }
        return this.image.getScaledWidth();
    }

    float width(String s) {
        if (this.image == null) {
            return this.font.getWidthPoint(s, this.size) * this.hScale;
        }
        return this.image.getScaledWidth();
    }

    BaseFont getFont() {
        return this.font;
    }

    void setImage(Image image) {
        this.image = image;
    }

    static PdfFont getDefaultFont() {
        try {
            BaseFont bf = BaseFont.createFont("Helvetica", "Cp1252", false);
            return new PdfFont(bf, 12.0f);
        }
        catch (Exception ee) {
            throw new ExceptionConverter(ee);
        }
    }

    void setHorizontalScaling(float hScale) {
        this.hScale = hScale;
    }
}

