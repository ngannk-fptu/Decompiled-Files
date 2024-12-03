/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf.extractor;

import org.apache.poi.POIDocument;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.HPSFPropertiesOnlyDocument;
import org.apache.poi.hpsf.Property;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class HPSFPropertiesExtractor
implements POIOLE2TextExtractor {
    private final POIDocument document;
    private boolean doCloseFilesystem = true;

    public HPSFPropertiesExtractor(POIOLE2TextExtractor mainExtractor) {
        this.document = mainExtractor.getDocument();
    }

    public HPSFPropertiesExtractor(POIDocument document) {
        this.document = document;
    }

    public HPSFPropertiesExtractor(POIFSFileSystem fs) {
        this.document = new HPSFPropertiesOnlyDocument(fs);
    }

    public String getDocumentSummaryInformationText() {
        CustomProperties cps;
        if (this.document == null) {
            return "";
        }
        DocumentSummaryInformation dsi = this.document.getDocumentSummaryInformation();
        StringBuilder text = new StringBuilder();
        text.append(HPSFPropertiesExtractor.getPropertiesText(dsi));
        CustomProperties customProperties = cps = dsi == null ? null : dsi.getCustomProperties();
        if (cps != null) {
            for (String key : cps.nameSet()) {
                String val = HPSFPropertiesExtractor.getPropertyValueText(cps.get(key));
                text.append(key).append(" = ").append(val).append("\n");
            }
        }
        return text.toString();
    }

    public String getSummaryInformationText() {
        if (this.document == null) {
            return "";
        }
        SummaryInformation si = this.document.getSummaryInformation();
        return HPSFPropertiesExtractor.getPropertiesText(si);
    }

    private static String getPropertiesText(PropertySet ps) {
        Property[] props;
        if (ps == null) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        PropertyIDMap idMap = ps.getPropertySetIDMap();
        for (Property prop : props = ps.getProperties()) {
            String typeObj;
            String type = Long.toString(prop.getID());
            String string = typeObj = idMap == null ? null : idMap.get(prop.getID());
            if (typeObj != null) {
                type = typeObj.toString();
            }
            String val = HPSFPropertiesExtractor.getPropertyValueText(prop.getValue());
            text.append(type).append(" = ").append(val).append("\n");
        }
        return text.toString();
    }

    @Override
    public String getText() {
        return this.getSummaryInformationText() + this.getDocumentSummaryInformationText();
    }

    @Override
    public POITextExtractor getMetadataTextExtractor() {
        throw new IllegalStateException("You already have the Metadata Text Extractor, not recursing!");
    }

    private static String getPropertyValueText(Object val) {
        return val == null ? "(not set)" : PropertySet.getPropertyStringValue(val);
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public POIDocument getDocument() {
        return this.document;
    }

    @Override
    public void setCloseFilesystem(boolean doCloseFilesystem) {
        this.doCloseFilesystem = doCloseFilesystem;
    }

    @Override
    public boolean isCloseFilesystem() {
        return this.doCloseFilesystem;
    }

    @Override
    public POIDocument getFilesystem() {
        return this.document;
    }
}

