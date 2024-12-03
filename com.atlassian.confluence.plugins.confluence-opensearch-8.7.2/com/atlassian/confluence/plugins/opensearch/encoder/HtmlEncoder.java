/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.PlainTextToHtmlConverter
 */
package com.atlassian.confluence.plugins.opensearch.encoder;

import com.atlassian.confluence.plugins.opensearch.encoder.Encoder;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;

public class HtmlEncoder
implements Encoder {
    @Override
    public String encode(String originalText) {
        return PlainTextToHtmlConverter.encodeHtmlEntities((String)originalText);
    }
}

