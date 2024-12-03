/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.FilteredQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.search.highlight;

import java.io.IOException;
import java.util.HashSet;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.WeightedTerm;

public final class QueryTermExtractor {
    public static final WeightedTerm[] getTerms(Query query) {
        return QueryTermExtractor.getTerms(query, false);
    }

    public static final WeightedTerm[] getIdfWeightedTerms(Query query, IndexReader reader, String fieldName) {
        WeightedTerm[] terms = QueryTermExtractor.getTerms(query, false, fieldName);
        int totalNumDocs = reader.maxDoc();
        for (int i = 0; i < terms.length; ++i) {
            try {
                int docFreq = reader.docFreq(new Term(fieldName, terms[i].term));
                float idf = (float)(Math.log((double)totalNumDocs / (double)(docFreq + 1)) + 1.0);
                terms[i].weight *= idf;
                continue;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return terms;
    }

    public static final WeightedTerm[] getTerms(Query query, boolean prohibited, String fieldName) {
        HashSet<WeightedTerm> terms = new HashSet<WeightedTerm>();
        QueryTermExtractor.getTerms(query, terms, prohibited, fieldName);
        return terms.toArray(new WeightedTerm[0]);
    }

    public static final WeightedTerm[] getTerms(Query query, boolean prohibited) {
        return QueryTermExtractor.getTerms(query, prohibited, null);
    }

    private static final void getTerms(Query query, HashSet<WeightedTerm> terms, boolean prohibited, String fieldName) {
        try {
            if (query instanceof BooleanQuery) {
                QueryTermExtractor.getTermsFromBooleanQuery((BooleanQuery)query, terms, prohibited, fieldName);
            } else if (query instanceof FilteredQuery) {
                QueryTermExtractor.getTermsFromFilteredQuery((FilteredQuery)query, terms, prohibited, fieldName);
            } else {
                HashSet nonWeightedTerms = new HashSet();
                query.extractTerms(nonWeightedTerms);
                for (Term term : nonWeightedTerms) {
                    if (fieldName != null && !term.field().equals(fieldName)) continue;
                    terms.add(new WeightedTerm(query.getBoost(), term.text()));
                }
            }
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
    }

    private static final void getTermsFromBooleanQuery(BooleanQuery query, HashSet<WeightedTerm> terms, boolean prohibited, String fieldName) {
        BooleanClause[] queryClauses = query.getClauses();
        for (int i = 0; i < queryClauses.length; ++i) {
            if (!prohibited && queryClauses[i].getOccur() == BooleanClause.Occur.MUST_NOT) continue;
            QueryTermExtractor.getTerms(queryClauses[i].getQuery(), terms, prohibited, fieldName);
        }
    }

    private static void getTermsFromFilteredQuery(FilteredQuery query, HashSet<WeightedTerm> terms, boolean prohibited, String fieldName) {
        QueryTermExtractor.getTerms(query.getQuery(), terms, prohibited, fieldName);
    }
}

