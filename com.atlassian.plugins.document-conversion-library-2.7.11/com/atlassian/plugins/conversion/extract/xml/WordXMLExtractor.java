/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.Document
 */
package com.atlassian.plugins.conversion.extract.xml;

import com.aspose.words.Document;
import com.atlassian.plugins.conversion.extract.xml.AbstractXMLExtractor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Deprecated
public class WordXMLExtractor
extends AbstractXMLExtractor {
    public static String extractText(InputStream inputStream, long maxSize) throws Exception {
        try {
            ByteArrayInputStream is = WordXMLExtractor.filterZipStream(inputStream, maxSize);
            return new Document((InputStream)is).getText();
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error reading content of Word XML document due to illegal file argument: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new Exception("Error reading content of Word XML document: " + e.getMessage(), e);
        }
    }
}

