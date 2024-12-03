/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

public final class CapitalizationFilter
extends TokenFilter {
    public static final int DEFAULT_MAX_WORD_COUNT = Integer.MAX_VALUE;
    public static final int DEFAULT_MAX_TOKEN_LENGTH = Integer.MAX_VALUE;
    private final boolean onlyFirstWord;
    private final CharArraySet keep;
    private final boolean forceFirstLetter;
    private final Collection<char[]> okPrefix;
    private final int minWordLength;
    private final int maxWordCount;
    private final int maxTokenLength;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public CapitalizationFilter(TokenStream in) {
        this(in, true, null, true, null, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public CapitalizationFilter(TokenStream in, boolean onlyFirstWord, CharArraySet keep, boolean forceFirstLetter, Collection<char[]> okPrefix, int minWordLength, int maxWordCount, int maxTokenLength) {
        super(in);
        this.onlyFirstWord = onlyFirstWord;
        this.keep = keep;
        this.forceFirstLetter = forceFirstLetter;
        this.okPrefix = okPrefix;
        this.minWordLength = minWordLength;
        this.maxWordCount = maxWordCount;
        this.maxTokenLength = maxTokenLength;
    }

    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        char[] termBuffer = this.termAtt.buffer();
        int termBufferLength = this.termAtt.length();
        char[] backup = null;
        if (this.maxWordCount < Integer.MAX_VALUE) {
            backup = new char[termBufferLength];
            System.arraycopy(termBuffer, 0, backup, 0, termBufferLength);
        }
        if (termBufferLength < this.maxTokenLength) {
            int wordCount = 0;
            int lastWordStart = 0;
            for (int i = 0; i < termBufferLength; ++i) {
                int len;
                char c = termBuffer[i];
                if (c > ' ' && c != '.' || (len = i - lastWordStart) <= 0) continue;
                this.processWord(termBuffer, lastWordStart, len, wordCount++);
                lastWordStart = i + 1;
                ++i;
            }
            if (lastWordStart < termBufferLength) {
                this.processWord(termBuffer, lastWordStart, termBufferLength - lastWordStart, wordCount++);
            }
            if (wordCount > this.maxWordCount) {
                this.termAtt.copyBuffer(backup, 0, termBufferLength);
            }
        }
        return true;
    }

    private void processWord(char[] buffer, int offset, int length, int wordCount) {
        if (length < 1) {
            return;
        }
        if (this.onlyFirstWord && wordCount > 0) {
            for (int i = 0; i < length; ++i) {
                buffer[offset + i] = Character.toLowerCase(buffer[offset + i]);
            }
            return;
        }
        if (this.keep != null && this.keep.contains(buffer, offset, length)) {
            if (wordCount == 0 && this.forceFirstLetter) {
                buffer[offset] = Character.toUpperCase(buffer[offset]);
            }
            return;
        }
        if (length < this.minWordLength) {
            return;
        }
        if (this.okPrefix != null) {
            for (char[] prefix : this.okPrefix) {
                if (length < prefix.length) continue;
                boolean match = true;
                for (int i = 0; i < prefix.length; ++i) {
                    if (prefix[i] == buffer[offset + i]) continue;
                    match = false;
                    break;
                }
                if (!match) continue;
                return;
            }
        }
        buffer[offset] = Character.toUpperCase(buffer[offset]);
        for (int i = 1; i < length; ++i) {
            buffer[offset + i] = Character.toLowerCase(buffer[offset + i]);
        }
    }
}

