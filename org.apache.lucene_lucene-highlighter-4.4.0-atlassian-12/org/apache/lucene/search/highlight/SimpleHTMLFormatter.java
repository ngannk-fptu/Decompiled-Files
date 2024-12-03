/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

public class SimpleHTMLFormatter
implements Formatter {
    private static final String DEFAULT_PRE_TAG = "<B>";
    private static final String DEFAULT_POST_TAG = "</B>";
    private String preTag;
    private String postTag;

    public SimpleHTMLFormatter(String preTag, String postTag) {
        this.preTag = preTag;
        this.postTag = postTag;
    }

    public SimpleHTMLFormatter() {
        this(DEFAULT_PRE_TAG, DEFAULT_POST_TAG);
    }

    @Override
    public String highlightTerm(String originalText, TokenGroup tokenGroup) {
        if (tokenGroup.getTotalScore() <= 0.0f) {
            return originalText;
        }
        StringBuilder returnBuffer = new StringBuilder(this.preTag.length() + originalText.length() + this.postTag.length());
        returnBuffer.append(this.preTag);
        returnBuffer.append(originalText);
        returnBuffer.append(this.postTag);
        return returnBuffer.toString();
    }
}

