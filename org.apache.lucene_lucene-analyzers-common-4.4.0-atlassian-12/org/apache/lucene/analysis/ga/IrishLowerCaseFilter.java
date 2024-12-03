/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.ga;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class IrishLowerCaseFilter
extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public IrishLowerCaseFilter(TokenStream in) {
        super(in);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            int i;
            char[] chArray = this.termAtt.buffer();
            int chLen = this.termAtt.length();
            int idx = 0;
            if (chLen > 1 && (chArray[0] == 'n' || chArray[0] == 't') && this.isUpperVowel(chArray[1])) {
                chArray = this.termAtt.resizeBuffer(chLen + 1);
                for (i = chLen; i > 1; --i) {
                    chArray[i] = chArray[i - 1];
                }
                chArray[1] = 45;
                this.termAtt.setLength(chLen + 1);
                idx = 2;
                ++chLen;
            }
            for (i = idx; i < chLen; i += Character.toChars(Character.toLowerCase(chArray[i]), chArray, i)) {
            }
            return true;
        }
        return false;
    }

    private boolean isUpperVowel(int v) {
        switch (v) {
            case 65: 
            case 69: 
            case 73: 
            case 79: 
            case 85: 
            case 193: 
            case 201: 
            case 205: 
            case 211: 
            case 218: {
                return true;
            }
        }
        return false;
    }
}

