/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.PDFRenderer;
import java.awt.geom.Rectangle2D;

class PDFStrokePaintCmd
extends PDFCmd {
    PDFPaint p;

    public PDFStrokePaintCmd(PDFPaint p) {
        this.p = p;
    }

    @Override
    public Rectangle2D execute(PDFRenderer state) {
        state.setStrokePaint(this.p);
        return null;
    }
}

