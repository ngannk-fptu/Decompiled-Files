/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.extra.flyingpdf.util.PdfPageProcessor;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Internal
public class PdfPostProcessor {
    private final String inputFile;
    private final String outputFile;
    private final List<PdfPageProcessor> pageProcessors;

    public PdfPostProcessor(String inputFile, String outputFile, List<PdfPageProcessor> pageProcessors) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.pageProcessors = pageProcessors;
    }

    public void run() throws IOException, DocumentException {
        PdfReader reader = new PdfReader(this.inputFile);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(this.outputFile));
        this.processPdf(reader, stamper);
        stamper.close();
        reader.close();
    }

    private void processPdf(PdfReader reader, PdfStamper stamper) {
        for (int page = 1; page <= reader.getNumberOfPages(); ++page) {
            if (page > 1) {
                reader.releasePage(page - 1);
            }
            for (PdfPageProcessor processor : this.pageProcessors) {
                processor.processPage(reader, stamper, page);
            }
        }
    }
}

