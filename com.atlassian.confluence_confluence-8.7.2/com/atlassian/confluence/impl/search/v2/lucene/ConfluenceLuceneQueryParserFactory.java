/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
 *  org.apache.lucene.queryparser.flexible.standard.StandardQueryParser
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.impl.search.v2.mappers.LuceneAnalyzerMapper;
import com.atlassian.confluence.internal.search.SearchLanguageProvider;
import com.atlassian.confluence.internal.search.v2.lucene.CustomFlexibleQueryParser;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryParserFactory;
import com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.search.SearchLanguage;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;

public class ConfluenceLuceneQueryParserFactory
implements LuceneQueryParserFactory {
    private final LuceneAnalyzerFactory luceneAnalyzerFactory;
    private final Analyzer unstemmedAnalyzer;
    private final LuceneAnalyzerMapper luceneAnalyzerMapper;
    private final SearchLanguageProvider searchLanguageProvider;

    public ConfluenceLuceneQueryParserFactory(LuceneAnalyzerFactory luceneAnalyzerFactory, Analyzer unstemmedAnalyzer) {
        this(luceneAnalyzerFactory, unstemmedAnalyzer, null, null);
    }

    public ConfluenceLuceneQueryParserFactory(LuceneAnalyzerFactory luceneAnalyzerFactory, Analyzer unstemmedAnalyzer, LuceneAnalyzerMapper luceneAnalyzerMapper, SearchLanguageProvider searchLanguageProvider) {
        this.luceneAnalyzerFactory = luceneAnalyzerFactory;
        this.unstemmedAnalyzer = unstemmedAnalyzer;
        this.luceneAnalyzerMapper = luceneAnalyzerMapper;
        this.searchLanguageProvider = searchLanguageProvider;
    }

    @Override
    public StandardQueryParser createQueryParser() {
        return new CustomFlexibleQueryParser(this.luceneAnalyzerFactory.createAnalyzer(), this.unstemmedAnalyzer);
    }

    @Override
    public StandardQueryParser createQueryParser(Map<String, ? extends AnalyzerDescriptorProvider> analyzerProviders) {
        Map<String, Analyzer> fieldAnalyzers = this.createFieldAnalyzers(analyzerProviders);
        if (fieldAnalyzers.isEmpty()) {
            return this.createQueryParser();
        }
        PerFieldAnalyzerWrapper customAnalyzer = new PerFieldAnalyzerWrapper(this.luceneAnalyzerFactory.createAnalyzer(), fieldAnalyzers);
        return new CustomFlexibleQueryParser((Analyzer)customAnalyzer, this.unstemmedAnalyzer);
    }

    private Map<String, Analyzer> createFieldAnalyzers(Map<String, ? extends AnalyzerDescriptorProvider> analyzerProviders) {
        if (this.searchLanguageProvider == null || this.luceneAnalyzerMapper == null) {
            return Collections.emptyMap();
        }
        SearchLanguage lang = this.searchLanguageProvider.get();
        return analyzerProviders.entrySet().stream().filter(fieldToAnalyzerMap -> ((AnalyzerDescriptorProvider)fieldToAnalyzerMap.getValue()).getAnalyzer(lang).isPresent()).collect(Collectors.toMap(Map.Entry::getKey, fieldToAnalyzerMap -> this.luceneAnalyzerMapper.map(((AnalyzerDescriptorProvider)fieldToAnalyzerMap.getValue()).getAnalyzer(lang).get())));
    }
}

