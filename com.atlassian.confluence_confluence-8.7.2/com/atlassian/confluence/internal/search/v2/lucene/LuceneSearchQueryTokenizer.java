/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory
 *  com.google.common.collect.Lists
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.search.v2.mappers.LuceneAnalyzerMapper;
import com.atlassian.confluence.internal.search.SearchLanguageProvider;
import com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.search.v2.analysis.SearchQueryTokenizer;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

@Internal
public class LuceneSearchQueryTokenizer
implements SearchQueryTokenizer {
    private final LuceneAnalyzerMapper luceneAnalyzerMapper;
    private final SearchLanguageProvider searchLanguageProvider;
    private final LuceneAnalyzerFactory luceneAnalyzerFactory;

    public LuceneSearchQueryTokenizer(LuceneAnalyzerMapper luceneAnalyzerMapper, SearchLanguageProvider searchLanguageProvider, LuceneAnalyzerFactory luceneAnalyzerFactory) {
        this.luceneAnalyzerMapper = Objects.requireNonNull(luceneAnalyzerMapper, "luceneAnalyzerMapper");
        this.searchLanguageProvider = Objects.requireNonNull(searchLanguageProvider, "searchLanguageProvider");
        this.luceneAnalyzerFactory = Objects.requireNonNull(luceneAnalyzerFactory, "luceneAnalyzerFactory");
    }

    @Override
    public Collection<String> tokenize(String fieldName, AnalyzerDescriptorProvider analyzerProvider, String text) {
        LinkedList result = Lists.newLinkedList();
        Optional<MappingAnalyzerDescriptor> mappingAnalyzerDescriptor = analyzerProvider.getAnalyzer(this.searchLanguageProvider.get());
        Analyzer luceneAnalyzer = mappingAnalyzerDescriptor.isPresent() ? this.luceneAnalyzerMapper.map(mappingAnalyzerDescriptor.get()) : this.luceneAnalyzerFactory.createAnalyzer();
        try (TokenStream stream = luceneAnalyzer.tokenStream(fieldName, text);){
            CharTermAttribute termAttribute = (CharTermAttribute)stream.addAttribute(CharTermAttribute.class);
            stream.reset();
            while (stream.incrementToken()) {
                result.add(termAttribute.toString());
            }
            stream.end();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Collection<String> tokenize(String fieldName, String text) {
        return this.tokenize(fieldName, lang -> Optional.empty(), text);
    }
}

