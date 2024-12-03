/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFRenderer;
import java.awt.geom.Rectangle2D;

class PDFImageCmd
extends PDFCmd {
    PDFImage image;

    public PDFImageCmd(PDFImage image) {
        this.image = image;
    }

    @Override
    public Rectangle2D execute(PDFRenderer state) {
        return state.drawImage(this.image);
    }
}

