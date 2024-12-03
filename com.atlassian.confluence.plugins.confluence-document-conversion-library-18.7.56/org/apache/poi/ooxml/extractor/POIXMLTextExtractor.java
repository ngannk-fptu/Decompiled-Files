/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ooxml.extractor;

import java.io.IOException;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ooxml.extractor.POIXMLPropertiesTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;

public interface POIXMLTextExtractor
extends POITextExtractor {
    default public POIXMLProperties.CoreProperties getCoreProperties() {
        return this.getDocument().getProperties().getCoreProperties();
    }

    default public POIXMLProperties.ExtendedProperties getExtendedProperties() {
        return this.getDocument().getProperties().getExtendedProperties();
    }

    default public POIXMLProperties.CustomProperties getCustomProperties() {
        return this.getDocument().getProperties().getCustomProperties();
    }

    @Override
    public POIXMLDocument getDocument();

    default public OPCPackage getPackage() {
        POIXMLDocument doc = this.getDocument();
        return doc != null ? doc.getPackage() : null;
    }

    @Override
    default public POIXMLPropertiesTextExtractor getMetadataTextExtractor() {
        return new POIXMLPropertiesTextExtractor(this.getDocument());
    }

    @Override
    default public void close() throws IOException {
        OPCPackage pkg;
        if (this.isCloseFilesystem() && (pkg = this.getPackage()) != null) {
            pkg.revert();
        }
    }

    default public void checkMaxTextSize(CharSequence text, String string) {
        if (string == null) {
            return;
        }
        int size = text.length() + string.length();
        if ((long)size > ZipSecureFile.getMaxTextSize()) {
            throw new IllegalStateException("The text would exceed the max allowed overall size of extracted text. By default this is prevented as some documents may exhaust available memory and it may indicate that the file is used to inflate memory usage and thus could pose a security risk. You can adjust this limit via ZipSecureFile.setMaxTextSize() if you need to work with files which have a lot of text. Size: " + size + ", limit: MAX_TEXT_SIZE: " + ZipSecureFile.getMaxTextSize());
        }
    }
}

