/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.extractor;

import org.apache.poi.POIDocument;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.extractor.HPSFPropertiesExtractor;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

public interface POIOLE2TextExtractor
extends POITextExtractor {
    default public DocumentSummaryInformation getDocSummaryInformation() {
        return this.getDocument().getDocumentSummaryInformation();
    }

    default public SummaryInformation getSummaryInformation() {
        return this.getDocument().getSummaryInformation();
    }

    @Override
    default public POITextExtractor getMetadataTextExtractor() {
        return new HPSFPropertiesExtractor(this);
    }

    default public DirectoryEntry getRoot() {
        return this.getDocument().getDirectory();
    }

    @Override
    public POIDocument getDocument();
}

