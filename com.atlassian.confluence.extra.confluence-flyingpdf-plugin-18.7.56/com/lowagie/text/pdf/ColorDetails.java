/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfWriter;

class ColorDetails {
    PdfIndirectReference indirectReference;
    PdfName colorName;
    PdfSpotColor spotcolor;

    ColorDetails(PdfName colorName, PdfIndirectReference indirectReference, PdfSpotColor scolor) {
        this.colorName = colorName;
        this.indirectReference = indirectReference;
        this.spotcolor = scolor;
    }

    PdfIndirectReference getIndirectReference() {
        return this.indirectReference;
    }

    PdfName getColorName() {
        return this.colorName;
    }

    PdfObject getSpotColor(PdfWriter writer) {
        return this.spotcolor.getSpotObject(writer);
    }
}

