/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

public final class TrimFilter
extends TokenFilter {
    final boolean updateOffsets;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);

    @Deprecated
    public TrimFilter(Version version, TokenStream in, boolean updateOffsets) {
        super(in);
        if (updateOffsets && version.onOrAfter(Version.LUCENE_44)) {
            throw new IllegalArgumentException("updateOffsets=true is not supported anymore as of Lucene 4.4");
        }
        this.updateOffsets = updateOffsets;
    }

    public TrimFilter(Version version, TokenStream in) {
        this(version, in, false);
    }

    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        char[] termBuffer = this.termAtt.buffer();
        int len = this.termAtt.length();
        if (len == 0) {
            return true;
        }
        int start = 0;
        int end = 0;
        int endOff = 0;
        for (start = 0; start < len && Character.isWhitespace(termBuffer[start]); ++start) {
        }
        for (end = len; end >= start && Character.isWhitespace(termBuffer[end - 1]); --end) {
            ++endOff;
        }
        if (start > 0 || end < len) {
            if (start < end) {
                this.termAtt.copyBuffer(termBuffer, start, end - start);
            } else {
                this.termAtt.setEmpty();
            }
            if (this.updateOffsets && len == this.offsetAtt.endOffset() - this.offsetAtt.startOffset()) {
                int newStart = this.offsetAtt.startOffset() + start;
                int newEnd = this.offsetAtt.endOffset() - (start < end ? endOff : 0);
                this.offsetAtt.setOffset(newStart, newEnd);
            }
        }
        return true;
    }
}

