/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;

public class PdfPattern
extends PdfStream {
    PdfPattern(PdfPatternPainter painter) {
        this(painter, -1);
    }

    PdfPattern(PdfPatternPainter painter, int compressionLevel) {
        PdfNumber one = new PdfNumber(1);
        PdfArray matrix = painter.getMatrix();
        if (matrix != null) {
            this.put(PdfName.MATRIX, matrix);
        }
        this.put(PdfName.TYPE, PdfName.PATTERN);
        this.put(PdfName.BBOX, new PdfRectangle(painter.getBoundingBox()));
        this.put(PdfName.RESOURCES, painter.getResources());
        this.put(PdfName.TILINGTYPE, one);
        this.put(PdfName.PATTERNTYPE, one);
        if (painter.isStencil()) {
            this.put(PdfName.PAINTTYPE, new PdfNumber(2));
        } else {
            this.put(PdfName.PAINTTYPE, one);
        }
        this.put(PdfName.XSTEP, new PdfNumber(painter.getXStep()));
        this.put(PdfName.YSTEP, new PdfNumber(painter.getYStep()));
        this.bytes = painter.toPdf(null);
        this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
        try {
            this.flateCompress(compressionLevel);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
}

