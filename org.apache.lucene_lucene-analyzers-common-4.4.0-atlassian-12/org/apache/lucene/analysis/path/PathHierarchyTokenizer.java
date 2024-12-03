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
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

public class PathHierarchyTokenizer
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
    private int startPosition = 0;
    private int skipped = 0;
    private boolean endDelimiter = false;
    private StringBuilder resultToken;
    private int charsRead = 0;

    public PathHierarchyTokenizer(Reader input) {
        this(input, 1024, '/', '/', 0);
    }

    public PathHierarchyTokenizer(Reader input, int skip) {
        this(input, 1024, '/', '/', skip);
    }

    public PathHierarchyTokenizer(Reader input, int bufferSize, char delimiter) {
        this(input, bufferSize, delimiter, delimiter, 0);
    }

    public PathHierarchyTokenizer(Reader input, char delimiter, char replacement) {
        this(input, 1024, delimiter, replacement, 0);
    }

    public PathHierarchyTokenizer(Reader input, char delimiter, char replacement, int skip) {
        this(input, 1024, delimiter, replacement, skip);
    }

    public PathHierarchyTokenizer(AttributeSource.AttributeFactory factory, Reader input, char delimiter, char replacement, int skip) {
        this(factory, input, 1024, delimiter, replacement, skip);
    }

    public PathHierarchyTokenizer(Reader input, int bufferSize, char delimiter, char replacement, int skip) {
        this(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, input, bufferSize, delimiter, replacement, skip);
    }

    public PathHierarchyTokenizer(AttributeSource.AttributeFactory factory, Reader input, int bufferSize, char delimiter, char replacement, int skip) {
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
    }

    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        this.termAtt.append(this.resultToken);
        if (this.resultToken.length() == 0) {
            this.posAtt.setPositionIncrement(1);
        } else {
            this.posAtt.setPositionIncrement(0);
        }
        int length = 0;
        boolean added = false;
        if (this.endDelimiter) {
            this.termAtt.append(this.replacement);
            ++length;
            this.endDelimiter = false;
            added = true;
        }
        while (true) {
            int c;
            if ((c = this.input.read()) >= 0) {
                ++this.charsRead;
            } else {
                if (this.skipped > this.skip) {
                    this.termAtt.setLength(length += this.resultToken.length());
                    this.offsetAtt.setOffset(this.correctOffset(this.startPosition), this.correctOffset(this.startPosition + length));
                    if (added) {
                        this.resultToken.setLength(0);
                        this.resultToken.append(this.termAtt.buffer(), 0, length);
                    }
                    return added;
                }
                return false;
            }
            if (!added) {
                added = true;
                ++this.skipped;
                if (this.skipped > this.skip) {
                    this.termAtt.append(c == this.delimiter ? this.replacement : (char)c);
                    ++length;
                    continue;
                }
                ++this.startPosition;
                continue;
            }
            if (c == this.delimiter) {
                if (this.skipped > this.skip) break;
                ++this.skipped;
                if (this.skipped > this.skip) {
                    this.termAtt.append(this.replacement);
                    ++length;
                    continue;
                }
                ++this.startPosition;
                continue;
            }
            if (this.skipped > this.skip) {
                this.termAtt.append((char)c);
                ++length;
                continue;
            }
            ++this.startPosition;
        }
        this.endDelimiter = true;
        this.termAtt.setLength(length += this.resultToken.length());
        this.offsetAtt.setOffset(this.correctOffset(this.startPosition), this.correctOffset(this.startPosition + length));
        this.resultToken.setLength(0);
        this.resultToken.append(this.termAtt.buffer(), 0, length);
        return true;
    }

    public final void end() {
        int finalOffset = this.correctOffset(this.charsRead);
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }

    public void reset() throws IOException {
        super.reset();
        this.resultToken.setLength(0);
        this.charsRead = 0;
        this.endDelimiter = false;
        this.skipped = 0;
        this.startPosition = 0;
    }
}

