/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.Tokenizer;
import java.io.IOException;
import java.io.Reader;

public abstract class ReusableAnalyzerBase
extends Analyzer {
    protected abstract TokenStreamComponents createComponents(String var1, Reader var2);

    public final TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        TokenStreamComponents streamChain = (TokenStreamComponents)this.getPreviousTokenStream();
        Reader r = this.initReader(reader);
        if (streamChain == null || !streamChain.reset(r)) {
            streamChain = this.createComponents(fieldName, r);
            this.setPreviousTokenStream(streamChain);
        }
        return streamChain.getTokenStream();
    }

    public final TokenStream tokenStream(String fieldName, Reader reader) {
        return this.createComponents(fieldName, this.initReader(reader)).getTokenStream();
    }

    protected Reader initReader(Reader reader) {
        return reader;
    }

    public static class TokenStreamComponents {
        protected final Tokenizer source;
        protected final TokenStream sink;

        public TokenStreamComponents(Tokenizer source, TokenStream result) {
            this.source = source;
            this.sink = result;
        }

        public TokenStreamComponents(Tokenizer source) {
            this.source = source;
            this.sink = source;
        }

        protected boolean reset(Reader reader) throws IOException {
            this.source.reset(reader);
            return true;
        }

        protected TokenStream getTokenStream() {
            return this.sink;
        }
    }
}

