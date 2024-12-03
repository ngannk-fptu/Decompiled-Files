/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.compound;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.compound.CompoundWordTokenFilterBase;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class DictionaryCompoundWordTokenFilter
extends CompoundWordTokenFilterBase {
    public DictionaryCompoundWordTokenFilter(Version matchVersion, TokenStream input, CharArraySet dictionary) {
        super(matchVersion, input, dictionary);
        if (dictionary == null) {
            throw new IllegalArgumentException("dictionary cannot be null");
        }
    }

    public DictionaryCompoundWordTokenFilter(Version matchVersion, TokenStream input, CharArraySet dictionary, int minWordSize, int minSubwordSize, int maxSubwordSize, boolean onlyLongestMatch) {
        super(matchVersion, input, dictionary, minWordSize, minSubwordSize, maxSubwordSize, onlyLongestMatch);
        if (dictionary == null) {
            throw new IllegalArgumentException("dictionary cannot be null");
        }
    }

    @Override
    protected void decompose() {
        int len = this.termAtt.length();
        for (int i = 0; i <= len - this.minSubwordSize; ++i) {
            CompoundWordTokenFilterBase.CompoundToken longestMatchToken = null;
            for (int j = this.minSubwordSize; j <= this.maxSubwordSize && i + j <= len; ++j) {
                if (!this.dictionary.contains(this.termAtt.buffer(), i, j)) continue;
                if (this.onlyLongestMatch) {
                    if (longestMatchToken != null) {
                        if (longestMatchToken.txt.length() >= j) continue;
                        longestMatchToken = new CompoundWordTokenFilterBase.CompoundToken(this, i, j);
                        continue;
                    }
                    longestMatchToken = new CompoundWordTokenFilterBase.CompoundToken(this, i, j);
                    continue;
                }
                this.tokens.add(new CompoundWordTokenFilterBase.CompoundToken(this, i, j));
            }
            if (!this.onlyLongestMatch || longestMatchToken == null) continue;
            this.tokens.add(longestMatchToken);
        }
    }
}

