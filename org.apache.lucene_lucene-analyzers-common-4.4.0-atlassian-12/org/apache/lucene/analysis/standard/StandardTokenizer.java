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
import org.apache.lucene.analysis.standard.StandardTokenizerImpl;
import org.apache.lucene.analysis.standard.StandardTokenizerInterface;
import org.apache.lucene.analysis.standard.std31.StandardTokenizerImpl31;
import org.apache.lucene.analysis.standard.std34.StandardTokenizerImpl34;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public final class StandardTokenizer
extends Tokenizer {
    private StandardTokenizerInterface scanner;
    public static final int ALPHANUM = 0;
    @Deprecated
    public static final int APOSTROPHE = 1;
    @Deprecated
    public static final int ACRONYM = 2;
    @Deprecated
    public static final int COMPANY = 3;
    public static final int EMAIL = 4;
    @Deprecated
    public static final int HOST = 5;
    public static final int NUM = 6;
    @Deprecated
    public static final int CJ = 7;
    @Deprecated
    public static final int ACRONYM_DEP = 8;
    public static final int SOUTHEAST_ASIAN = 9;
    public static final int IDEOGRAPHIC = 10;
    public static final int HIRAGANA = 11;
    public static final int KATAKANA = 12;
    public static final int HANGUL = 13;
    public static final String[] TOKEN_TYPES = new String[]{"<ALPHANUM>", "<APOSTROPHE>", "<ACRONYM>", "<COMPANY>", "<EMAIL>", "<HOST>", "<NUM>", "<CJ>", "<ACRONYM_DEP>", "<SOUTHEAST_ASIAN>", "<IDEOGRAPHIC>", "<HIRAGANA>", "<KATAKANA>", "<HANGUL>"};
    private int maxTokenLength = 255;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);

    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
        if (this.scanner instanceof StandardTokenizerImpl) {
            ((StandardTokenizerImpl)this.scanner).setBufferSize(Math.min(length, 0x100000));
        }
    }

    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }

    public StandardTokenizer(Version matchVersion, Reader input) {
        super(input);
        this.init(matchVersion);
    }

    public StandardTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input) {
        super(factory, input);
        this.init(matchVersion);
    }

    private final void init(Version matchVersion) {
        this.scanner = matchVersion.onOrAfter(Version.LUCENE_40) ? new StandardTokenizerImpl(null) : (matchVersion.onOrAfter(Version.LUCENE_34) ? new StandardTokenizerImpl34(null) : (matchVersion.onOrAfter(Version.LUCENE_31) ? new StandardTokenizerImpl31(null) : new ClassicTokenizerImpl(null)));
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

