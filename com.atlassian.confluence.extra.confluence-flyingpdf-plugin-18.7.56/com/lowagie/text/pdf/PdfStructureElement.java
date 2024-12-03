/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStructureTreeRoot;

public class PdfStructureElement
extends PdfDictionary {
    private PdfStructureElement parent;
    private PdfStructureTreeRoot top;
    private PdfIndirectReference reference;

    public PdfStructureElement(PdfStructureElement parent, PdfName structureType) {
        this.top = parent.top;
        this.init(parent, structureType);
        this.parent = parent;
        this.put(PdfName.P, parent.reference);
    }

    public PdfStructureElement(PdfStructureTreeRoot parent, PdfName structureType) {
        this.top = parent;
        this.init(parent, structureType);
        this.put(PdfName.P, parent.getReference());
    }

    private void init(PdfDictionary parent, PdfName structureType) {
        PdfObject kido = parent.get(PdfName.K);
        PdfArray kids = null;
        if (kido != null && !kido.isArray()) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.parent.has.already.another.function"));
        }
        if (kido == null) {
            kids = new PdfArray();
            parent.put(PdfName.K, kids);
        } else {
            kids = (PdfArray)kido;
        }
        kids.add(this);
        this.put(PdfName.S, structureType);
        this.reference = this.top.getWriter().getPdfIndirectReference();
    }

    public PdfDictionary getParent() {
        return this.parent;
    }

    void setPageMark(int page, int mark) {
        if (mark >= 0) {
            this.put(PdfName.K, new PdfNumber(mark));
        }
        this.top.setPageMark(page, this.reference);
    }

    public PdfIndirectReference getReference() {
        return this.reference;
    }
}

