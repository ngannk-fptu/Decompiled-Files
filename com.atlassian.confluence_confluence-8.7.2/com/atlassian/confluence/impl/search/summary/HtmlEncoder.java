/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.highlight.Encoder
 */
package com.atlassian.confluence.impl.search.summary;

import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import org.apache.lucene.search.highlight.Encoder;

public class HtmlEncoder
implements Encoder {
    public String encodeText(String originalText) {
        return PlainTextToHtmlConverter.encodeHtmlEntities(originalText);
    }
}

