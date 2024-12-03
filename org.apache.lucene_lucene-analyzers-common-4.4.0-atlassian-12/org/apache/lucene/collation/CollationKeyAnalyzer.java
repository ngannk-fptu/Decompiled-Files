/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.collation;

import java.io.Reader;
import java.text.Collator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.collation.CollationAttributeFactory;
import org.apache.lucene.collation.CollationKeyFilter;
import org.apache.lucene.util.Version;

public final class CollationKeyAnalyzer
extends Analyzer {
    private final Collator collator;
    private final CollationAttributeFactory factory;
    private final Version matchVersion;

    public CollationKeyAnalyzer(Version matchVersion, Collator collator) {
        this.matchVersion = matchVersion;
        this.collator = collator;
        this.factory = new CollationAttributeFactory(collator);
    }

    @Deprecated
    public CollationKeyAnalyzer(Collator collator) {
        this(Version.LUCENE_31, collator);
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        if (this.matchVersion.onOrAfter(Version.LUCENE_40)) {
            KeywordTokenizer tokenizer = new KeywordTokenizer(this.factory, reader, 256);
            return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)tokenizer);
        }
        KeywordTokenizer tokenizer = new KeywordTokenizer(reader);
        return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)new CollationKeyFilter((TokenStream)tokenizer, this.collator));
    }
}

