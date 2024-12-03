/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFRenderer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public class PDFPaint {
    private Paint mainPaint;

    protected PDFPaint(Paint p) {
        this.mainPaint = p;
    }

    public static PDFPaint getColorPaint(Color c) {
        return PDFPaint.getPaint(c);
    }

    public static PDFPaint getPaint(Paint p) {
        return new PDFPaint(p);
    }

    public Rectangle2D fill(PDFRenderer state, Graphics2D g, GeneralPath s) {
        g.setPaint(this.mainPaint);
        g.fill(s);
        return s.createTransformedShape(g.getTransform()).getBounds2D();
    }

    public Paint getPaint() {
        return this.mainPaint;
    }
}

