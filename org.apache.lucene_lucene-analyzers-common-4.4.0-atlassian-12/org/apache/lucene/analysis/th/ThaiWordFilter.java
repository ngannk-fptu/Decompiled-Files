/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.util.AttributeSource
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.th;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.Locale;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.util.CharArrayIterator;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public final class ThaiWordFilter
extends TokenFilter {
    public static final boolean DBBI_AVAILABLE;
    private static final BreakIterator proto;
    private final BreakIterator breaker = (BreakIterator)proto.clone();
    private final CharArrayIterator charIterator = CharArrayIterator.newWordInstance();
    private final boolean handlePosIncr;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private AttributeSource clonedToken = null;
    private CharTermAttribute clonedTermAtt = null;
    private OffsetAttribute clonedOffsetAtt = null;
    private boolean hasMoreTokensInClone = false;
    private boolean hasIllegalOffsets = false;

    public ThaiWordFilter(Version matchVersion, TokenStream input) {
        super((TokenStream)(matchVersion.onOrAfter(Version.LUCENE_31) ? input : new LowerCaseFilter(matchVersion, input)));
        if (!DBBI_AVAILABLE) {
            throw new UnsupportedOperationException("This JRE does not have support for Thai segmentation");
        }
        this.handlePosIncr = matchVersion.onOrAfter(Version.LUCENE_31);
    }

    public boolean incrementToken() throws IOException {
        if (this.hasMoreTokensInClone) {
            int start = this.breaker.current();
            int end = this.breaker.next();
            if (end != -1) {
                this.clonedToken.copyTo((AttributeSource)this);
                this.termAtt.copyBuffer(this.clonedTermAtt.buffer(), start, end - start);
                if (this.hasIllegalOffsets) {
                    this.offsetAtt.setOffset(this.clonedOffsetAtt.startOffset(), this.clonedOffsetAtt.endOffset());
                } else {
                    this.offsetAtt.setOffset(this.clonedOffsetAtt.startOffset() + start, this.clonedOffsetAtt.startOffset() + end);
                }
                if (this.handlePosIncr) {
                    this.posAtt.setPositionIncrement(1);
                }
                return true;
            }
            this.hasMoreTokensInClone = false;
        }
        if (!this.input.incrementToken()) {
            return false;
        }
        if (this.termAtt.length() == 0 || Character.UnicodeBlock.of(this.termAtt.charAt(0)) != Character.UnicodeBlock.THAI) {
            return true;
        }
        this.hasMoreTokensInClone = true;
        boolean bl = this.hasIllegalOffsets = this.offsetAtt.endOffset() - this.offsetAtt.startOffset() != this.termAtt.length();
        if (this.clonedToken == null) {
            this.clonedToken = this.cloneAttributes();
            this.clonedTermAtt = (CharTermAttribute)this.clonedToken.getAttribute(CharTermAttribute.class);
            this.clonedOffsetAtt = (OffsetAttribute)this.clonedToken.getAttribute(OffsetAttribute.class);
        } else {
            this.copyTo(this.clonedToken);
        }
        this.charIterator.setText(this.clonedTermAtt.buffer(), 0, this.clonedTermAtt.length());
        this.breaker.setText(this.charIterator);
        int end = this.breaker.next();
        if (end != -1) {
            this.termAtt.setLength(end);
            if (this.hasIllegalOffsets) {
                this.offsetAtt.setOffset(this.clonedOffsetAtt.startOffset(), this.clonedOffsetAtt.endOffset());
            } else {
                this.offsetAtt.setOffset(this.clonedOffsetAtt.startOffset(), this.clonedOffsetAtt.startOffset() + end);
            }
            return true;
        }
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.hasMoreTokensInClone = false;
        this.clonedToken = null;
        this.clonedTermAtt = null;
        this.clonedOffsetAtt = null;
    }

    static {
        proto = BreakIterator.getWordInstance(new Locale("th"));
        proto.setText("\u0e20\u0e32\u0e29\u0e32\u0e44\u0e17\u0e22");
        DBBI_AVAILABLE = proto.isBoundary(4);
    }
}

