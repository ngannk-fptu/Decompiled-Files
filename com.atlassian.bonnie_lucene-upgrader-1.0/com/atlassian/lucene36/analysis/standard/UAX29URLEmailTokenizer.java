/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.standard;

import com.atlassian.lucene36.analysis.Tokenizer;
import com.atlassian.lucene36.analysis.standard.StandardTokenizer;
import com.atlassian.lucene36.analysis.standard.StandardTokenizerInterface;
import com.atlassian.lucene36.analysis.standard.UAX29URLEmailTokenizerImpl;
import com.atlassian.lucene36.analysis.standard.std31.UAX29URLEmailTokenizerImpl31;
import com.atlassian.lucene36.analysis.standard.std34.UAX29URLEmailTokenizerImpl34;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.OffsetAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.PositionIncrementAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.TypeAttribute;
import com.atlassian.lucene36.util.AttributeSource;
import com.atlassian.lucene36.util.Version;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
    @Deprecated
    public static final String WORD_TYPE = TOKEN_TYPES[0];
    @Deprecated
    public static final String NUMERIC_TYPE = TOKEN_TYPES[1];
    @Deprecated
    public static final String URL_TYPE = TOKEN_TYPES[7];
    @Deprecated
    public static final String EMAIL_TYPE = TOKEN_TYPES[8];
    @Deprecated
    public static final String SOUTH_EAST_ASIAN_TYPE = TOKEN_TYPES[2];
    @Deprecated
    public static final String IDEOGRAPHIC_TYPE = TOKEN_TYPES[3];
    @Deprecated
    public static final String HIRAGANA_TYPE = TOKEN_TYPES[4];
    @Deprecated
    public static final String KATAKANA_TYPE = TOKEN_TYPES[5];
    @Deprecated
    public static final String HANGUL_TYPE = TOKEN_TYPES[6];
    private int maxTokenLength = 255;
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = this.addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAtt = this.addAttribute(TypeAttribute.class);

    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
    }

    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }

    @Deprecated
    public UAX29URLEmailTokenizer(Reader input) {
        this(Version.LUCENE_31, input);
    }

    @Deprecated
    public UAX29URLEmailTokenizer(InputStream input) {
        this(Version.LUCENE_31, (Reader)new InputStreamReader(input));
    }

    @Deprecated
    public UAX29URLEmailTokenizer(AttributeSource source, Reader input) {
        this(Version.LUCENE_31, source, input);
    }

    @Deprecated
    public UAX29URLEmailTokenizer(AttributeSource.AttributeFactory factory, Reader input) {
        this(Version.LUCENE_31, factory, input);
    }

    public UAX29URLEmailTokenizer(Version matchVersion, Reader input) {
        super(input);
        this.scanner = UAX29URLEmailTokenizer.getScannerFor(matchVersion, input);
    }

    public UAX29URLEmailTokenizer(Version matchVersion, AttributeSource source, Reader input) {
        super(source, input);
        this.scanner = UAX29URLEmailTokenizer.getScannerFor(matchVersion, input);
    }

    public UAX29URLEmailTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input) {
        super(factory, input);
        this.scanner = UAX29URLEmailTokenizer.getScannerFor(matchVersion, input);
    }

    private static StandardTokenizerInterface getScannerFor(Version matchVersion, Reader input) {
        if (matchVersion.onOrAfter(Version.LUCENE_36)) {
            return new UAX29URLEmailTokenizerImpl(input);
        }
        if (matchVersion.onOrAfter(Version.LUCENE_34)) {
            return new UAX29URLEmailTokenizerImpl34(input);
        }
        return new UAX29URLEmailTokenizerImpl31(input);
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

    public void reset(Reader reader) throws IOException {
        super.reset(reader);
        this.scanner.yyreset(reader);
    }
}

