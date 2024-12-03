/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.collation;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.analysis.KeywordTokenizer;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.Tokenizer;
import com.atlassian.lucene36.collation.CollationKeyFilter;
import java.io.IOException;
import java.io.Reader;
import java.text.Collator;

public final class CollationKeyAnalyzer
extends Analyzer {
    private Collator collator;

    public CollationKeyAnalyzer(Collator collator) {
        this.collator = collator;
    }

    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream result = new KeywordTokenizer(reader);
        result = new CollationKeyFilter(result, this.collator);
        return result;
    }

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        SavedStreams streams = (SavedStreams)this.getPreviousTokenStream();
        if (streams == null) {
            streams = new SavedStreams();
            streams.source = new KeywordTokenizer(reader);
            streams.result = new CollationKeyFilter(streams.source, this.collator);
            this.setPreviousTokenStream(streams);
        } else {
            streams.source.reset(reader);
        }
        return streams.result;
    }

    private class SavedStreams {
        Tokenizer source;
        TokenStream result;

        private SavedStreams() {
        }
    }
}

