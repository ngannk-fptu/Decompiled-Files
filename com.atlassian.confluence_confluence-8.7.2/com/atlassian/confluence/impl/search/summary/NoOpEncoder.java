/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.highlight.Encoder
 */
package com.atlassian.confluence.impl.search.summary;

import org.apache.lucene.search.highlight.Encoder;

public class NoOpEncoder
implements Encoder {
    public String encodeText(String s) {
        return s;
    }
}

