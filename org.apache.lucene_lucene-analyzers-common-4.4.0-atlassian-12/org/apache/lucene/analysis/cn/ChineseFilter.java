/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.cn;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

@Deprecated
public final class ChineseFilter
extends TokenFilter {
    public static final String[] STOP_WORDS = new String[]{"and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with"};
    private CharArraySet stopTable;
    private CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public ChineseFilter(TokenStream in) {
        super(in);
        this.stopTable = new CharArraySet(Version.LUCENE_CURRENT, Arrays.asList(STOP_WORDS), false);
    }

    public boolean incrementToken() throws IOException {
        while (this.input.incrementToken()) {
            int termLength;
            char[] text = this.termAtt.buffer();
            if (this.stopTable.contains(text, 0, termLength = this.termAtt.length())) continue;
            switch (Character.getType(text[0])) {
                case 1: 
                case 2: {
                    if (termLength <= 1) break;
                    return true;
                }
                case 5: {
                    return true;
                }
            }
        }
        return false;
    }
}

