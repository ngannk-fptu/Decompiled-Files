/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;

public class PdfTransparencyGroup
extends PdfDictionary {
    public PdfTransparencyGroup() {
        this.put(PdfName.S, PdfName.TRANSPARENCY);
    }

    public void setIsolated(boolean isolated) {
        if (isolated) {
            this.put(PdfName.I, PdfBoolean.PDFTRUE);
        } else {
            this.remove(PdfName.I);
        }
    }

    public void setKnockout(boolean knockout) {
        if (knockout) {
            this.put(PdfName.K, PdfBoolean.PDFTRUE);
        } else {
            this.remove(PdfName.K);
        }
    }
}

