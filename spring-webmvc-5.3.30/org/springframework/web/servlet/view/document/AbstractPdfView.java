/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.lowagie.text.Document
 *  com.lowagie.text.DocumentException
 *  com.lowagie.text.PageSize
 *  com.lowagie.text.pdf.PdfWriter
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.view.document;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractPdfView
extends AbstractView {
    public AbstractPdfView() {
        this.setContentType("application/pdf");
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream baos = this.createTemporaryOutputStream();
        Document document = this.newDocument();
        PdfWriter writer = this.newWriter(document, baos);
        this.prepareWriter(model, writer, request);
        this.buildPdfMetadata(model, document, request);
        document.open();
        this.buildPdfDocument(model, document, writer, request, response);
        document.close();
        this.writeToResponse(response, baos);
    }

    protected Document newDocument() {
        return new Document(PageSize.A4);
    }

    protected PdfWriter newWriter(Document document, OutputStream os) throws DocumentException {
        return PdfWriter.getInstance((Document)document, (OutputStream)os);
    }

    protected void prepareWriter(Map<String, Object> model, PdfWriter writer, HttpServletRequest request) throws DocumentException {
        writer.setViewerPreferences(this.getViewerPreferences());
    }

    protected int getViewerPreferences() {
        return 2053;
    }

    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
    }

    protected abstract void buildPdfDocument(Map<String, Object> var1, Document var2, PdfWriter var3, HttpServletRequest var4, HttpServletResponse var5) throws Exception;
}

