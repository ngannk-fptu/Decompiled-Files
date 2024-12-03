/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFRenderer;
import java.awt.geom.Rectangle2D;

public abstract class PDFCmd {
    public abstract Rectangle2D execute(PDFRenderer var1);

    public String toString() {
        String name = this.getClass().getName();
        int lastDot = name.lastIndexOf(46);
        if (lastDot >= 0) {
            return name.substring(lastDot + 1);
        }
        return name;
    }

    public String getDetails() {
        return super.toString();
    }
}

