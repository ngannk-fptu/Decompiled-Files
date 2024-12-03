/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.util.ArrayUtil
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.ngram;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

@Deprecated
public final class Lucene43EdgeNGramTokenizer
extends Tokenizer {
    public static final Side DEFAULT_SIDE = Side.FRONT;
    public static final int DEFAULT_MAX_GRAM_SIZE = 1;
    public static final int DEFAULT_MIN_GRAM_SIZE = 1;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private int minGram;
    private int maxGram;
    private int gramSize;
    private Side side;
    private boolean started;
    private int inLen;
    private int charsRead;
    private String inStr;

    @Deprecated
    public Lucene43EdgeNGramTokenizer(Version version, Reader input, Side side, int minGram, int maxGram) {
        super(input);
        this.init(version, side, minGram, maxGram);
    }

    @Deprecated
    public Lucene43EdgeNGramTokenizer(Version version, AttributeSource.AttributeFactory factory, Reader input, Side side, int minGram, int maxGram) {
        super(factory, input);
        this.init(version, side, minGram, maxGram);
    }

    @Deprecated
    public Lucene43EdgeNGramTokenizer(Version version, Reader input, String sideLabel, int minGram, int maxGram) {
        this(version, input, Side.getSide(sideLabel), minGram, maxGram);
    }

    @Deprecated
    public Lucene43EdgeNGramTokenizer(Version version, AttributeSource.AttributeFactory factory, Reader input, String sideLabel, int minGram, int maxGram) {
        this(version, factory, input, Side.getSide(sideLabel), minGram, maxGram);
    }

    public Lucene43EdgeNGramTokenizer(Version version, Reader input, int minGram, int maxGram) {
        this(version, input, Side.FRONT, minGram, maxGram);
    }

    public Lucene43EdgeNGramTokenizer(Version version, AttributeSource.AttributeFactory factory, Reader input, int minGram, int maxGram) {
        this(version, factory, input, Side.FRONT, minGram, maxGram);
    }

    private void init(Version version, Side side, int minGram, int maxGram) {
        if (version == null) {
            throw new IllegalArgumentException("version must not be null");
        }
        if (side == null) {
            throw new IllegalArgumentException("sideLabel must be either front or back");
        }
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        if (version.onOrAfter(Version.LUCENE_44)) {
            if (side == Side.BACK) {
                throw new IllegalArgumentException("Side.BACK is not supported anymore as of Lucene 4.4");
            }
        } else {
            maxGram = Math.min(maxGram, 1024);
        }
        this.minGram = minGram;
        this.maxGram = maxGram;
        this.side = side;
    }

    public boolean incrementToken() throws IOException {
        this.clearAttributes();
        if (!this.started) {
            this.started = true;
            this.gramSize = this.minGram;
            int limit = this.side == Side.FRONT ? this.maxGram : 1024;
            char[] chars = new char[Math.min(1024, limit)];
            this.charsRead = 0;
            boolean exhausted = false;
            while (this.charsRead < limit) {
                int inc = this.input.read(chars, this.charsRead, chars.length - this.charsRead);
                if (inc == -1) {
                    exhausted = true;
                    break;
                }
                this.charsRead += inc;
                if (this.charsRead != chars.length || this.charsRead >= limit) continue;
                chars = ArrayUtil.grow((char[])chars);
            }
            this.inStr = new String(chars, 0, this.charsRead);
            this.inStr = this.inStr.trim();
            if (!exhausted) {
                int inc;
                char[] throwaway = new char[1024];
                while ((inc = this.input.read(throwaway, 0, throwaway.length)) != -1) {
                    this.charsRead += inc;
                }
            }
            this.inLen = this.inStr.length();
            if (this.inLen == 0) {
                return false;
            }
            this.posIncrAtt.setPositionIncrement(1);
        } else {
            this.posIncrAtt.setPositionIncrement(0);
        }
        if (this.gramSize > this.inLen) {
            return false;
        }
        if (this.gramSize > this.maxGram || this.gramSize > this.inLen) {
            return false;
        }
        int start = this.side == Side.FRONT ? 0 : this.inLen - this.gramSize;
        int end = start + this.gramSize;
        this.termAtt.setEmpty().append((CharSequence)this.inStr, start, end);
        this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(end));
        ++this.gramSize;
        return true;
    }

    public void end() {
        int finalOffset = this.correctOffset(this.charsRead);
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }

    public void reset() throws IOException {
        super.reset();
        this.started = false;
    }

    public static enum Side {
        FRONT{

            @Override
            public String getLabel() {
                return "front";
            }
        }
        ,
        BACK{

            @Override
            public String getLabel() {
                return "back";
            }
        };


        public abstract String getLabel();

        public static Side getSide(String sideName) {
            if (FRONT.getLabel().equals(sideName)) {
                return FRONT;
            }
            if (BACK.getLabel().equals(sideName)) {
                return BACK;
            }
            return null;
        }
    }
}

