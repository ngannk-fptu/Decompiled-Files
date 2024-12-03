/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.multipdf;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PageExtractor {
    private static final Log LOG = LogFactory.getLog(PageExtractor.class);
    private final PDDocument sourceDocument;
    private int startPage = 1;
    private int endPage;

    public PageExtractor(PDDocument sourceDocument) {
        this.sourceDocument = sourceDocument;
        this.endPage = sourceDocument.getNumberOfPages();
    }

    public PageExtractor(PDDocument sourceDocument, int startPage, int endPage) {
        this.sourceDocument = sourceDocument;
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public PDDocument extract() throws IOException {
        PDDocument extractedDocument = new PDDocument();
        extractedDocument.setDocumentInformation(this.sourceDocument.getDocumentInformation());
        extractedDocument.getDocumentCatalog().setViewerPreferences(this.sourceDocument.getDocumentCatalog().getViewerPreferences());
        for (int i = this.startPage; i <= this.endPage; ++i) {
            PDPage page = this.sourceDocument.getPage(i - 1);
            PDPage imported = extractedDocument.importPage(page);
            if (page.getResources() == null || page.getCOSObject().containsKey(COSName.RESOURCES)) continue;
            imported.setResources(page.getResources());
            LOG.info((Object)"Done in PageExtractor");
        }
        return extractedDocument;
    }

    public int getStartPage() {
        return this.startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return this.endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }
}

