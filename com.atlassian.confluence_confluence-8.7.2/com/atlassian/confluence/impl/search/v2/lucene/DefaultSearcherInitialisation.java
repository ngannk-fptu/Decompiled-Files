/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.standard.StandardAnalyzer
 *  org.apache.lucene.queryparser.classic.QueryParser
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.SortField
 *  org.apache.lucene.search.SortField$Type
 *  org.apache.lucene.search.similarities.DefaultSimilarity
 *  org.apache.lucene.search.similarities.Similarity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.impl.search.v2.lucene.SinceDateQueryFactory;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSearcherInitialisation
implements SearcherInitialisation {
    private static final Logger log = LoggerFactory.getLogger(DefaultSearcherInitialisation.class);
    private static final Pattern LUCENE_SORT_ERROR_PATTERN = Pattern.compile("field \"\\w+\" does not appear to be indexed");

    public void initialise(IndexSearcher searcher) {
        searcher.setSimilarity((Similarity)new DefaultSimilarity());
        if (log.isDebugEnabled()) {
            log.debug("Warming up searcher..");
        }
        Sort sort = new Sort(new SortField("modified", SortField.Type.LONG));
        Sort sortByCreated = new Sort(new SortField("created", SortField.Type.LONG));
        try {
            BooleanQuery q = (BooleanQuery)new QueryParser(LuceneConstants.LUCENE_VERSION, "", (Analyzer)new StandardAnalyzer(LuceneConstants.LUCENE_VERSION)).parse("title:overview contentBody:overview");
            q.add(SinceDateQueryFactory.getInstance("lastmonth", "modified").toQuery(), BooleanClause.Occur.MUST);
            searcher.search((Query)q, null, 1, sort);
            searcher.search((Query)q, null, 1, sortByCreated);
        }
        catch (NumberFormatException e) {
            if (!log.isDebugEnabled() && "Invalid shift value in prefixCoded string (is encoded value really a LONG?)".equalsIgnoreCase(e.getMessage())) {
                log.error("Error encountered while warming up searcher - The index has not been upgraded. Please reindex.");
                return;
            }
            log.error("Error encountered while warming up searcher: " + e.getMessage(), (Throwable)e);
        }
        catch (Throwable e) {
            if ("no terms in field modified - cannot determine sort type".equalsIgnoreCase(e.getMessage())) {
                log.info("Error encountered while warming up searcher - most likely empty index", e);
            }
            if (LUCENE_SORT_ERROR_PATTERN.matcher(StringUtils.defaultString((String)e.getMessage())).matches()) {
                log.debug("Error encountered while warming up searcher - no documents in the index contain the sort field.", e);
            }
            log.error("Error encountered while warming up searcher: " + e.getMessage(), e);
        }
    }
}

