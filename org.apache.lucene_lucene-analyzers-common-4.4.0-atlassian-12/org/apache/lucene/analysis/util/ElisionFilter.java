/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.util;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

public final class ElisionFilter
extends TokenFilter {
    private final CharArraySet articles;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public ElisionFilter(TokenStream input, CharArraySet articles) {
        super(input);
        this.articles = articles;
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            char[] termBuffer = this.termAtt.buffer();
            int termLength = this.termAtt.length();
            int index = -1;
            for (int i = 0; i < termLength; ++i) {
                char ch = termBuffer[i];
                if (ch != '\'' && ch != '\u2019') continue;
                index = i;
                break;
            }
            if (index >= 0 && this.articles.contains(termBuffer, 0, index)) {
                this.termAtt.copyBuffer(termBuffer, index + 1, termLength - (index + 1));
            }
            return true;
        }
        return false;
    }
}

