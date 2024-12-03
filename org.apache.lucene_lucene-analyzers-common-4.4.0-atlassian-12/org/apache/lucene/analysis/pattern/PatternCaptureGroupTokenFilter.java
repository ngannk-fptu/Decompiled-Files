/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.util.AttributeSource$State
 *  org.apache.lucene.util.CharsRef
 */
package org.apache.lucene.analysis.pattern;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.CharsRef;

public final class PatternCaptureGroupTokenFilter
extends TokenFilter {
    private final CharTermAttribute charTermAttr = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posAttr = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private AttributeSource.State state;
    private final Matcher[] matchers;
    private final CharsRef spare = new CharsRef();
    private final int[] groupCounts;
    private final boolean preserveOriginal;
    private int[] currentGroup;
    private int currentMatcher;

    public PatternCaptureGroupTokenFilter(TokenStream input, boolean preserveOriginal, Pattern ... patterns) {
        super(input);
        this.preserveOriginal = preserveOriginal;
        this.matchers = new Matcher[patterns.length];
        this.groupCounts = new int[patterns.length];
        this.currentGroup = new int[patterns.length];
        for (int i = 0; i < patterns.length; ++i) {
            this.matchers[i] = patterns[i].matcher("");
            this.groupCounts[i] = this.matchers[i].groupCount();
            this.currentGroup[i] = -1;
        }
    }

    private boolean nextCapture() {
        int min_offset = Integer.MAX_VALUE;
        this.currentMatcher = -1;
        for (int i = 0; i < this.matchers.length; ++i) {
            Matcher matcher = this.matchers[i];
            if (this.currentGroup[i] == -1) {
                int n = this.currentGroup[i] = matcher.find() ? 1 : 0;
            }
            if (this.currentGroup[i] == 0) continue;
            while (this.currentGroup[i] < this.groupCounts[i] + 1) {
                int end;
                int start = matcher.start(this.currentGroup[i]);
                if (start == (end = matcher.end(this.currentGroup[i])) || this.preserveOriginal && start == 0 && this.spare.length == end) {
                    int n = i;
                    this.currentGroup[n] = this.currentGroup[n] + 1;
                    continue;
                }
                if (start >= min_offset) break;
                min_offset = start;
                this.currentMatcher = i;
                break;
            }
            if (this.currentGroup[i] != this.groupCounts[i] + 1) continue;
            this.currentGroup[i] = -1;
            --i;
        }
        return this.currentMatcher != -1;
    }

    public boolean incrementToken() throws IOException {
        if (this.currentMatcher != -1 && this.nextCapture()) {
            assert (this.state != null);
            this.clearAttributes();
            this.restoreState(this.state);
            int start = this.matchers[this.currentMatcher].start(this.currentGroup[this.currentMatcher]);
            int end = this.matchers[this.currentMatcher].end(this.currentGroup[this.currentMatcher]);
            this.posAttr.setPositionIncrement(0);
            this.charTermAttr.copyBuffer(this.spare.chars, start, end - start);
            int n = this.currentMatcher;
            this.currentGroup[n] = this.currentGroup[n] + 1;
            return true;
        }
        if (!this.input.incrementToken()) {
            return false;
        }
        char[] buffer = this.charTermAttr.buffer();
        int length = this.charTermAttr.length();
        this.spare.copyChars(buffer, 0, length);
        this.state = this.captureState();
        for (int i = 0; i < this.matchers.length; ++i) {
            this.matchers[i].reset((CharSequence)this.spare);
            this.currentGroup[i] = -1;
        }
        if (this.preserveOriginal) {
            this.currentMatcher = 0;
        } else if (this.nextCapture()) {
            int start = this.matchers[this.currentMatcher].start(this.currentGroup[this.currentMatcher]);
            int end = this.matchers[this.currentMatcher].end(this.currentGroup[this.currentMatcher]);
            if (start == 0) {
                this.charTermAttr.setLength(end);
            } else {
                this.charTermAttr.copyBuffer(this.spare.chars, start, end - start);
            }
            int n = this.currentMatcher;
            this.currentGroup[n] = this.currentGroup[n] + 1;
        }
        return true;
    }

    public void reset() throws IOException {
        super.reset();
        this.state = null;
        this.currentMatcher = -1;
    }
}

