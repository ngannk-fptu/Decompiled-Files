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

public class FilenameFilter
extends TokenFilter {
    private static final String TOKEN_TYPE_FILE = "FILE";
    private static String defaultDelimiters = "\\.|-|_";
    private char[] curTermBuffer;
    private int curPos;
    private int tokStart;
    private int tokEnd;
    private boolean returnAllToken;
    private CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    private PositionIncrementAttribute posAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private int posIncrement;
    private String delimiters;

    public FilenameFilter(TokenStream tokenStream) {
        this(tokenStream, defaultDelimiters, true);
    }

    public FilenameFilter(TokenStream tokenStream, String delimiters, boolean shouldIncrementPosition) {
        super(tokenStream);
        this.delimiters = delimiters;
        this.posIncrement = shouldIncrementPosition ? 1 : 0;
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
                this.returnAllToken = true;
            }
            int length = this.tokEnd - this.tokStart - this.curPos;
            String termText = new String(this.curTermBuffer, this.curPos, length);
            if (this.returnAllToken) {
                this.returnAllToken = false;
                this.clearAttributes();
                this.termAtt.copyBuffer(this.curTermBuffer, this.curPos, length);
                this.offsetAtt.setOffset(this.offsetAtt.startOffset(), this.offsetAtt.startOffset() + length);
                this.typeAtt.setType(TOKEN_TYPE_FILE);
                return true;
            }
            StringTokenizer tokenizer = new StringTokenizer(termText, this.delimiters, true);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (this.delimiters.contains(token)) {
                    this.curPos += token.length();
                    continue;
                }
                this.clearAttributes();
                this.termAtt.copyBuffer(token.toCharArray(), 0, token.length());
                this.offsetAtt.setOffset(this.offsetAtt.startOffset(), this.offsetAtt.startOffset() + token.length());
                this.typeAtt.setType(TOKEN_TYPE_FILE);
                this.posAtt.setPositionIncrement(this.posIncrement);
                this.curPos += token.length();
                return true;
            }
            this.curTermBuffer = null;
        }
    }
}

