/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.draw;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.draw.LineSeparator;

public class DottedLineSeparator
extends LineSeparator {
    protected float gap = 5.0f;

    @Override
    public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y) {
        canvas.saveState();
        canvas.setLineWidth(this.lineWidth);
        canvas.setLineCap(1);
        canvas.setLineDash(0.0f, this.gap, this.gap / 2.0f);
        this.drawLine(canvas, llx, urx, y);
        canvas.restoreState();
    }

    public float getGap() {
        return this.gap;
    }

    public void setGap(float gap) {
        this.gap = gap;
    }
}

