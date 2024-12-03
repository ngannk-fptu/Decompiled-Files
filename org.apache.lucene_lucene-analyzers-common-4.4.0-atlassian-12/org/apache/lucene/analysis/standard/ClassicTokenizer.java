/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.standard;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.ClassicTokenizerImpl;
import org.apache.lucene.analysis.standard.StandardTokenizerInterface;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public final class ClassicTokenizer
extends Tokenizer {
    private StandardTokenizerInterface scanner;
    public static final int ALPHANUM = 0;
    public static final int APOSTROPHE = 1;
    public static final int ACRONYM = 2;
    public static final int COMPANY = 3;
    public static final int EMAIL = 4;
    public static final int HOST = 5;
    public static final int NUM = 6;
    public static final int CJ = 7;
    public static final int ACRONYM_DEP = 8;
    public static final String[] TOKEN_TYPES = new String[]{"<ALPHANUM>", "<APOSTROPHE>", "<ACRONYM>", "<COMPANY>", "<EMAIL>", "<HOST>", "<NUM>", "<CJ>", "<ACRONYM_DEP>"};
    private int maxTokenLength = 255;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);

    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
    }

    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }

    public ClassicTokenizer(Version matchVersion, Reader input) {
        super(input);
        this.init(matchVersion);
    }

    public ClassicTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input) {
        super(factory, input);
        this.init(matchVersion);
    }

    private void init(Version matchVersion) {
        this.scanner = new ClassicTokenizerImpl(null);
    }

    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        int posIncr = 1;
        int tokenType;
        while ((tokenType = this.scanner.getNextToken()) != -1) {
            if (this.scanner.yylength() <= this.maxTokenLength) {
                this.posIncrAtt.setPositionIncrement(posIncr);
                this.scanner.getText(this.termAtt);
                int start = this.scanner.yychar();
                this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(start + this.termAtt.length()));
                if (tokenType == 8) {
                    this.typeAtt.setType(TOKEN_TYPES[5]);
                    this.termAtt.setLength(this.termAtt.length() - 1);
                } else {
                    this.typeAtt.setType(TOKEN_TYPES[tokenType]);
                }
                return true;
            }
            ++posIncr;
        }
        return false;
    }

    public final void end() {
        int finalOffset = this.correctOffset(this.scanner.yychar() + this.scanner.yylength());
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }

    public void reset() throws IOException {
        this.scanner.yyreset(this.input);
    }
}

