/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.rototor.pdfbox.graphics2d.IPdfBoxGraphics2DFontTextDrawer
 *  de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D
 *  de.rototor.pdfbox.graphics2d.PdfBoxGraphics2DFontTextDrawer
 *  org.apache.pdfbox.pdmodel.PDDocument
 *  org.apache.pdfbox.pdmodel.PDPage
 *  org.apache.pdfbox.pdmodel.PDPageContentStream
 *  org.apache.pdfbox.pdmodel.common.PDRectangle
 *  org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
 */
package org.apache.poi.xslf.util;

import de.rototor.pdfbox.graphics2d.IPdfBoxGraphics2DFontTextDrawer;
import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D;
import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2DFontTextDrawer;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.poi.util.Internal;
import org.apache.poi.xslf.util.MFProxy;
import org.apache.poi.xslf.util.OutputFormat;
import org.apache.poi.xslf.util.PDFFontMapper;

@Internal
public class PDFFormat
implements OutputFormat {
    private final PDDocument document;
    private PDPageContentStream contentStream;
    private PdfBoxGraphics2D pdfBoxGraphics2D;
    private PdfBoxGraphics2DFontTextDrawer fontTextDrawer;

    public PDFFormat(boolean textAsShapes, String fontDir, String fontTtf) {
        if (!textAsShapes) {
            this.fontTextDrawer = new PDFFontMapper(fontDir, fontTtf);
        }
        this.document = new PDDocument();
    }

    @Override
    public Graphics2D addSlide(double width, double height) throws IOException {
        PDPage page = new PDPage(new PDRectangle((float)width, (float)height));
        this.document.addPage(page);
        this.contentStream = new PDPageContentStream(this.document, page);
        this.pdfBoxGraphics2D = new PdfBoxGraphics2D(this.document, (float)width, (float)height);
        if (this.fontTextDrawer != null) {
            this.pdfBoxGraphics2D.setFontTextDrawer((IPdfBoxGraphics2DFontTextDrawer)this.fontTextDrawer);
        }
        return this.pdfBoxGraphics2D;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeSlide(MFProxy proxy, File outFile) throws IOException {
        try {
            this.pdfBoxGraphics2D.dispose();
            PDFormXObject appearanceStream = this.pdfBoxGraphics2D.getXFormObject();
            this.contentStream.drawForm(appearanceStream);
        }
        finally {
            this.contentStream.close();
        }
    }

    @Override
    public void writeDocument(MFProxy proxy, File outFile) throws IOException {
        this.document.save(new File(outFile.getCanonicalPath()));
    }

    @Override
    public void close() throws IOException {
        try {
            this.document.close();
        }
        finally {
            if (this.fontTextDrawer != null) {
                this.fontTextDrawer.close();
            }
        }
    }
}

