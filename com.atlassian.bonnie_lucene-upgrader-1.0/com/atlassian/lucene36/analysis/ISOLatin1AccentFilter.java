/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.TokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import java.io.IOException;

@Deprecated
public final class ISOLatin1AccentFilter
extends TokenFilter {
    private char[] output = new char[256];
    private int outputPos;
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);

    public ISOLatin1AccentFilter(TokenStream input) {
        super(input);
    }

    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            char[] buffer = this.termAtt.buffer();
            int length = this.termAtt.length();
            for (int i = 0; i < length; ++i) {
                char c = buffer[i];
                if (c < '\u00c0' || c > '\ufb06') continue;
                this.removeAccents(buffer, length);
                this.termAtt.copyBuffer(this.output, 0, this.outputPos);
                break;
            }
            return true;
        }
        return false;
    }

    public final void removeAccents(char[] input, int length) {
        int size;
        int maxSizeNeeded = 2 * length;
        for (size = this.output.length; size < maxSizeNeeded; size *= 2) {
        }
        if (size != this.output.length) {
            this.output = new char[size];
        }
        this.outputPos = 0;
        int pos = 0;
        int i = 0;
        while (i < length) {
            char c = input[pos];
            if (c < '\u00c0' || c > '\ufb06') {
                this.output[this.outputPos++] = c;
            } else {
                switch (c) {
                    case '\u00c0': 
                    case '\u00c1': 
                    case '\u00c2': 
                    case '\u00c3': 
                    case '\u00c4': 
                    case '\u00c5': {
                        this.output[this.outputPos++] = 65;
                        break;
                    }
                    case '\u00c6': {
                        this.output[this.outputPos++] = 65;
                        this.output[this.outputPos++] = 69;
                        break;
                    }
                    case '\u00c7': {
                        this.output[this.outputPos++] = 67;
                        break;
                    }
                    case '\u00c8': 
                    case '\u00c9': 
                    case '\u00ca': 
                    case '\u00cb': {
                        this.output[this.outputPos++] = 69;
                        break;
                    }
                    case '\u00cc': 
                    case '\u00cd': 
                    case '\u00ce': 
                    case '\u00cf': {
                        this.output[this.outputPos++] = 73;
                        break;
                    }
                    case '\u0132': {
                        this.output[this.outputPos++] = 73;
                        this.output[this.outputPos++] = 74;
                        break;
                    }
                    case '\u00d0': {
                        this.output[this.outputPos++] = 68;
                        break;
                    }
                    case '\u00d1': {
                        this.output[this.outputPos++] = 78;
                        break;
                    }
                    case '\u00d2': 
                    case '\u00d3': 
                    case '\u00d4': 
                    case '\u00d5': 
                    case '\u00d6': 
                    case '\u00d8': {
                        this.output[this.outputPos++] = 79;
                        break;
                    }
                    case '\u0152': {
                        this.output[this.outputPos++] = 79;
                        this.output[this.outputPos++] = 69;
                        break;
                    }
                    case '\u00de': {
                        this.output[this.outputPos++] = 84;
                        this.output[this.outputPos++] = 72;
                        break;
                    }
                    case '\u00d9': 
                    case '\u00da': 
                    case '\u00db': 
                    case '\u00dc': {
                        this.output[this.outputPos++] = 85;
                        break;
                    }
                    case '\u00dd': 
                    case '\u0178': {
                        this.output[this.outputPos++] = 89;
                        break;
                    }
                    case '\u00e0': 
                    case '\u00e1': 
                    case '\u00e2': 
                    case '\u00e3': 
                    case '\u00e4': 
                    case '\u00e5': {
                        this.output[this.outputPos++] = 97;
                        break;
                    }
                    case '\u00e6': {
                        this.output[this.outputPos++] = 97;
                        this.output[this.outputPos++] = 101;
                        break;
                    }
                    case '\u00e7': {
                        this.output[this.outputPos++] = 99;
                        break;
                    }
                    case '\u00e8': 
                    case '\u00e9': 
                    case '\u00ea': 
                    case '\u00eb': {
                        this.output[this.outputPos++] = 101;
                        break;
                    }
                    case '\u00ec': 
                    case '\u00ed': 
                    case '\u00ee': 
                    case '\u00ef': {
                        this.output[this.outputPos++] = 105;
                        break;
                    }
                    case '\u0133': {
                        this.output[this.outputPos++] = 105;
                        this.output[this.outputPos++] = 106;
                        break;
                    }
                    case '\u00f0': {
                        this.output[this.outputPos++] = 100;
                        break;
                    }
                    case '\u00f1': {
                        this.output[this.outputPos++] = 110;
                        break;
                    }
                    case '\u00f2': 
                    case '\u00f3': 
                    case '\u00f4': 
                    case '\u00f5': 
                    case '\u00f6': 
                    case '\u00f8': {
                        this.output[this.outputPos++] = 111;
                        break;
                    }
                    case '\u0153': {
                        this.output[this.outputPos++] = 111;
                        this.output[this.outputPos++] = 101;
                        break;
                    }
                    case '\u00df': {
                        this.output[this.outputPos++] = 115;
                        this.output[this.outputPos++] = 115;
                        break;
                    }
                    case '\u00fe': {
                        this.output[this.outputPos++] = 116;
                        this.output[this.outputPos++] = 104;
                        break;
                    }
                    case '\u00f9': 
                    case '\u00fa': 
                    case '\u00fb': 
                    case '\u00fc': {
                        this.output[this.outputPos++] = 117;
                        break;
                    }
                    case '\u00fd': 
                    case '\u00ff': {
                        this.output[this.outputPos++] = 121;
                        break;
                    }
                    case '\ufb00': {
                        this.output[this.outputPos++] = 102;
                        this.output[this.outputPos++] = 102;
                        break;
                    }
                    case '\ufb01': {
                        this.output[this.outputPos++] = 102;
                        this.output[this.outputPos++] = 105;
                        break;
                    }
                    case '\ufb02': {
                        this.output[this.outputPos++] = 102;
                        this.output[this.outputPos++] = 108;
                        break;
                    }
                    case '\ufb05': {
                        this.output[this.outputPos++] = 102;
                        this.output[this.outputPos++] = 116;
                        break;
                    }
                    case '\ufb06': {
                        this.output[this.outputPos++] = 115;
                        this.output[this.outputPos++] = 116;
                        break;
                    }
                    default: {
                        this.output[this.outputPos++] = c;
                    }
                }
            }
            ++i;
            ++pos;
        }
    }
}

