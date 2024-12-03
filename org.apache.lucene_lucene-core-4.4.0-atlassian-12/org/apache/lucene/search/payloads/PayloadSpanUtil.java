/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.payloads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;

public class PayloadSpanUtil {
    private IndexReaderContext context;

    public PayloadSpanUtil(IndexReaderContext context) {
        this.context = context;
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
        HashMap<Term, TermContext> termContexts = new HashMap<Term, TermContext>();
        TreeSet<Term> terms = new TreeSet<Term>();
        query.extractTerms(terms);
        for (Term term : terms) {
            termContexts.put(term, TermContext.build(this.context, term, true));
        }
        for (AtomicReaderContext atomicReaderContext : this.context.leaves()) {
            Spans spans = query.getSpans(atomicReaderContext, atomicReaderContext.reader().getLiveDocs(), termContexts);
            while (spans.next()) {
                if (!spans.isPayloadAvailable()) continue;
                Collection<byte[]> payload = spans.getPayload();
                for (byte[] bytes : payload) {
                    payloads.add(bytes);
                }
            }
        }
    }
}

