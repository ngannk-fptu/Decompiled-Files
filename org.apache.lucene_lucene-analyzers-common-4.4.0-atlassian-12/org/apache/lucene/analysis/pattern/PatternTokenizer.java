/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.pattern;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeSource;

public final class PatternTokenizer
extends Tokenizer {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final StringBuilder str = new StringBuilder();
    private int index;
    private final int group;
    private final Matcher matcher;
    final char[] buffer = new char[8192];

    public PatternTokenizer(Reader input, Pattern pattern, int group) {
        this(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, input, pattern, group);
    }

    public PatternTokenizer(AttributeSource.AttributeFactory factory, Reader input, Pattern pattern, int group) {
        super(factory, input);
        this.group = group;
        this.matcher = pattern.matcher("");
        if (group >= 0 && group > this.matcher.groupCount()) {
            throw new IllegalArgumentException("invalid group specified: pattern only has: " + this.matcher.groupCount() + " capturing groups");
        }
    }

    public boolean incrementToken() {
        if (this.index >= this.str.length()) {
            return false;
        }
        this.clearAttributes();
        if (this.group >= 0) {
            while (this.matcher.find()) {
                this.index = this.matcher.start(this.group);
                int endIndex = this.matcher.end(this.group);
                if (this.index == endIndex) continue;
                this.termAtt.setEmpty().append((CharSequence)this.str, this.index, endIndex);
                this.offsetAtt.setOffset(this.correctOffset(this.index), this.correctOffset(endIndex));
                return true;
            }
            this.index = Integer.MAX_VALUE;
            return false;
        }
        while (this.matcher.find()) {
            if (this.matcher.start() - this.index > 0) {
                this.termAtt.setEmpty().append((CharSequence)this.str, this.index, this.matcher.start());
                this.offsetAtt.setOffset(this.correctOffset(this.index), this.correctOffset(this.matcher.start()));
                this.index = this.matcher.end();
                return true;
            }
            this.index = this.matcher.end();
        }
        if (this.str.length() - this.index == 0) {
            this.index = Integer.MAX_VALUE;
            return false;
        }
        this.termAtt.setEmpty().append((CharSequence)this.str, this.index, this.str.length());
        this.offsetAtt.setOffset(this.correctOffset(this.index), this.correctOffset(this.str.length()));
        this.index = Integer.MAX_VALUE;
        return true;
    }

    public void end() {
        int ofs = this.correctOffset(this.str.length());
        this.offsetAtt.setOffset(ofs, ofs);
    }

    public void reset() throws IOException {
        this.fillBuffer(this.str, this.input);
        this.matcher.reset(this.str);
        this.index = 0;
    }

    private void fillBuffer(StringBuilder sb, Reader input) throws IOException {
        int len;
        sb.setLength(0);
        while ((len = input.read(this.buffer)) > 0) {
            sb.append(this.buffer, 0, len);
        }
    }
}

