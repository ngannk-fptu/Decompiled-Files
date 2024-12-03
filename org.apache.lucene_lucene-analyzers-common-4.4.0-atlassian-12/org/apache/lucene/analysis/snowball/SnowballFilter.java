/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 */
package org.apache.lucene.analysis.snowball;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.tartarus.snowball.SnowballProgram;

public final class SnowballFilter
extends TokenFilter {
    private final SnowballProgram stemmer;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);

    public SnowballFilter(TokenStream input, SnowballProgram stemmer) {
        super(input);
        this.stemmer = stemmer;
    }

    public SnowballFilter(TokenStream in, String name) {
        super(in);
        try {
            Class<SnowballProgram> stemClass = Class.forName("org.tartarus.snowball.ext." + name + "Stemmer").asSubclass(SnowballProgram.class);
            this.stemmer = stemClass.newInstance();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid stemmer class specified: " + name, e);
        }
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAttr.isKeyword()) {
                char[] termBuffer = this.termAtt.buffer();
                int length = this.termAtt.length();
                this.stemmer.setCurrent(termBuffer, length);
                this.stemmer.stem();
                char[] finalTerm = this.stemmer.getCurrentBuffer();
                int newLength = this.stemmer.getCurrentBufferLength();
                if (finalTerm != termBuffer) {
                    this.termAtt.copyBuffer(finalTerm, 0, newLength);
                } else {
                    this.termAtt.setLength(newLength);
                }
            }
            return true;
        }
        return false;
    }
}

