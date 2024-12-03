/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.conversion.extract.xml.AbstractXMLExtractor
 */
package com.atlassian.confluence.plugins.conversion.extract.xml;

import com.atlassian.confluence.plugins.conversion.extract.xml.slides.ExtendedXSLFPowerPointExtractor;
import com.atlassian.plugins.conversion.extract.xml.AbstractXMLExtractor;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.OPCPackage;

public class SlidesXMLExtractor
extends AbstractXMLExtractor {
    /*
     * Enabled aggressive exception aggregation
     */
    public static String extractText(InputStream inputStream, long maxSize) throws IOException {
        try (OPCPackage bundle = OPCPackage.open(SlidesXMLExtractor.filterZipStream((InputStream)inputStream, (long)maxSize));){
            String string;
            try (ExtendedXSLFPowerPointExtractor extractor = new ExtendedXSLFPowerPointExtractor(bundle);){
                extractor.setNotesByDefault(true);
                string = extractor.getText();
            }
            return string;
        }
        catch (Exception e) {
            throw new IOException("Error reading content of PowerPoint document: " + e.getMessage(), e);
        }
    }
}

