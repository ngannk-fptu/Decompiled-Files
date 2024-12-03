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
 */
package com.atlassian.confluence.impl.search.v2.lucene.analysis.tokenfilter;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class ExtendedDelimitersFilter
extends TokenFilter {
    public static final String FULL_TOKEN_TYPE = "EXTENDED_DELIMITER_FULL_TOKEN";
    public static final String SPLIT_TOKEN_TYPE = "EXTENDED_DELIMITER_SPLIT_TOKEN";
    private final String delimiters;
    private char[] curTermBuffer;
    private int curPos;
    private int tokStart;
    private int tokEnd;
    private boolean returnAllToken;
    private CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    private PositionIncrementAttribute posIncAttr = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);

    public ExtendedDelimitersFilter(TokenStream tokenStream, String delimiters) {
        super(tokenStream);
        this.delimiters = delimiters;
    }

    public final boolean incrementToken() throws IOException {
        while (true) {
            if (this.curTermBuffer == null) {
                if (!this.input.incrementToken()) {
                    return false;
                }
                this.curTermBuffer = (char[])this.termAtt.buffer().clone();
                this.curPos = 0;
                this.tokStart = this.offsetAtt.startOffset();
                this.tokEnd = this.offsetAtt.endOffset();
                this.posIncAttr.setPositionIncrement(1);
                this.returnAllToken = true;
            }
            int length = this.tokEnd - this.tokStart - this.curPos;
            String termText = new String(this.curTermBuffer, this.curPos, length);
            if (this.returnAllToken) {
                this.returnAllToken = false;
                this.clearAttributes();
                this.termAtt.copyBuffer(this.curTermBuffer, this.curPos, length);
                this.offsetAtt.setOffset(this.offsetAtt.startOffset(), this.offsetAtt.startOffset() + length);
                this.typeAtt.setType(FULL_TOKEN_TYPE);
                return true;
            }
            StringTokenizer tokenizer = new StringTokenizer(termText, this.delimiters, true);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.equals(termText)) continue;
                if (this.delimiters.contains(token)) {
                    this.curPos += token.length();
                    continue;
                }
                this.clearAttributes();
                this.termAtt.copyBuffer(token.toCharArray(), 0, token.length());
                this.offsetAtt.setOffset(this.offsetAtt.startOffset(), this.offsetAtt.startOffset() + token.length());
                this.typeAtt.setType(SPLIT_TOKEN_TYPE);
                this.posIncAttr.setPositionIncrement(0);
                this.curPos += token.length();
                return true;
            }
            this.curTermBuffer = null;
        }
    }
}

