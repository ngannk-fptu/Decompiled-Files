/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.standard;

import com.atlassian.lucene36.analysis.Tokenizer;
import com.atlassian.lucene36.analysis.standard.ClassicTokenizerImpl;
import com.atlassian.lucene36.analysis.standard.StandardTokenizerInterface;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.OffsetAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.PositionIncrementAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.TypeAttribute;
import com.atlassian.lucene36.util.AttributeSource;
import com.atlassian.lucene36.util.Version;
import java.io.IOException;
import java.io.Reader;

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
    @Deprecated
    public static final int ACRONYM_DEP = 8;
    public static final String[] TOKEN_TYPES = new String[]{"<ALPHANUM>", "<APOSTROPHE>", "<ACRONYM>", "<COMPANY>", "<EMAIL>", "<HOST>", "<NUM>", "<CJ>", "<ACRONYM_DEP>"};
    private boolean replaceInvalidAcronym;
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

    public ClassicTokenizer(Version matchVersion, Reader input) {
        super(input);
        this.init(matchVersion);
    }

    public ClassicTokenizer(Version matchVersion, AttributeSource source, Reader input) {
        super(source, input);
        this.init(matchVersion);
    }

    public ClassicTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input) {
        super(factory, input);
        this.init(matchVersion);
    }

    private final void init(Version matchVersion) {
        this.scanner = new ClassicTokenizerImpl(this.input);
        this.replaceInvalidAcronym = matchVersion.onOrAfter(Version.LUCENE_24);
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
                    if (this.replaceInvalidAcronym) {
                        this.typeAtt.setType(TOKEN_TYPES[5]);
                        this.termAtt.setLength(this.termAtt.length() - 1);
                    } else {
                        this.typeAtt.setType(TOKEN_TYPES[2]);
                    }
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

    public void reset(Reader reader) throws IOException {
        super.reset(reader);
        this.scanner.yyreset(reader);
    }

    @Deprecated
    public boolean isReplaceInvalidAcronym() {
        return this.replaceInvalidAcronym;
    }

    @Deprecated
    public void setReplaceInvalidAcronym(boolean replaceInvalidAcronym) {
        this.replaceInvalidAcronym = replaceInvalidAcronym;
    }
}

