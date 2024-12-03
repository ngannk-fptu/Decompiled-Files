/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public final class RemoveDuplicatesTokenFilter
extends TokenFilter {
    private final CharTermAttribute termAttribute = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAttribute = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final CharArraySet previous = new CharArraySet(Version.LUCENE_31, 8, false);

    public RemoveDuplicatesTokenFilter(TokenStream in) {
        super(in);
    }

    public boolean incrementToken() throws IOException {
        while (this.input.incrementToken()) {
            char[] term = this.termAttribute.buffer();
            int length = this.termAttribute.length();
            int posIncrement = this.posIncAttribute.getPositionIncrement();
            if (posIncrement > 0) {
                this.previous.clear();
            }
            boolean duplicate = posIncrement == 0 && this.previous.contains(term, 0, length);
            char[] saved = new char[length];
            System.arraycopy(term, 0, saved, 0, length);
            this.previous.add(saved);
            if (duplicate) continue;
            return true;
        }
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.previous.clear();
    }
}

