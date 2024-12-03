/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.ConstantScoreQuery
 *  org.apache.lucene.search.PrefixQuery
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneTextFieldTokenizer;
import com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.query.UserInfoQuery;
import com.google.common.annotations.VisibleForTesting;
import java.util.LinkedHashSet;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class UserInfoQueryMapper
implements LuceneQueryMapper<UserInfoQuery> {
    private LuceneAnalyzerFactory luceneAnalyzerFactory;

    public UserInfoQueryMapper() {
    }

    @VisibleForTesting
    UserInfoQueryMapper(LuceneAnalyzerFactory luceneAnalyzerFactory) {
        this.luceneAnalyzerFactory = luceneAnalyzerFactory;
    }

    @Override
    public Query convertToLuceneQuery(UserInfoQuery authorQuery) {
        BooleanQuery queryBuilder = new BooleanQuery();
        queryBuilder.add((Query)new TermQuery(new Term(SearchFieldNames.TYPE, "userinfo")), BooleanClause.Occur.MUST);
        LuceneTextFieldTokenizer tokenizer = new LuceneTextFieldTokenizer(this.luceneAnalyzerFactory);
        new LinkedHashSet<String>(tokenizer.tokenize("", authorQuery.getQueryString())).forEach(token -> {
            BooleanQuery userQueryBuilder = new BooleanQuery();
            userQueryBuilder.add((Query)new PrefixQuery(new Term(SearchFieldNames.USER_FULLNAME, token)), BooleanClause.Occur.SHOULD);
            userQueryBuilder.add((Query)new PrefixQuery(new Term(SearchFieldNames.USER_NAME, token)), BooleanClause.Occur.SHOULD);
            userQueryBuilder.add((Query)new TermQuery(new Term(SearchFieldNames.USER_FULLNAME, token)), BooleanClause.Occur.SHOULD);
            ConstantScoreQuery exactMatchUserNameQuery = new ConstantScoreQuery((Query)new TermQuery(new Term(SearchFieldNames.USER_NAME, token)));
            exactMatchUserNameQuery.setBoost(1000.0f);
            userQueryBuilder.add((Query)exactMatchUserNameQuery, BooleanClause.Occur.SHOULD);
            queryBuilder.add((Query)userQueryBuilder, BooleanClause.Occur.MUST);
        });
        return queryBuilder;
    }

    public void setLuceneAnalyzerFactory(LuceneAnalyzerFactory luceneAnalyzerFactory) {
        this.luceneAnalyzerFactory = luceneAnalyzerFactory;
    }
}

