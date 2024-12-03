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
import org.apache.lucene.analysis.ngram.Lucene43NGramTokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public class NGramTokenizerFactory
extends TokenizerFactory {
    private final int maxGramSize;
    private final int minGramSize;

    public NGramTokenizerFactory(Map<String, String> args) {
        super(args);
        this.minGramSize = this.getInt(args, "minGramSize", 1);
        this.maxGramSize = this.getInt(args, "maxGramSize", 2);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public Tokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_44)) {
            return new NGramTokenizer(this.luceneMatchVersion, factory, input, this.minGramSize, this.maxGramSize);
        }
        return new Lucene43NGramTokenizer(factory, input, this.minGramSize, this.maxGramSize);
    }
}

