/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Token
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

public class TokenGroup {
    private static final int MAX_NUM_TOKENS_PER_GROUP = 50;
    Token[] tokens = new Token[50];
    float[] scores = new float[50];
    int numTokens = 0;
    int startOffset = 0;
    int endOffset = 0;
    float tot;
    int matchStartOffset;
    int matchEndOffset;
    private OffsetAttribute offsetAtt;
    private CharTermAttribute termAtt;

    public TokenGroup(TokenStream tokenStream) {
        this.offsetAtt = (OffsetAttribute)tokenStream.addAttribute(OffsetAttribute.class);
        this.termAtt = (CharTermAttribute)tokenStream.addAttribute(CharTermAttribute.class);
    }

    void addToken(float score) {
        if (this.numTokens < 50) {
            int termStartOffset = this.offsetAtt.startOffset();
            int termEndOffset = this.offsetAtt.endOffset();
            if (this.numTokens == 0) {
                this.startOffset = this.matchStartOffset = termStartOffset;
                this.endOffset = this.matchEndOffset = termEndOffset;
                this.tot += score;
            } else {
                this.startOffset = Math.min(this.startOffset, termStartOffset);
                this.endOffset = Math.max(this.endOffset, termEndOffset);
                if (score > 0.0f) {
                    if (this.tot == 0.0f) {
                        this.matchStartOffset = this.offsetAtt.startOffset();
                        this.matchEndOffset = this.offsetAtt.endOffset();
                    } else {
                        this.matchStartOffset = Math.min(this.matchStartOffset, termStartOffset);
                        this.matchEndOffset = Math.max(this.matchEndOffset, termEndOffset);
                    }
                    this.tot += score;
                }
            }
            Token token = new Token(termStartOffset, termEndOffset);
            token.setEmpty().append(this.termAtt);
            this.tokens[this.numTokens] = token;
            this.scores[this.numTokens] = score;
            ++this.numTokens;
        }
    }

    boolean isDistinct() {
        return this.offsetAtt.startOffset() >= this.endOffset;
    }

    void clear() {
        this.numTokens = 0;
        this.tot = 0.0f;
    }

    public Token getToken(int index) {
        return this.tokens[index];
    }

    public float getScore(int index) {
        return this.scores[index];
    }

    public int getEndOffset() {
        return this.endOffset;
    }

    public int getNumTokens() {
        return this.numTokens;
    }

    public int getStartOffset() {
        return this.startOffset;
    }

    public float getTotalScore() {
        return this.tot;
    }
}

