/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReaderInstance;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfTransparencyGroup;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;

public class PdfImportedPage
extends PdfTemplate {
    PdfReaderInstance readerInstance;
    int pageNumber;

    PdfImportedPage(PdfReaderInstance readerInstance, PdfWriter writer, int pageNumber) {
        this.readerInstance = readerInstance;
        this.pageNumber = pageNumber;
        this.writer = writer;
        this.bBox = readerInstance.getReader().getPageSize(pageNumber);
        this.setMatrix(1.0f, 0.0f, 0.0f, 1.0f, -this.bBox.getLeft(), -this.bBox.getBottom());
        this.type = 2;
    }

    public PdfImportedPage getFromReader() {
        return this;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    @Override
    public void addImage(Image image, float a, float b, float c, float d, float e, float f) throws DocumentException {
        this.throwError();
    }

    @Override
    public void addTemplate(PdfTemplate template, float a, float b, float c, float d, float e, float f) {
        this.throwError();
    }

    @Override
    public PdfContentByte getDuplicate() {
        this.throwError();
        return null;
    }

    @Override
    PdfStream getFormXObject(int compressionLevel) throws IOException {
        return this.readerInstance.getFormXObject(this.pageNumber, compressionLevel);
    }

    @Override
    public void setColorFill(PdfSpotColor sp, float tint) {
        this.throwError();
    }

    @Override
    public void setColorStroke(PdfSpotColor sp, float tint) {
        this.throwError();
    }

    @Override
    PdfObject getResources() {
        return this.readerInstance.getResources(this.pageNumber);
    }

    @Override
    public void setFontAndSize(BaseFont bf, float size) {
        this.throwError();
    }

    @Override
    public void setGroup(PdfTransparencyGroup group) {
        this.throwError();
    }

    void throwError() {
        throw new RuntimeException(MessageLocalization.getComposedMessage("content.can.not.be.added.to.a.pdfimportedpage"));
    }

    PdfReaderInstance getPdfReaderInstance() {
        return this.readerInstance;
    }
}

