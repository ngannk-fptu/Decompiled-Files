/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.highlight.Formatter
 *  org.apache.lucene.search.highlight.TokenGroup
 */
package com.atlassian.confluence.impl.search.summary;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

public class NoFormatFormatter
implements Formatter {
    private static final NoFormatFormatter INSTANCE = new NoFormatFormatter();

    protected NoFormatFormatter() {
    }

    public static NoFormatFormatter getInstance() {
        return INSTANCE;
    }

    public String highlightTerm(String originalText, TokenGroup tokenGroup) {
        return originalText;
    }
}

