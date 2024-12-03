/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class PdfPSXObject
extends PdfTemplate {
    protected PdfPSXObject() {
    }

    public PdfPSXObject(PdfWriter wr) {
        super(wr);
    }

    @Override
    PdfStream getFormXObject(int compressionLevel) {
        PdfStream s = new PdfStream(this.content.toByteArray());
        s.put(PdfName.TYPE, PdfName.XOBJECT);
        s.put(PdfName.SUBTYPE, PdfName.PS);
        s.flateCompress(compressionLevel);
        return s;
    }

    @Override
    public PdfContentByte getDuplicate() {
        PdfPSXObject tpl = new PdfPSXObject();
        tpl.writer = this.writer;
        tpl.pdf = this.pdf;
        tpl.thisReference = this.thisReference;
        tpl.pageResources = this.pageResources;
        tpl.separator = this.separator;
        return tpl;
    }
}

