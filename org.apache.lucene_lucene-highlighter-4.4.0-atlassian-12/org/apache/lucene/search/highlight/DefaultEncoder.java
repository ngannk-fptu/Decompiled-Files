/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.search.highlight.Encoder;

public class DefaultEncoder
implements Encoder {
    @Override
    public String encodeText(String originalText) {
        return originalText;
    }
}

