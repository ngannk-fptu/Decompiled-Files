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
 *  org.apache.lucene.util.AttributeSource
 */
package org.apache.lucene.analysis.shingle;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

public final class ShingleFilter
extends TokenFilter {
    public static final char[] FILLER_TOKEN = new char[]{'_'};
    public static final int DEFAULT_MAX_SHINGLE_SIZE = 2;
    public static final int DEFAULT_MIN_SHINGLE_SIZE = 2;
    public static final String DEFAULT_TOKEN_TYPE = "shingle";
    public static final String TOKEN_SEPARATOR = " ";
    private LinkedList<InputWindowToken> inputWindow = new LinkedList();
    private CircularSequence gramSize;
    private StringBuilder gramBuilder = new StringBuilder();
    private String tokenType = "shingle";
    private String tokenSeparator = " ";
    private boolean outputUnigrams = true;
    private boolean outputUnigramsIfNoShingles = false;
    private int maxShingleSize;
    private int minShingleSize;
    private int numFillerTokensToInsert;
    private AttributeSource nextInputStreamToken;
    private boolean isNextInputStreamToken = false;
    private boolean isOutputHere = false;
    boolean noShingleOutput = true;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLenAtt = (PositionLengthAttribute)this.addAttribute(PositionLengthAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    private boolean exhausted;

    public ShingleFilter(TokenStream input, int minShingleSize, int maxShingleSize) {
        super(input);
        this.setMaxShingleSize(maxShingleSize);
        this.setMinShingleSize(minShingleSize);
    }

    public ShingleFilter(TokenStream input, int maxShingleSize) {
        this(input, 2, maxShingleSize);
    }

    public ShingleFilter(TokenStream input) {
        this(input, 2, 2);
    }

    public ShingleFilter(TokenStream input, String tokenType) {
        this(input, 2, 2);
        this.setTokenType(tokenType);
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setOutputUnigrams(boolean outputUnigrams) {
        this.outputUnigrams = outputUnigrams;
        this.gramSize = new CircularSequence();
    }

    public void setOutputUnigramsIfNoShingles(boolean outputUnigramsIfNoShingles) {
        this.outputUnigramsIfNoShingles = outputUnigramsIfNoShingles;
    }

    public void setMaxShingleSize(int maxShingleSize) {
        if (maxShingleSize < 2) {
            throw new IllegalArgumentException("Max shingle size must be >= 2");
        }
        this.maxShingleSize = maxShingleSize;
    }

    public void setMinShingleSize(int minShingleSize) {
        if (minShingleSize < 2) {
            throw new IllegalArgumentException("Min shingle size must be >= 2");
        }
        if (minShingleSize > this.maxShingleSize) {
            throw new IllegalArgumentException("Min shingle size must be <= max shingle size");
        }
        this.minShingleSize = minShingleSize;
        this.gramSize = new CircularSequence();
    }

    public void setTokenSeparator(String tokenSeparator) {
        this.tokenSeparator = null == tokenSeparator ? "" : tokenSeparator;
    }

    public final boolean incrementToken() throws IOException {
        boolean tokenAvailable = false;
        int builtGramSize = 0;
        if (this.gramSize.atMinValue() || this.inputWindow.size() < this.gramSize.getValue()) {
            this.shiftInputWindow();
            this.gramBuilder.setLength(0);
        } else {
            builtGramSize = this.gramSize.getPreviousValue();
        }
        if (this.inputWindow.size() >= this.gramSize.getValue()) {
            boolean isAllFiller = true;
            InputWindowToken nextToken = null;
            Iterator iter = this.inputWindow.iterator();
            int gramNum = 1;
            while (iter.hasNext() && builtGramSize < this.gramSize.getValue()) {
                nextToken = (InputWindowToken)iter.next();
                if (builtGramSize < gramNum) {
                    if (builtGramSize > 0) {
                        this.gramBuilder.append(this.tokenSeparator);
                    }
                    this.gramBuilder.append(nextToken.termAtt.buffer(), 0, nextToken.termAtt.length());
                    ++builtGramSize;
                }
                if (isAllFiller && nextToken.isFiller) {
                    if (gramNum == this.gramSize.getValue()) {
                        this.gramSize.advance();
                    }
                } else {
                    isAllFiller = false;
                }
                ++gramNum;
            }
            if (!isAllFiller && builtGramSize == this.gramSize.getValue()) {
                this.inputWindow.getFirst().attSource.copyTo((AttributeSource)this);
                this.posIncrAtt.setPositionIncrement(this.isOutputHere ? 0 : 1);
                this.termAtt.setEmpty().append(this.gramBuilder);
                if (this.gramSize.getValue() > 1) {
                    this.typeAtt.setType(this.tokenType);
                    this.noShingleOutput = false;
                }
                this.offsetAtt.setOffset(this.offsetAtt.startOffset(), nextToken.offsetAtt.endOffset());
                this.posLenAtt.setPositionLength(builtGramSize);
                this.isOutputHere = true;
                this.gramSize.advance();
                tokenAvailable = true;
            }
        }
        return tokenAvailable;
    }

    private InputWindowToken getNextToken(InputWindowToken target) throws IOException {
        InputWindowToken newTarget = target;
        if (this.numFillerTokensToInsert > 0) {
            if (null == target) {
                newTarget = new InputWindowToken(this.nextInputStreamToken.cloneAttributes());
            } else {
                this.nextInputStreamToken.copyTo(target.attSource);
            }
            newTarget.offsetAtt.setOffset(newTarget.offsetAtt.startOffset(), newTarget.offsetAtt.startOffset());
            newTarget.termAtt.copyBuffer(FILLER_TOKEN, 0, FILLER_TOKEN.length);
            newTarget.isFiller = true;
            --this.numFillerTokensToInsert;
        } else if (this.isNextInputStreamToken) {
            if (null == target) {
                newTarget = new InputWindowToken(this.nextInputStreamToken.cloneAttributes());
            } else {
                this.nextInputStreamToken.copyTo(target.attSource);
            }
            this.isNextInputStreamToken = false;
            newTarget.isFiller = false;
        } else if (!this.exhausted && this.input.incrementToken()) {
            if (null == target) {
                newTarget = new InputWindowToken(this.cloneAttributes());
            } else {
                this.copyTo(target.attSource);
            }
            if (this.posIncrAtt.getPositionIncrement() > 1) {
                this.numFillerTokensToInsert = Math.min(this.posIncrAtt.getPositionIncrement() - 1, this.maxShingleSize - 1);
                if (null == this.nextInputStreamToken) {
                    this.nextInputStreamToken = this.cloneAttributes();
                } else {
                    this.copyTo(this.nextInputStreamToken);
                }
                this.isNextInputStreamToken = true;
                newTarget.offsetAtt.setOffset(this.offsetAtt.startOffset(), this.offsetAtt.startOffset());
                newTarget.termAtt.copyBuffer(FILLER_TOKEN, 0, FILLER_TOKEN.length);
                newTarget.isFiller = true;
                --this.numFillerTokensToInsert;
            } else {
                newTarget.isFiller = false;
            }
        } else {
            newTarget = null;
            this.exhausted = true;
        }
        return newTarget;
    }

    private void shiftInputWindow() throws IOException {
        InputWindowToken firstToken = null;
        if (this.inputWindow.size() > 0) {
            firstToken = this.inputWindow.removeFirst();
        }
        while (this.inputWindow.size() < this.maxShingleSize) {
            if (null != firstToken) {
                if (null == this.getNextToken(firstToken)) break;
                this.inputWindow.add(firstToken);
                firstToken = null;
                continue;
            }
            InputWindowToken nextToken = this.getNextToken(null);
            if (null == nextToken) break;
            this.inputWindow.add(nextToken);
        }
        if (this.outputUnigramsIfNoShingles && this.noShingleOutput && this.gramSize.minValue > 1 && this.inputWindow.size() < this.minShingleSize) {
            this.gramSize.minValue = 1;
        }
        this.gramSize.reset();
        this.isOutputHere = false;
    }

    public void reset() throws IOException {
        super.reset();
        this.gramSize.reset();
        this.inputWindow.clear();
        this.nextInputStreamToken = null;
        this.isNextInputStreamToken = false;
        this.numFillerTokensToInsert = 0;
        this.isOutputHere = false;
        this.noShingleOutput = true;
        this.exhausted = false;
        if (this.outputUnigramsIfNoShingles && !this.outputUnigrams) {
            this.gramSize.minValue = this.minShingleSize;
        }
    }

    private class InputWindowToken {
        final AttributeSource attSource;
        final CharTermAttribute termAtt;
        final OffsetAttribute offsetAtt;
        boolean isFiller = false;

        public InputWindowToken(AttributeSource attSource) {
            this.attSource = attSource;
            this.termAtt = (CharTermAttribute)attSource.getAttribute(CharTermAttribute.class);
            this.offsetAtt = (OffsetAttribute)attSource.getAttribute(OffsetAttribute.class);
        }
    }

    private class CircularSequence {
        private int value;
        private int previousValue;
        private int minValue;

        public CircularSequence() {
            this.minValue = ShingleFilter.this.outputUnigrams ? 1 : ShingleFilter.this.minShingleSize;
            this.reset();
        }

        public int getValue() {
            return this.value;
        }

        public void advance() {
            this.previousValue = this.value;
            if (this.value == 1) {
                this.value = ShingleFilter.this.minShingleSize;
            } else if (this.value == ShingleFilter.this.maxShingleSize) {
                this.reset();
            } else {
                ++this.value;
            }
        }

        public void reset() {
            this.previousValue = this.value = this.minValue;
        }

        public boolean atMinValue() {
            return this.value == this.minValue;
        }

        public int getPreviousValue() {
            return this.previousValue;
        }
    }
}

