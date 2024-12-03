/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.payloads;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.BooleanQuery;
import com.atlassian.lucene36.search.DisjunctionMaxQuery;
import com.atlassian.lucene36.search.FilteredQuery;
import com.atlassian.lucene36.search.MultiPhraseQuery;
import com.atlassian.lucene36.search.PhraseQuery;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.TermQuery;
import com.atlassian.lucene36.search.spans.SpanNearQuery;
import com.atlassian.lucene36.search.spans.SpanOrQuery;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.SpanTermQuery;
import com.atlassian.lucene36.search.spans.Spans;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PayloadSpanUtil {
    private IndexReader reader;

    public PayloadSpanUtil(IndexReader reader) {
        this.reader = reader;
    }

    public Collection<byte[]> getPayloadsForQuery(Query query) throws IOException {
        ArrayList<byte[]> payloads = new ArrayList<byte[]>();
        this.queryToSpanQuery(query, payloads);
        return payloads;
    }

    private void queryToSpanQuery(Query query, Collection<byte[]> payloads) throws IOException {
        if (query instanceof BooleanQuery) {
            BooleanClause[] queryClauses = ((BooleanQuery)query).getClauses();
            for (int i = 0; i < queryClauses.length; ++i) {
                if (queryClauses[i].isProhibited()) continue;
                this.queryToSpanQuery(queryClauses[i].getQuery(), payloads);
            }
        } else if (query instanceof PhraseQuery) {
            Term[] phraseQueryTerms = ((PhraseQuery)query).getTerms();
            SpanQuery[] clauses = new SpanQuery[phraseQueryTerms.length];
            for (int i = 0; i < phraseQueryTerms.length; ++i) {
                clauses[i] = new SpanTermQuery(phraseQueryTerms[i]);
            }
            int slop = ((PhraseQuery)query).getSlop();
            boolean inorder = false;
            if (slop == 0) {
                inorder = true;
            }
            SpanNearQuery sp = new SpanNearQuery(clauses, slop, inorder);
            sp.setBoost(query.getBoost());
            this.getPayloads(payloads, sp);
        } else if (query instanceof TermQuery) {
            SpanTermQuery stq = new SpanTermQuery(((TermQuery)query).getTerm());
            stq.setBoost(query.getBoost());
            this.getPayloads(payloads, stq);
        } else if (query instanceof SpanQuery) {
            this.getPayloads(payloads, (SpanQuery)query);
        } else if (query instanceof FilteredQuery) {
            this.queryToSpanQuery(((FilteredQuery)query).getQuery(), payloads);
        } else if (query instanceof DisjunctionMaxQuery) {
            Iterator<Query> iterator = ((DisjunctionMaxQuery)query).iterator();
            while (iterator.hasNext()) {
                this.queryToSpanQuery(iterator.next(), payloads);
            }
        } else if (query instanceof MultiPhraseQuery) {
            MultiPhraseQuery mpq = (MultiPhraseQuery)query;
            List<Term[]> termArrays = mpq.getTermArrays();
            int[] positions = mpq.getPositions();
            if (positions.length > 0) {
                int maxPosition = positions[positions.length - 1];
                for (int i = 0; i < positions.length - 1; ++i) {
                    if (positions[i] <= maxPosition) continue;
                    maxPosition = positions[i];
                }
                List[] disjunctLists = new List[maxPosition + 1];
                int distinctPositions = 0;
                for (int i = 0; i < termArrays.size(); ++i) {
                    Term[] termArray = termArrays.get(i);
                    ArrayList<SpanTermQuery> disjuncts = disjunctLists[positions[i]];
                    if (disjuncts == null) {
                        ArrayList<SpanTermQuery> arrayList = new ArrayList<SpanTermQuery>(termArray.length);
                        disjunctLists[positions[i]] = arrayList;
                        disjuncts = arrayList;
                        ++distinctPositions;
                    }
                    for (Term term : termArray) {
                        disjuncts.add(new SpanTermQuery(term));
                    }
                }
                int positionGaps = 0;
                int position = 0;
                SpanQuery[] clauses = new SpanQuery[distinctPositions];
                for (int i = 0; i < disjunctLists.length; ++i) {
                    List disjuncts = disjunctLists[i];
                    if (disjuncts != null) {
                        clauses[position++] = new SpanOrQuery(disjuncts.toArray(new SpanQuery[disjuncts.size()]));
                        continue;
                    }
                    ++positionGaps;
                }
                int slop = mpq.getSlop();
                boolean inorder = slop == 0;
                SpanNearQuery sp = new SpanNearQuery(clauses, slop + positionGaps, inorder);
                sp.setBoost(query.getBoost());
                this.getPayloads(payloads, sp);
            }
        }
    }

    private void getPayloads(Collection<byte[]> payloads, SpanQuery query) throws IOException {
        Spans spans = query.getSpans(this.reader);
        while (spans.next()) {
            if (!spans.isPayloadAvailable()) continue;
            Collection<byte[]> payload = spans.getPayload();
            for (byte[] bytes : payload) {
                payloads.add(bytes);
            }
        }
    }
}

