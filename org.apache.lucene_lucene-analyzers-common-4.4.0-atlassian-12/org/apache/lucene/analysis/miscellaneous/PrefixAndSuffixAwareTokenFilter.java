/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Token
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.AttributeSource
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.PrefixAwareTokenFilter;
import org.apache.lucene.util.AttributeSource;

public class PrefixAndSuffixAwareTokenFilter
extends TokenStream {
    private PrefixAwareTokenFilter suffix;

    public PrefixAndSuffixAwareTokenFilter(TokenStream prefix, TokenStream input, TokenStream suffix) {
        super((AttributeSource)suffix);
        prefix = new PrefixAwareTokenFilter(prefix, input){

            @Override
            public Token updateSuffixToken(Token suffixToken, Token lastInputToken) {
                return PrefixAndSuffixAwareTokenFilter.this.updateInputToken(suffixToken, lastInputToken);
            }
        };
        this.suffix = new PrefixAwareTokenFilter(prefix, suffix){

            @Override
            public Token updateSuffixToken(Token suffixToken, Token lastInputToken) {
                return PrefixAndSuffixAwareTokenFilter.this.updateSuffixToken(suffixToken, lastInputToken);
            }
        };
    }

    public Token updateInputToken(Token inputToken, Token lastPrefixToken) {
        inputToken.setOffset(lastPrefixToken.endOffset() + inputToken.startOffset(), lastPrefixToken.endOffset() + inputToken.endOffset());
        return inputToken;
    }

    public Token updateSuffixToken(Token suffixToken, Token lastInputToken) {
        suffixToken.setOffset(lastInputToken.endOffset() + suffixToken.startOffset(), lastInputToken.endOffset() + suffixToken.endOffset());
        return suffixToken;
    }

    public final boolean incrementToken() throws IOException {
        return this.suffix.incrementToken();
    }

    public void reset() throws IOException {
        this.suffix.reset();
    }

    public void close() throws IOException {
        this.suffix.close();
    }

    public void end() throws IOException {
        this.suffix.end();
    }
}

