/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.multipdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

public class Splitter {
    private static final Log LOG = LogFactory.getLog(Splitter.class);
    private PDDocument sourceDocument;
    private PDDocument currentDestinationDocument;
    private int splitLength = 1;
    private int startPage = Integer.MIN_VALUE;
    private int endPage = Integer.MAX_VALUE;
    private List<PDDocument> destinationDocuments;
    private int currentPageNumber;
    private MemoryUsageSetting memoryUsageSetting = null;

    public MemoryUsageSetting getMemoryUsageSetting() {
        return this.memoryUsageSetting;
    }

    public void setMemoryUsageSetting(MemoryUsageSetting memoryUsageSetting) {
        this.memoryUsageSetting = memoryUsageSetting;
    }

    public List<PDDocument> split(PDDocument document) throws IOException {
        this.currentPageNumber = 0;
        this.destinationDocuments = new ArrayList<PDDocument>();
        this.sourceDocument = document;
        this.processPages();
        return this.destinationDocuments;
    }

    public void setSplitAtPage(int split) {
        if (split <= 0) {
            throw new IllegalArgumentException("Number of pages is smaller than one");
        }
        this.splitLength = split;
    }

    public void setStartPage(int start) {
        if (start <= 0) {
            throw new IllegalArgumentException("Start page is smaller than one");
        }
        this.startPage = start;
    }

    public void setEndPage(int end) {
        if (end <= 0) {
            throw new IllegalArgumentException("End page is smaller than one");
        }
        this.endPage = end;
    }

    private void processPages() throws IOException {
        for (PDPage page : this.sourceDocument.getPages()) {
            if (this.currentPageNumber + 1 >= this.startPage && this.currentPageNumber + 1 <= this.endPage) {
                this.processPage(page);
                ++this.currentPageNumber;
                continue;
            }
            if (this.currentPageNumber > this.endPage) break;
            ++this.currentPageNumber;
        }
    }

    private void createNewDocumentIfNecessary() throws IOException {
        if (this.splitAtPage(this.currentPageNumber) || this.currentDestinationDocument == null) {
            this.currentDestinationDocument = this.createNewDocument();
            this.destinationDocuments.add(this.currentDestinationDocument);
        }
    }

    protected boolean splitAtPage(int pageNumber) {
        return (pageNumber + 1 - Math.max(1, this.startPage)) % this.splitLength == 0;
    }

    protected PDDocument createNewDocument() throws IOException {
        PDDocument document = this.memoryUsageSetting == null ? new PDDocument() : new PDDocument(this.memoryUsageSetting);
        document.getDocument().setVersion(this.getSourceDocument().getVersion());
        PDDocumentInformation sourceDocumentInformation = this.getSourceDocument().getDocumentInformation();
        if (sourceDocumentInformation != null) {
            COSDictionary sourceDocumentInformationDictionary = sourceDocumentInformation.getCOSObject();
            COSDictionary destDocumentInformationDictionary = new COSDictionary();
            for (COSName key : sourceDocumentInformationDictionary.keySet()) {
                COSBase value = sourceDocumentInformationDictionary.getDictionaryObject(key);
                if (value instanceof COSDictionary) {
                    LOG.warn((Object)("Nested entry for key '" + key.getName() + "' skipped in document information dictionary"));
                    if (this.sourceDocument.getDocumentCatalog().getCOSObject() != this.sourceDocument.getDocumentInformation().getCOSObject()) continue;
                    LOG.warn((Object)"/Root and /Info share the same dictionary");
                    continue;
                }
                if (COSName.TYPE.equals(key)) continue;
                destDocumentInformationDictionary.setItem(key, value);
            }
            document.setDocumentInformation(new PDDocumentInformation(destDocumentInformationDictionary));
        }
        document.getDocumentCatalog().setViewerPreferences(this.getSourceDocument().getDocumentCatalog().getViewerPreferences());
        return document;
    }

    protected void processPage(PDPage page) throws IOException {
        this.createNewDocumentIfNecessary();
        PDPage imported = this.getDestinationDocument().importPage(page);
        if (page.getResources() != null && !page.getCOSObject().containsKey(COSName.RESOURCES)) {
            imported.setResources(page.getResources());
            LOG.info((Object)"Resources imported in Splitter");
        }
        this.processAnnotations(imported);
    }

    private void processAnnotations(PDPage imported) throws IOException {
        List<PDAnnotation> annotations = imported.getAnnotations();
        for (PDAnnotation annotation : annotations) {
            if (annotation instanceof PDAnnotationLink) {
                PDAnnotationLink link = (PDAnnotationLink)annotation;
                PDDestination destination = link.getDestination();
                PDAction action = link.getAction();
                if (destination == null && action instanceof PDActionGoTo) {
                    destination = ((PDActionGoTo)action).getDestination();
                }
                if (destination instanceof PDPageDestination) {
                    ((PDPageDestination)destination).setPage(null);
                }
            }
            annotation.setPage(null);
        }
    }

    protected final PDDocument getSourceDocument() {
        return this.sourceDocument;
    }

    protected final PDDocument getDestinationDocument() {
        return this.currentDestinationDocument;
    }
}

