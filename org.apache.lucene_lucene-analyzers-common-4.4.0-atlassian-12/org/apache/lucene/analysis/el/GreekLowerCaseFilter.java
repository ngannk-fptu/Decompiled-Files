/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.el;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.Version;

public final class GreekLowerCaseFilter
extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final CharacterUtils charUtils;

    public GreekLowerCaseFilter(Version matchVersion, TokenStream in) {
        super(in);
        this.charUtils = CharacterUtils.getInstance(matchVersion);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            char[] chArray = this.termAtt.buffer();
            int chLen = this.termAtt.length();
            for (int i = 0; i < chLen; i += Character.toChars(this.lowerCase(this.charUtils.codePointAt(chArray, i, chLen)), chArray, i)) {
            }
            return true;
        }
        return false;
    }

    private int lowerCase(int codepoint) {
        switch (codepoint) {
            case 962: {
                return 963;
            }
            case 902: 
            case 940: {
                return 945;
            }
            case 904: 
            case 941: {
                return 949;
            }
            case 905: 
            case 942: {
                return 951;
            }
            case 906: 
            case 912: 
            case 938: 
            case 943: 
            case 970: {
                return 953;
            }
            case 910: 
            case 939: 
            case 944: 
            case 971: 
            case 973: {
                return 965;
            }
            case 908: 
            case 972: {
                return 959;
            }
            case 911: 
            case 974: {
                return 969;
            }
            case 930: {
                return 962;
            }
        }
        return Character.toLowerCase(codepoint);
    }
}

