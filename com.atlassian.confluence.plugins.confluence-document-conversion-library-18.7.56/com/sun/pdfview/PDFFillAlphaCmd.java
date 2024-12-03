/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFRenderer;
import java.awt.geom.Rectangle2D;

class PDFFillAlphaCmd
extends PDFCmd {
    float a;

    public PDFFillAlphaCmd(float a) {
        this.a = a;
    }

    @Override
    public Rectangle2D execute(PDFRenderer state) {
        state.setFillAlpha(this.a);
        return null;
    }
}

