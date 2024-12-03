/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.conversion.extract.xml.AbstractXMLExtractor
 *  com.atlassian.plugins.conversion.extract.xml.WordXMLExtractor
 */
package com.atlassian.confluence.plugins.conversion.extract.xml;

import com.atlassian.plugins.conversion.extract.xml.AbstractXMLExtractor;
import java.io.IOException;
import java.io.InputStream;

public class WordXMLExtractor
extends AbstractXMLExtractor {
    public static String extractText(InputStream inputStream, long maxSize) throws IOException {
        try {
            return com.atlassian.plugins.conversion.extract.xml.WordXMLExtractor.extractText((InputStream)inputStream, (long)maxSize);
        }
        catch (Exception e) {
            throw new IOException("Error reading content of Word XML document: " + e.getMessage(), e);
        }
    }
}

