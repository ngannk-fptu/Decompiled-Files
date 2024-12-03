/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;

public class PdfGState
extends PdfDictionary {
    public static final PdfName BM_NORMAL = new PdfName("Normal");
    public static final PdfName BM_COMPATIBLE = new PdfName("Compatible");
    public static final PdfName BM_MULTIPLY = new PdfName("Multiply");
    public static final PdfName BM_SCREEN = new PdfName("Screen");
    public static final PdfName BM_OVERLAY = new PdfName("Overlay");
    public static final PdfName BM_DARKEN = new PdfName("Darken");
    public static final PdfName BM_LIGHTEN = new PdfName("Lighten");
    public static final PdfName BM_COLORDODGE = new PdfName("ColorDodge");
    public static final PdfName BM_COLORBURN = new PdfName("ColorBurn");
    public static final PdfName BM_HARDLIGHT = new PdfName("HardLight");
    public static final PdfName BM_SOFTLIGHT = new PdfName("SoftLight");
    public static final PdfName BM_DIFFERENCE = new PdfName("Difference");
    public static final PdfName BM_EXCLUSION = new PdfName("Exclusion");

    public void setOverPrintStroking(boolean ov) {
        this.put(PdfName.OP, ov ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setOverPrintNonStroking(boolean ov) {
        this.put(PdfName.op, ov ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setOverPrintMode(int ov) {
        this.put(PdfName.OPM, new PdfNumber(ov == 0 ? 0 : 1));
    }

    public void setStrokeOpacity(float n) {
        this.put(PdfName.CA, new PdfNumber(n));
    }

    public void setFillOpacity(float n) {
        this.put(PdfName.ca, new PdfNumber(n));
    }

    public void setAlphaIsShape(boolean v) {
        this.put(PdfName.AIS, v ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setTextKnockout(boolean v) {
        this.put(PdfName.TK, v ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setBlendMode(PdfName bm) {
        this.put(PdfName.BM, bm);
    }
}

