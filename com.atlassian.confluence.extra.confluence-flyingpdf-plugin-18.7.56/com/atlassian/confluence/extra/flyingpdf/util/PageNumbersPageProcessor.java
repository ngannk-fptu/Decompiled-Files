/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.extra.flyingpdf.util.PdfPageProcessor;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

@Internal
public class PageNumbersPageProcessor
implements PdfPageProcessor {
    @Override
    public void processPage(PdfReader reader, PdfStamper stamper, int page) {
        float xPos = (reader.getPageSize(page).getRight() - reader.getPageSize(page).getLeft()) / 2.0f;
        PdfContentByte pdfContentByte = stamper.getOverContent(page);
        ColumnText.showTextAligned(pdfContentByte, 1, new Phrase(String.valueOf(page), FontFactory.getFont("Helvetica", 8.0f)), xPos, 35.0f, 0.0f);
    }
}

