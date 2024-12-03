/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.Document
 */
package com.atlassian.plugins.conversion.extract.binary;

import com.aspose.words.Document;
import com.atlassian.plugins.conversion.AsposeAware;
import java.io.InputStream;

@Deprecated
public class WordBinaryExtractor
extends AsposeAware {
    public static String extractText(InputStream inputStream) throws Exception {
        try {
            return new Document(inputStream).getText();
        }
        catch (Exception e) {
            throw new Exception("Error reading content of Word binary document: " + e.getMessage(), e);
        }
    }
}

