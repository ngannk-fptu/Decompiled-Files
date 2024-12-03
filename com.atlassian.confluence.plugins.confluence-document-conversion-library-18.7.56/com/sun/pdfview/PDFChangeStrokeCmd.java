/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFRenderer;
import java.awt.geom.Rectangle2D;

class PDFChangeStrokeCmd
extends PDFCmd {
    float w = -1000.0f;
    float limit = -1000.0f;
    float phase = -1000.0f;
    int cap = -1000;
    int join = -1000;
    float[] ary = PDFRenderer.NODASH;

    public void setWidth(float w) {
        this.w = w;
    }

    public void setEndCap(int cap) {
        this.cap = cap;
    }

    public void setLineJoin(int join) {
        this.join = join;
    }

    public void setMiterLimit(float limit) {
        this.limit = limit;
    }

    public void setDash(float[] ary, float phase) {
        this.ary = ary;
        this.phase = phase;
    }

    @Override
    public Rectangle2D execute(PDFRenderer state) {
        state.setStrokeParts(this.w, this.cap, this.join, this.limit, this.ary, this.phase);
        return null;
    }

    public String toString(PDFRenderer state) {
        return "STROKE: w=" + this.w + " cap=" + this.cap + " join=" + this.join + " limit=" + this.limit + " ary=" + this.ary + " phase=" + this.phase;
    }
}

