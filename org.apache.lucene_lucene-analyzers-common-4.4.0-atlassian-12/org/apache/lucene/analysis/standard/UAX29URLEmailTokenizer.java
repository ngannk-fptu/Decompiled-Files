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
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizerInterface;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizerImpl;
import org.apache.lucene.analysis.standard.std31.UAX29URLEmailTokenizerImpl31;
import org.apache.lucene.analysis.standard.std34.UAX29URLEmailTokenizerImpl34;
import org.apache.lucene.analysis.standard.std36.UAX29URLEmailTokenizerImpl36;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public final class UAX29URLEmailTokenizer
extends Tokenizer {
    private final StandardTokenizerInterface scanner;
    public static final int ALPHANUM = 0;
    public static final int NUM = 1;
    public static final int SOUTHEAST_ASIAN = 2;
    public static final int IDEOGRAPHIC = 3;
    public static final int HIRAGANA = 4;
    public static final int KATAKANA = 5;
    public static final int HANGUL = 6;
    public static final int URL = 7;
    public static final int EMAIL = 8;
    public static final String[] TOKEN_TYPES = new String[]{StandardTokenizer.TOKEN_TYPES[0], StandardTokenizer.TOKEN_TYPES[6], StandardTokenizer.TOKEN_TYPES[9], StandardTokenizer.TOKEN_TYPES[10], StandardTokenizer.TOKEN_TYPES[11], StandardTokenizer.TOKEN_TYPES[12], StandardTokenizer.TOKEN_TYPES[13], "<URL>", "<EMAIL>"};
    private int maxTokenLength = 255;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);

    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
        if (this.scanner instanceof UAX29URLEmailTokenizerImpl) {
            ((UAX29URLEmailTokenizerImpl)this.scanner).setBufferSize(Math.min(length, 0x100000));
        }
    }

    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }

    public UAX29URLEmailTokenizer(Version matchVersion, Reader input) {
        super(input);
        this.scanner = UAX29URLEmailTokenizer.getScannerFor(matchVersion);
    }

    public UAX29URLEmailTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input) {
        super(factory, input);
        this.scanner = UAX29URLEmailTokenizer.getScannerFor(matchVersion);
    }

    private static StandardTokenizerInterface getScannerFor(Version matchVersion) {
        if (matchVersion.onOrAfter(Version.LUCENE_40)) {
            return new UAX29URLEmailTokenizerImpl(null);
        }
        if (matchVersion.onOrAfter(Version.LUCENE_36)) {
            return new UAX29URLEmailTokenizerImpl36(null);
        }
        if (matchVersion.onOrAfter(Version.LUCENE_34)) {
            return new UAX29URLEmailTokenizerImpl34(null);
        }
        return new UAX29URLEmailTokenizerImpl31(null);
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
                this.typeAtt.setType(TOKEN_TYPES[tokenType]);
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

