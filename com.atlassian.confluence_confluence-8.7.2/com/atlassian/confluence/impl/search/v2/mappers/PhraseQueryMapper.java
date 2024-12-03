/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.PhraseQuery
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.impl.search.v2.mappers.LuceneAnalyzerMapper;
import com.atlassian.confluence.internal.search.SearchLanguageProvider;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryUtil;
import com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import java.util.List;
import java.util.Optional;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

public class PhraseQueryMapper
implements LuceneQueryMapper<com.atlassian.confluence.search.v2.query.PhraseQuery> {
    private LuceneAnalyzerFactory luceneAnalyzerFactory;
    private LuceneAnalyzerMapper luceneAnalyzerMapper;
    private SearchLanguageProvider searchLanguageProvider;

    @Override
    public Query convertToLuceneQuery(com.atlassian.confluence.search.v2.query.PhraseQuery query) {
        Optional<MappingAnalyzerDescriptor> mappingAnalyzerDescriptor = query.getAnalyzerDescriptorProvider().getAnalyzer(this.searchLanguageProvider.get());
        Analyzer analyzer = mappingAnalyzerDescriptor.isEmpty() ? this.luceneAnalyzerFactory.createAnalyzer() : this.luceneAnalyzerMapper.map(mappingAnalyzerDescriptor.get());
        PhraseQuery result = new PhraseQuery();
        List<String> terms = LuceneQueryUtil.tokenize(analyzer, query.getFieldName(), query.getText());
        for (String term : terms) {
            result.add(new Term(query.getFieldName(), term));
        }
        result.setSlop(query.getSlop());
        result.setBoost(query.getBoost());
        return result;
    }

    public void setLuceneAnalyzerFactory(LuceneAnalyzerFactory luceneAnalyzerFactory) {
        this.luceneAnalyzerFactory = luceneAnalyzerFactory;
    }

    public void setLuceneAnalyzerMapper(LuceneAnalyzerMapper luceneAnalyzerMapper) {
        this.luceneAnalyzerMapper = luceneAnalyzerMapper;
    }

    public void setSearchLanguageProvider(SearchLanguageProvider searchLanguageProvider) {
        this.searchLanguageProvider = searchLanguageProvider;
    }
}

