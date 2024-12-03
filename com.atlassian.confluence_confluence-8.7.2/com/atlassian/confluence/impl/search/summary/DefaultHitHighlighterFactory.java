/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.queryparser.classic.ParseException
 *  org.apache.lucene.queryparser.classic.QueryParser
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.summary;

import com.atlassian.confluence.impl.search.summary.HitHighlighterImpl;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import com.atlassian.confluence.search.summary.HitHighlighter;
import com.atlassian.confluence.search.summary.HitHighlighterFactory;
import com.atlassian.confluence.search.v2.QueryUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

public class DefaultHitHighlighterFactory
implements HitHighlighterFactory {
    private final Analyzer queryAnalyzer;

    public DefaultHitHighlighterFactory(Analyzer analyzer) {
        this.queryAnalyzer = analyzer;
    }

    @Override
    public HitHighlighter create(String queryString) {
        try {
            Query query = new QueryParser(LuceneConstants.LUCENE_VERSION, "", this.queryAnalyzer).parse(QueryUtil.escape(queryString));
            return new HitHighlighterImpl(query, this.queryAnalyzer);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

