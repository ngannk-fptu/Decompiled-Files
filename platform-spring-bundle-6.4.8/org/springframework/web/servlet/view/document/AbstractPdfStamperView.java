/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.lowagie.text.pdf.PdfReader
 *  com.lowagie.text.pdf.PdfStamper
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.view.document;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public abstract class AbstractPdfStamperView
extends AbstractUrlBasedView {
    public AbstractPdfStamperView() {
        this.setContentType("application/pdf");
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream baos = this.createTemporaryOutputStream();
        PdfReader reader = this.readPdfResource();
        PdfStamper stamper = new PdfStamper(reader, (OutputStream)baos);
        this.mergePdfDocument(model, stamper, request, response);
        stamper.close();
        this.writeToResponse(response, baos);
    }

    protected PdfReader readPdfResource() throws IOException {
        String url = this.getUrl();
        Assert.state(url != null, "'url' not set");
        return new PdfReader(this.obtainApplicationContext().getResource(url).getInputStream());
    }

    protected abstract void mergePdfDocument(Map<String, Object> var1, PdfStamper var2, HttpServletRequest var3, HttpServletResponse var4) throws Exception;
}

