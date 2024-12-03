/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.ngram;

import java.io.Reader;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public class EdgeNGramTokenizer
extends NGramTokenizer {
    public static final int DEFAULT_MAX_GRAM_SIZE = 1;
    public static final int DEFAULT_MIN_GRAM_SIZE = 1;

    public EdgeNGramTokenizer(Version version, Reader input, int minGram, int maxGram) {
        super(version, input, minGram, maxGram, true);
    }

    public EdgeNGramTokenizer(Version version, AttributeSource.AttributeFactory factory, Reader input, int minGram, int maxGram) {
        super(version, factory, input, minGram, maxGram, true);
    }
}

