/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFRenderer;
import java.awt.geom.Rectangle2D;

class PDFPushCmd
extends PDFCmd {
    PDFPushCmd() {
    }

    @Override
    public Rectangle2D execute(PDFRenderer state) {
        state.push();
        return null;
    }
}

