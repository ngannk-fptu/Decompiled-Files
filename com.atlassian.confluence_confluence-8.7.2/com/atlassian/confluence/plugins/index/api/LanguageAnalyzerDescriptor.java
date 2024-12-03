/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.LanguageDescriptor;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

@ExperimentalApi
public final class LanguageAnalyzerDescriptor
implements AnalyzerDescriptorProvider {
    private final MappingAnalyzerDescriptor analyzer;
    private final Map<LanguageDescriptor, MappingAnalyzerDescriptor> languageSpecificAnalyzers;

    private LanguageAnalyzerDescriptor(Builder builder) {
        this.analyzer = builder.analyzer;
        this.languageSpecificAnalyzers = new HashMap<LanguageDescriptor, MappingAnalyzerDescriptor>(builder.languageSpecificAnalyzers);
    }

    @Override
    public Optional<MappingAnalyzerDescriptor> getAnalyzer(LanguageDescriptor language) {
        return Optional.ofNullable(this.languageSpecificAnalyzers.getOrDefault(language, this.analyzer));
    }

    public static Builder builder(MappingAnalyzerDescriptor analyzer) {
        return new Builder(analyzer);
    }

    public static Builder builder() {
        return new Builder(null);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LanguageAnalyzerDescriptor)) {
            return false;
        }
        LanguageAnalyzerDescriptor that = (LanguageAnalyzerDescriptor)o;
        return this.analyzer.equals(that.analyzer) && this.languageSpecificAnalyzers.equals(that.languageSpecificAnalyzers);
    }

    public int hashCode() {
        return Objects.hash(this.analyzer, this.languageSpecificAnalyzers);
    }

    public static class Builder {
        private final MappingAnalyzerDescriptor analyzer;
        private final Map<LanguageDescriptor, MappingAnalyzerDescriptor> languageSpecificAnalyzers;

        private Builder(@Nullable MappingAnalyzerDescriptor analyzer) {
            this.analyzer = analyzer;
            this.languageSpecificAnalyzers = new HashMap<LanguageDescriptor, MappingAnalyzerDescriptor>();
        }

        public Builder analyzer(LanguageDescriptor language, MappingAnalyzerDescriptor analyzer) {
            this.languageSpecificAnalyzers.put(language, analyzer);
            return this;
        }

        public LanguageAnalyzerDescriptor build() {
            return new LanguageAnalyzerDescriptor(this);
        }
    }
}

