/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfStamperImp;

public class StampContent
extends PdfContentByte {
    PdfStamperImp.PageStamp ps;
    PageResources pageResources;

    StampContent(PdfStamperImp stamper, PdfStamperImp.PageStamp ps) {
        super(stamper);
        this.ps = ps;
        this.pageResources = ps.pageResources;
    }

    @Override
    public void setAction(PdfAction action, float llx, float lly, float urx, float ury) {
        ((PdfStamperImp)this.writer).addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action), this.ps.pageN);
    }

    @Override
    public PdfContentByte getDuplicate() {
        return new StampContent((PdfStamperImp)this.writer, this.ps);
    }

    @Override
    PageResources getPageResources() {
        return this.pageResources;
    }

    @Override
    void addAnnotation(PdfAnnotation annot) {
        ((PdfStamperImp)this.writer).addAnnotation(annot, this.ps.pageN);
    }
}

