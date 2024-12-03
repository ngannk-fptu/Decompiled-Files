/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFRenderer;
import java.awt.geom.Rectangle2D;

class PDFStrokeAlphaCmd
extends PDFCmd {
    float a;

    public PDFStrokeAlphaCmd(float a) {
        this.a = a;
    }

    @Override
    public Rectangle2D execute(PDFRenderer state) {
        state.setStrokeAlpha(this.a);
        return null;
    }
}

