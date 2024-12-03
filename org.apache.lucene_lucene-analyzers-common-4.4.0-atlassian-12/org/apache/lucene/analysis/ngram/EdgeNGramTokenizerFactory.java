/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.ngram;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenizer;
import org.apache.lucene.analysis.ngram.Lucene43EdgeNGramTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public class EdgeNGramTokenizerFactory
extends TokenizerFactory {
    private final int maxGramSize;
    private final int minGramSize;
    private final String side;

    public EdgeNGramTokenizerFactory(Map<String, String> args) {
        super(args);
        this.minGramSize = this.getInt(args, "minGramSize", 1);
        this.maxGramSize = this.getInt(args, "maxGramSize", 1);
        this.side = this.get(args, "side", EdgeNGramTokenFilter.Side.FRONT.getLabel());
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public Tokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_44)) {
            if (!EdgeNGramTokenFilter.Side.FRONT.getLabel().equals(this.side)) {
                throw new IllegalArgumentException(EdgeNGramTokenizer.class.getSimpleName() + " does not support backward n-grams as of Lucene 4.4");
            }
            return new EdgeNGramTokenizer(this.luceneMatchVersion, input, this.minGramSize, this.maxGramSize);
        }
        return new Lucene43EdgeNGramTokenizer(this.luceneMatchVersion, input, this.side, this.minGramSize, this.maxGramSize);
    }
}

