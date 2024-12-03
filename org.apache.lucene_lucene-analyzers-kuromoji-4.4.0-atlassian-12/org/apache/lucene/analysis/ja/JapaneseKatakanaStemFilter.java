/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 */
package org.apache.lucene.analysis.ja;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public final class JapaneseKatakanaStemFilter
extends TokenFilter {
    public static final int DEFAULT_MINIMUM_LENGTH = 4;
    private static final char HIRAGANA_KATAKANA_PROLONGED_SOUND_MARK = '\u30fc';
    private final CharTermAttribute termAttr = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);
    private final int minimumKatakanaLength;

    public JapaneseKatakanaStemFilter(TokenStream input, int minimumLength) {
        super(input);
        this.minimumKatakanaLength = minimumLength;
    }

    public JapaneseKatakanaStemFilter(TokenStream input) {
        this(input, 4);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAttr.isKeyword()) {
                this.termAttr.setLength(this.stem(this.termAttr.buffer(), this.termAttr.length()));
            }
            return true;
        }
        return false;
    }

    private int stem(char[] term, int length) {
        if (length < this.minimumKatakanaLength) {
            return length;
        }
        if (!this.isKatakana(term, length)) {
            return length;
        }
        if (term[length - 1] == '\u30fc') {
            return length - 1;
        }
        return length;
    }

    private boolean isKatakana(char[] term, int length) {
        for (int i = 0; i < length; ++i) {
            if (Character.UnicodeBlock.of(term[i]) == Character.UnicodeBlock.KATAKANA) continue;
            return false;
        }
        return true;
    }
}

