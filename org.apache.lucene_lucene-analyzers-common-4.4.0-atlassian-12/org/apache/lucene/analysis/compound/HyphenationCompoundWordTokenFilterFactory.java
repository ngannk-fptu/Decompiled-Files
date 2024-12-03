/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.IOUtils
 */
package org.apache.lucene.analysis.compound;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.compound.HyphenationCompoundWordTokenFilter;
import org.apache.lucene.analysis.compound.hyphenation.HyphenationTree;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.util.IOUtils;
import org.xml.sax.InputSource;

public class HyphenationCompoundWordTokenFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private CharArraySet dictionary;
    private HyphenationTree hyphenator;
    private final String dictFile;
    private final String hypFile;
    private final String encoding;
    private final int minWordSize;
    private final int minSubwordSize;
    private final int maxSubwordSize;
    private final boolean onlyLongestMatch;

    public HyphenationCompoundWordTokenFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        this.dictFile = this.get(args, "dictionary");
        this.encoding = this.get(args, "encoding");
        this.hypFile = this.require(args, "hyphenator");
        this.minWordSize = this.getInt(args, "minWordSize", 5);
        this.minSubwordSize = this.getInt(args, "minSubwordSize", 2);
        this.maxSubwordSize = this.getInt(args, "maxSubwordSize", 15);
        this.onlyLongestMatch = this.getBoolean(args, "onlyLongestMatch", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void inform(ResourceLoader loader) throws IOException {
        InputStream stream = null;
        try {
            if (this.dictFile != null) {
                this.dictionary = this.getWordSet(loader, this.dictFile, false);
            }
            stream = loader.openResource(this.hypFile);
            InputSource is = new InputSource(stream);
            is.setEncoding(this.encoding);
            is.setSystemId(this.hypFile);
            this.hyphenator = HyphenationCompoundWordTokenFilter.getHyphenationTree(is);
        }
        catch (Throwable throwable) {
            IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{stream});
            throw throwable;
        }
        IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{stream});
    }

    public HyphenationCompoundWordTokenFilter create(TokenStream input) {
        return new HyphenationCompoundWordTokenFilter(this.luceneMatchVersion, input, this.hyphenator, this.dictionary, this.minWordSize, this.minSubwordSize, this.maxSubwordSize, this.onlyLongestMatch);
    }
}

