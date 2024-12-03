/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFRenderer;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

class PDFXformCmd
extends PDFCmd {
    AffineTransform at;

    public PDFXformCmd(AffineTransform at) {
        if (at == null) {
            throw new RuntimeException("Null transform in PDFXformCmd");
        }
        this.at = at;
    }

    @Override
    public Rectangle2D execute(PDFRenderer state) {
        state.transform(this.at);
        return null;
    }

    public String toString(PDFRenderer state) {
        return "PDFXformCmd: " + this.at;
    }

    @Override
    public String getDetails() {
        StringBuffer buf = new StringBuffer();
        buf.append("PDFXformCommand: \n");
        buf.append(this.at.toString());
        return buf.toString();
    }
}

