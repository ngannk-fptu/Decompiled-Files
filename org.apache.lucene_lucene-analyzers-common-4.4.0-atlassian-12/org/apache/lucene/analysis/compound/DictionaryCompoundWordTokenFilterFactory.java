/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.compound;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.compound.DictionaryCompoundWordTokenFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class DictionaryCompoundWordTokenFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private CharArraySet dictionary;
    private final String dictFile;
    private final int minWordSize;
    private final int minSubwordSize;
    private final int maxSubwordSize;
    private final boolean onlyLongestMatch;

    public DictionaryCompoundWordTokenFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        this.dictFile = this.require(args, "dictionary");
        this.minWordSize = this.getInt(args, "minWordSize", 5);
        this.minSubwordSize = this.getInt(args, "minSubwordSize", 2);
        this.maxSubwordSize = this.getInt(args, "maxSubwordSize", 15);
        this.onlyLongestMatch = this.getBoolean(args, "onlyLongestMatch", true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        this.dictionary = super.getWordSet(loader, this.dictFile, false);
    }

    @Override
    public TokenStream create(TokenStream input) {
        return this.dictionary == null ? input : new DictionaryCompoundWordTokenFilter(this.luceneMatchVersion, input, this.dictionary, this.minWordSize, this.minSubwordSize, this.maxSubwordSize, this.onlyLongestMatch);
    }
}

