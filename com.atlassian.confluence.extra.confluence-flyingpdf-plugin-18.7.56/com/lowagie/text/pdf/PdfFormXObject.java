/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfTemplate;

public class PdfFormXObject
extends PdfStream {
    public static final PdfNumber ZERO = new PdfNumber(0);
    public static final PdfNumber ONE = new PdfNumber(1);
    public static final PdfLiteral MATRIX = new PdfLiteral("[1 0 0 1 0 0]");

    PdfFormXObject(PdfTemplate template, int compressionLevel) {
        PdfArray matrix;
        this.put(PdfName.TYPE, PdfName.XOBJECT);
        this.put(PdfName.SUBTYPE, PdfName.FORM);
        this.put(PdfName.RESOURCES, template.getResources());
        this.put(PdfName.BBOX, new PdfRectangle(template.getBoundingBox()));
        this.put(PdfName.FORMTYPE, ONE);
        if (template.getLayer() != null) {
            this.put(PdfName.OC, template.getLayer().getRef());
        }
        if (template.getGroup() != null) {
            this.put(PdfName.GROUP, template.getGroup());
        }
        if ((matrix = template.getMatrix()) == null) {
            this.put(PdfName.MATRIX, MATRIX);
        } else {
            this.put(PdfName.MATRIX, matrix);
        }
        this.bytes = template.toPdf(null);
        this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
        this.flateCompress(compressionLevel);
    }
}

