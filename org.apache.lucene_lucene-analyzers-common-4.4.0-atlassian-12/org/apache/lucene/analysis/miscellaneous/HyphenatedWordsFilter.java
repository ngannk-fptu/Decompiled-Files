/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.util.AttributeSource$State
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeSource;

public final class HyphenatedWordsFilter
extends TokenFilter {
    private final CharTermAttribute termAttribute = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttribute = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final StringBuilder hyphenated = new StringBuilder();
    private AttributeSource.State savedState;
    private boolean exhausted = false;
    private int lastEndOffset = 0;

    public HyphenatedWordsFilter(TokenStream in) {
        super(in);
    }

    public boolean incrementToken() throws IOException {
        while (!this.exhausted && this.input.incrementToken()) {
            char[] term = this.termAttribute.buffer();
            int termLength = this.termAttribute.length();
            this.lastEndOffset = this.offsetAttribute.endOffset();
            if (termLength > 0 && term[termLength - 1] == '-') {
                if (this.savedState == null) {
                    this.savedState = this.captureState();
                }
                this.hyphenated.append(term, 0, termLength - 1);
                continue;
            }
            if (this.savedState == null) {
                return true;
            }
            this.hyphenated.append(term, 0, termLength);
            this.unhyphenate();
            return true;
        }
        this.exhausted = true;
        if (this.savedState != null) {
            this.hyphenated.append('-');
            this.unhyphenate();
            return true;
        }
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.hyphenated.setLength(0);
        this.savedState = null;
        this.exhausted = false;
        this.lastEndOffset = 0;
    }

    private void unhyphenate() {
        this.restoreState(this.savedState);
        this.savedState = null;
        char[] term = this.termAttribute.buffer();
        int length = this.hyphenated.length();
        if (length > this.termAttribute.length()) {
            term = this.termAttribute.resizeBuffer(length);
        }
        this.hyphenated.getChars(0, length, term, 0);
        this.termAttribute.setLength(length);
        this.offsetAttribute.setOffset(this.offsetAttribute.startOffset(), this.lastEndOffset);
        this.hyphenated.setLength(0);
    }
}

