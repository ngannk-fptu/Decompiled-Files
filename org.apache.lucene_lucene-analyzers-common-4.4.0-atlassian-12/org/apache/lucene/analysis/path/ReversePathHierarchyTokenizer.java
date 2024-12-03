/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.path;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

public class ReversePathHierarchyTokenizer
extends Tokenizer {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final char DEFAULT_DELIMITER = '/';
    public static final int DEFAULT_SKIP = 0;
    private final char delimiter;
    private final char replacement;
    private final int skip;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private int endPosition = 0;
    private int finalOffset = 0;
    private int skipped = 0;
    private StringBuilder resultToken;
    private List<Integer> delimiterPositions;
    private int delimitersCount = -1;
    private char[] resultTokenBuffer;

    public ReversePathHierarchyTokenizer(Reader input) {
        this(input, 1024, '/', '/', 0);
    }

    public ReversePathHierarchyTokenizer(Reader input, int skip) {
        this(input, 1024, '/', '/', skip);
    }

    public ReversePathHierarchyTokenizer(Reader input, int bufferSize, char delimiter) {
        this(input, bufferSize, delimiter, delimiter, 0);
    }

    public ReversePathHierarchyTokenizer(Reader input, char delimiter, char replacement) {
        this(input, 1024, delimiter, replacement, 0);
    }

    public ReversePathHierarchyTokenizer(Reader input, int bufferSize, char delimiter, char replacement) {
        this(input, bufferSize, delimiter, replacement, 0);
    }

    public ReversePathHierarchyTokenizer(Reader input, char delimiter, int skip) {
        this(input, 1024, delimiter, delimiter, skip);
    }

    public ReversePathHierarchyTokenizer(Reader input, char delimiter, char replacement, int skip) {
        this(input, 1024, delimiter, replacement, skip);
    }

    public ReversePathHierarchyTokenizer(AttributeSource.AttributeFactory factory, Reader input, char delimiter, char replacement, int skip) {
        this(factory, input, 1024, delimiter, replacement, skip);
    }

    public ReversePathHierarchyTokenizer(Reader input, int bufferSize, char delimiter, char replacement, int skip) {
        this(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, input, bufferSize, delimiter, replacement, skip);
    }

    public ReversePathHierarchyTokenizer(AttributeSource.AttributeFactory factory, Reader input, int bufferSize, char delimiter, char replacement, int skip) {
        super(factory, input);
        if (bufferSize < 0) {
            throw new IllegalArgumentException("bufferSize cannot be negative");
        }
        if (skip < 0) {
            throw new IllegalArgumentException("skip cannot be negative");
        }
        this.termAtt.resizeBuffer(bufferSize);
        this.delimiter = delimiter;
        this.replacement = replacement;
        this.skip = skip;
        this.resultToken = new StringBuilder(bufferSize);
        this.resultTokenBuffer = new char[bufferSize];
        this.delimiterPositions = new ArrayList<Integer>(bufferSize / 10);
    }

    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        if (this.delimitersCount == -1) {
            int c;
            int length = 0;
            this.delimiterPositions.add(0);
            while ((c = this.input.read()) >= 0) {
                ++length;
                if (c == this.delimiter) {
                    this.delimiterPositions.add(length);
                    this.resultToken.append(this.replacement);
                    continue;
                }
                this.resultToken.append((char)c);
            }
            this.delimitersCount = this.delimiterPositions.size();
            if (this.delimiterPositions.get(this.delimitersCount - 1) < length) {
                this.delimiterPositions.add(length);
                ++this.delimitersCount;
            }
            if (this.resultTokenBuffer.length < this.resultToken.length()) {
                this.resultTokenBuffer = new char[this.resultToken.length()];
            }
            this.resultToken.getChars(0, this.resultToken.length(), this.resultTokenBuffer, 0);
            this.resultToken.setLength(0);
            int idx = this.delimitersCount - 1 - this.skip;
            if (idx >= 0) {
                this.endPosition = this.delimiterPositions.get(idx);
            }
            this.finalOffset = this.correctOffset(length);
            this.posAtt.setPositionIncrement(1);
        } else {
            this.posAtt.setPositionIncrement(0);
        }
        if (this.skipped < this.delimitersCount - this.skip - 1) {
            int start = this.delimiterPositions.get(this.skipped);
            this.termAtt.copyBuffer(this.resultTokenBuffer, start, this.endPosition - start);
            this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(this.endPosition));
            ++this.skipped;
            return true;
        }
        return false;
    }

    public final void end() {
        this.offsetAtt.setOffset(this.finalOffset, this.finalOffset);
    }

    public void reset() throws IOException {
        super.reset();
        this.resultToken.setLength(0);
        this.finalOffset = 0;
        this.endPosition = 0;
        this.skipped = 0;
        this.delimitersCount = -1;
        this.delimiterPositions.clear();
    }
}

