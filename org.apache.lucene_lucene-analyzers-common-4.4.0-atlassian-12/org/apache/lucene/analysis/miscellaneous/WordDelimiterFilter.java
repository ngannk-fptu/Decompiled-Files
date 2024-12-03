/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.ArrayUtil
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterIterator;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.ArrayUtil;

public final class WordDelimiterFilter
extends TokenFilter {
    public static final int LOWER = 1;
    public static final int UPPER = 2;
    public static final int DIGIT = 4;
    public static final int SUBWORD_DELIM = 8;
    public static final int ALPHA = 3;
    public static final int ALPHANUM = 7;
    public static final int GENERATE_WORD_PARTS = 1;
    public static final int GENERATE_NUMBER_PARTS = 2;
    public static final int CATENATE_WORDS = 4;
    public static final int CATENATE_NUMBERS = 8;
    public static final int CATENATE_ALL = 16;
    public static final int PRESERVE_ORIGINAL = 32;
    public static final int SPLIT_ON_CASE_CHANGE = 64;
    public static final int SPLIT_ON_NUMERICS = 128;
    public static final int STEM_ENGLISH_POSSESSIVE = 256;
    final CharArraySet protWords;
    private final int flags;
    private final CharTermAttribute termAttribute = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttribute = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncAttribute = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAttribute = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    private final WordDelimiterIterator iterator;
    private final WordDelimiterConcatenation concat = new WordDelimiterConcatenation();
    private int lastConcatCount = 0;
    private final WordDelimiterConcatenation concatAll = new WordDelimiterConcatenation();
    private int accumPosInc = 0;
    private char[] savedBuffer = new char[1024];
    private int savedStartOffset;
    private int savedEndOffset;
    private String savedType;
    private boolean hasSavedState = false;
    private boolean hasIllegalOffsets = false;
    private boolean hasOutputToken = false;
    private boolean hasOutputFollowingOriginal = false;

    public WordDelimiterFilter(TokenStream in, byte[] charTypeTable, int configurationFlags, CharArraySet protWords) {
        super(in);
        this.flags = configurationFlags;
        this.protWords = protWords;
        this.iterator = new WordDelimiterIterator(charTypeTable, this.has(64), this.has(128), this.has(256));
    }

    public WordDelimiterFilter(TokenStream in, int configurationFlags, CharArraySet protWords) {
        this(in, WordDelimiterIterator.DEFAULT_WORD_DELIM_TABLE, configurationFlags, protWords);
    }

    public boolean incrementToken() throws IOException {
        while (true) {
            if (!this.hasSavedState) {
                if (!this.input.incrementToken()) {
                    return false;
                }
                int termLength = this.termAttribute.length();
                char[] termBuffer = this.termAttribute.buffer();
                this.accumPosInc += this.posIncAttribute.getPositionIncrement();
                this.iterator.setText(termBuffer, termLength);
                this.iterator.next();
                if (this.iterator.current == 0 && this.iterator.end == termLength || this.protWords != null && this.protWords.contains(termBuffer, 0, termLength)) {
                    this.posIncAttribute.setPositionIncrement(this.accumPosInc);
                    this.accumPosInc = 0;
                    return true;
                }
                if (this.iterator.end == -1 && !this.has(32)) {
                    if (this.posIncAttribute.getPositionIncrement() != 1) continue;
                    --this.accumPosInc;
                    continue;
                }
                this.saveState();
                this.hasOutputToken = false;
                this.hasOutputFollowingOriginal = !this.has(32);
                this.lastConcatCount = 0;
                if (this.has(32)) {
                    this.posIncAttribute.setPositionIncrement(this.accumPosInc);
                    this.accumPosInc = 0;
                    return true;
                }
            }
            if (this.iterator.end == -1) {
                if (!this.concat.isEmpty() && this.flushConcatenation(this.concat)) {
                    return true;
                }
                if (!this.concatAll.isEmpty()) {
                    if (this.concatAll.subwordCount > this.lastConcatCount) {
                        this.concatAll.writeAndClear();
                        return true;
                    }
                    this.concatAll.clear();
                }
                this.hasSavedState = false;
                continue;
            }
            if (this.iterator.isSingleWord()) {
                this.generatePart(true);
                this.iterator.next();
                return true;
            }
            int wordType = this.iterator.type();
            if (!this.concat.isEmpty() && (this.concat.type & wordType) == 0) {
                if (this.flushConcatenation(this.concat)) {
                    this.hasOutputToken = false;
                    return true;
                }
                this.hasOutputToken = false;
            }
            if (this.shouldConcatenate(wordType)) {
                if (this.concat.isEmpty()) {
                    this.concat.type = wordType;
                }
                this.concatenate(this.concat);
            }
            if (this.has(16)) {
                this.concatenate(this.concatAll);
            }
            if (this.shouldGenerateParts(wordType)) {
                this.generatePart(false);
                this.iterator.next();
                return true;
            }
            this.iterator.next();
        }
    }

    public void reset() throws IOException {
        super.reset();
        this.hasSavedState = false;
        this.concat.clear();
        this.concatAll.clear();
        this.accumPosInc = 0;
    }

    private void saveState() {
        this.savedStartOffset = this.offsetAttribute.startOffset();
        this.savedEndOffset = this.offsetAttribute.endOffset();
        this.hasIllegalOffsets = this.savedEndOffset - this.savedStartOffset != this.termAttribute.length();
        this.savedType = this.typeAttribute.type();
        if (this.savedBuffer.length < this.termAttribute.length()) {
            this.savedBuffer = new char[ArrayUtil.oversize((int)this.termAttribute.length(), (int)2)];
        }
        System.arraycopy(this.termAttribute.buffer(), 0, this.savedBuffer, 0, this.termAttribute.length());
        this.iterator.text = this.savedBuffer;
        this.hasSavedState = true;
    }

    private boolean flushConcatenation(WordDelimiterConcatenation concatenation) {
        this.lastConcatCount = concatenation.subwordCount;
        if (concatenation.subwordCount != 1 || !this.shouldGenerateParts(concatenation.type)) {
            concatenation.writeAndClear();
            return true;
        }
        concatenation.clear();
        return false;
    }

    private boolean shouldConcatenate(int wordType) {
        return this.has(4) && WordDelimiterFilter.isAlpha(wordType) || this.has(8) && WordDelimiterFilter.isDigit(wordType);
    }

    private boolean shouldGenerateParts(int wordType) {
        return this.has(1) && WordDelimiterFilter.isAlpha(wordType) || this.has(2) && WordDelimiterFilter.isDigit(wordType);
    }

    private void concatenate(WordDelimiterConcatenation concatenation) {
        if (concatenation.isEmpty()) {
            concatenation.startOffset = this.savedStartOffset + this.iterator.current;
        }
        concatenation.append(this.savedBuffer, this.iterator.current, this.iterator.end - this.iterator.current);
        concatenation.endOffset = this.savedStartOffset + this.iterator.end;
    }

    private void generatePart(boolean isSingleWord) {
        this.clearAttributes();
        this.termAttribute.copyBuffer(this.savedBuffer, this.iterator.current, this.iterator.end - this.iterator.current);
        int startOffset = this.savedStartOffset + this.iterator.current;
        int endOffset = this.savedStartOffset + this.iterator.end;
        if (this.hasIllegalOffsets) {
            if (isSingleWord && startOffset <= this.savedEndOffset) {
                this.offsetAttribute.setOffset(startOffset, this.savedEndOffset);
            } else {
                this.offsetAttribute.setOffset(this.savedStartOffset, this.savedEndOffset);
            }
        } else {
            this.offsetAttribute.setOffset(startOffset, endOffset);
        }
        this.posIncAttribute.setPositionIncrement(this.position(false));
        this.typeAttribute.setType(this.savedType);
    }

    private int position(boolean inject) {
        int posInc = this.accumPosInc;
        if (this.hasOutputToken) {
            this.accumPosInc = 0;
            return inject ? 0 : Math.max(1, posInc);
        }
        this.hasOutputToken = true;
        if (!this.hasOutputFollowingOriginal) {
            this.hasOutputFollowingOriginal = true;
            return 0;
        }
        this.accumPosInc = 0;
        return Math.max(1, posInc);
    }

    static boolean isAlpha(int type) {
        return (type & 3) != 0;
    }

    static boolean isDigit(int type) {
        return (type & 4) != 0;
    }

    static boolean isSubwordDelim(int type) {
        return (type & 8) != 0;
    }

    static boolean isUpper(int type) {
        return (type & 2) != 0;
    }

    private boolean has(int flag) {
        return (this.flags & flag) != 0;
    }

    final class WordDelimiterConcatenation {
        final StringBuilder buffer = new StringBuilder();
        int startOffset;
        int endOffset;
        int type;
        int subwordCount;

        WordDelimiterConcatenation() {
        }

        void append(char[] text, int offset, int length) {
            this.buffer.append(text, offset, length);
            ++this.subwordCount;
        }

        void write() {
            WordDelimiterFilter.this.clearAttributes();
            if (WordDelimiterFilter.this.termAttribute.length() < this.buffer.length()) {
                WordDelimiterFilter.this.termAttribute.resizeBuffer(this.buffer.length());
            }
            char[] termbuffer = WordDelimiterFilter.this.termAttribute.buffer();
            this.buffer.getChars(0, this.buffer.length(), termbuffer, 0);
            WordDelimiterFilter.this.termAttribute.setLength(this.buffer.length());
            if (WordDelimiterFilter.this.hasIllegalOffsets) {
                WordDelimiterFilter.this.offsetAttribute.setOffset(WordDelimiterFilter.this.savedStartOffset, WordDelimiterFilter.this.savedEndOffset);
            } else {
                WordDelimiterFilter.this.offsetAttribute.setOffset(this.startOffset, this.endOffset);
            }
            WordDelimiterFilter.this.posIncAttribute.setPositionIncrement(WordDelimiterFilter.this.position(true));
            WordDelimiterFilter.this.typeAttribute.setType(WordDelimiterFilter.this.savedType);
            WordDelimiterFilter.this.accumPosInc = 0;
        }

        boolean isEmpty() {
            return this.buffer.length() == 0;
        }

        void clear() {
            this.buffer.setLength(0);
            this.subwordCount = 0;
            this.type = 0;
            this.endOffset = 0;
            this.startOffset = 0;
        }

        void writeAndClear() {
            this.write();
            this.clear();
        }
    }
}

