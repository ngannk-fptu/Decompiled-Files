/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.CharFilterDescriptor;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.TokenFilterDescriptor;
import com.atlassian.confluence.plugins.index.api.TokenizerDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Deprecated(since="8.7", forRemoval=true)
public class AnalyzerDescriptor
implements MappingAnalyzerDescriptor {
    private final Collection<CharFilterDescriptor> charFilters;
    private final TokenizerDescriptor tokenizer;
    private final Collection<TokenFilterDescriptor> tokenFilters;

    private AnalyzerDescriptor(Builder builder) {
        this.charFilters = new ArrayList<CharFilterDescriptor>(Objects.requireNonNull(builder.charFilters, "charFilters must not be null"));
        this.tokenizer = Objects.requireNonNull(builder.tokenizer, "tokenizer must not be null");
        this.tokenFilters = new ArrayList<TokenFilterDescriptor>(Objects.requireNonNull(builder.tokenFilters, "tokenFilters must not be null"));
    }

    public Collection<CharFilterDescriptor> getCharFilters() {
        return Collections.unmodifiableCollection(this.charFilters);
    }

    public TokenizerDescriptor getTokenizer() {
        return this.tokenizer;
    }

    public Collection<TokenFilterDescriptor> getTokenFilters() {
        return Collections.unmodifiableCollection(this.tokenFilters);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnalyzerDescriptor)) {
            return false;
        }
        AnalyzerDescriptor that = (AnalyzerDescriptor)o;
        return Objects.equals(this.getCharFilters(), that.getCharFilters()) && Objects.equals(this.getTokenizer(), that.getTokenizer()) && Objects.equals(this.getTokenFilters(), that.getTokenFilters());
    }

    public int hashCode() {
        return Objects.hash(this.getCharFilters(), this.getTokenizer(), this.getTokenFilters());
    }

    public static Builder builder(TokenizerDescriptor tokenizer) {
        return new Builder(tokenizer);
    }

    public static class Builder {
        private final Collection<CharFilterDescriptor> charFilters = new ArrayList<CharFilterDescriptor>();
        private final TokenizerDescriptor tokenizer;
        private final Collection<TokenFilterDescriptor> tokenFilters;

        public Builder(TokenizerDescriptor tokenizer) {
            this.tokenizer = Objects.requireNonNull(tokenizer);
            this.tokenFilters = new ArrayList<TokenFilterDescriptor>();
        }

        public Builder charFilter(CharFilterDescriptor charFilter) {
            this.charFilters.add(charFilter);
            return this;
        }

        public Builder tokenFilter(TokenFilterDescriptor tokenFilter) {
            this.tokenFilters.add(tokenFilter);
            return this;
        }

        public AnalyzerDescriptor build() {
            return new AnalyzerDescriptor(this);
        }
    }
}

