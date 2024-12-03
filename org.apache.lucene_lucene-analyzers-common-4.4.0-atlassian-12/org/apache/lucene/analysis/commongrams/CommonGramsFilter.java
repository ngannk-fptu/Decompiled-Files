/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.AttributeSource$State
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.commongrams;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public final class CommonGramsFilter
extends TokenFilter {
    public static final String GRAM_TYPE = "gram";
    private static final char SEPARATOR = '_';
    private final CharArraySet commonWords;
    private final StringBuilder buffer = new StringBuilder();
    private final CharTermAttribute termAttribute = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttribute = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAttribute = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    private final PositionIncrementAttribute posIncAttribute = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLenAttribute = (PositionLengthAttribute)this.addAttribute(PositionLengthAttribute.class);
    private int lastStartOffset;
    private boolean lastWasCommon;
    private AttributeSource.State savedState;

    public CommonGramsFilter(Version matchVersion, TokenStream input, CharArraySet commonWords) {
        super(input);
        this.commonWords = commonWords;
    }

    public boolean incrementToken() throws IOException {
        if (this.savedState != null) {
            this.restoreState(this.savedState);
            this.savedState = null;
            this.saveTermBuffer();
            return true;
        }
        if (!this.input.incrementToken()) {
            return false;
        }
        if (this.lastWasCommon || this.isCommon() && this.buffer.length() > 0) {
            this.savedState = this.captureState();
            this.gramToken();
            return true;
        }
        this.saveTermBuffer();
        return true;
    }

    public void reset() throws IOException {
        super.reset();
        this.lastWasCommon = false;
        this.savedState = null;
        this.buffer.setLength(0);
    }

    private boolean isCommon() {
        return this.commonWords != null && this.commonWords.contains(this.termAttribute.buffer(), 0, this.termAttribute.length());
    }

    private void saveTermBuffer() {
        this.buffer.setLength(0);
        this.buffer.append(this.termAttribute.buffer(), 0, this.termAttribute.length());
        this.buffer.append('_');
        this.lastStartOffset = this.offsetAttribute.startOffset();
        this.lastWasCommon = this.isCommon();
    }

    private void gramToken() {
        this.buffer.append(this.termAttribute.buffer(), 0, this.termAttribute.length());
        int endOffset = this.offsetAttribute.endOffset();
        this.clearAttributes();
        int length = this.buffer.length();
        char[] termText = this.termAttribute.buffer();
        if (length > termText.length) {
            termText = this.termAttribute.resizeBuffer(length);
        }
        this.buffer.getChars(0, length, termText, 0);
        this.termAttribute.setLength(length);
        this.posIncAttribute.setPositionIncrement(0);
        this.posLenAttribute.setPositionLength(2);
        this.offsetAttribute.setOffset(this.lastStartOffset, endOffset);
        this.typeAttribute.setType(GRAM_TYPE);
        this.buffer.setLength(0);
    }
}

