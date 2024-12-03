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

public class WrappingFormatter
implements Formatter {
    final String pre;
    final String post;

    public WrappingFormatter(String pre, String post) {
        this.pre = pre;
        this.post = post;
    }

    public String highlightTerm(String originalText, TokenGroup tokenGroup) {
        if (tokenGroup.getTotalScore() == 0.0f) {
            return originalText;
        }
        return this.pre + originalText + this.post;
    }
}

