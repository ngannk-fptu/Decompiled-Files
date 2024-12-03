/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.CachingTokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.BinaryDocValues
 *  org.apache.lucene.index.FieldInfos
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.FilterAtomicReader
 *  org.apache.lucene.index.FilterAtomicReader$FilterFields
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexReaderContext
 *  org.apache.lucene.index.NumericDocValues
 *  org.apache.lucene.index.SortedDocValues
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.TermContext
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.memory.MemoryIndex
 *  org.apache.lucene.queries.CommonTermsQuery
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.ConstantScoreQuery
 *  org.apache.lucene.search.DisjunctionMaxQuery
 *  org.apache.lucene.search.FilteredQuery
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.MultiPhraseQuery
 *  org.apache.lucene.search.MultiTermQuery
 *  org.apache.lucene.search.PhraseQuery
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 *  org.apache.lucene.search.spans.FieldMaskingSpanQuery
 *  org.apache.lucene.search.spans.SpanFirstQuery
 *  org.apache.lucene.search.spans.SpanNearQuery
 *  org.apache.lucene.search.spans.SpanNotQuery
 *  org.apache.lucene.search.spans.SpanOrQuery
 *  org.apache.lucene.search.spans.SpanQuery
 *  org.apache.lucene.search.spans.SpanTermQuery
 *  org.apache.lucene.search.spans.Spans
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.IOUtils
 */
package org.apache.lucene.search.highlight;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.FilterAtomicReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.queries.CommonTermsQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.OffsetLimitTokenFilter;
import org.apache.lucene.search.highlight.PositionSpan;
import org.apache.lucene.search.highlight.WeightedSpanTerm;
import org.apache.lucene.search.spans.FieldMaskingSpanQuery;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.IOUtils;

public class WeightedSpanTermExtractor {
    private String fieldName;
    private TokenStream tokenStream;
    private String defaultField;
    private boolean expandMultiTermQuery;
    private boolean cachedTokenStream;
    private boolean wrapToCaching = true;
    private int maxDocCharsToAnalyze;
    private AtomicReader internalReader = null;

    public WeightedSpanTermExtractor() {
    }

    public WeightedSpanTermExtractor(String defaultField) {
        if (defaultField != null) {
            this.defaultField = defaultField;
        }
    }

    protected void extract(Query query, Map<String, WeightedSpanTerm> terms) throws IOException {
        if (query instanceof BooleanQuery) {
            BooleanClause[] queryClauses = ((BooleanQuery)query).getClauses();
            for (int i = 0; i < queryClauses.length; ++i) {
                if (queryClauses[i].isProhibited()) continue;
                this.extract(queryClauses[i].getQuery(), terms);
            }
        } else if (query instanceof PhraseQuery) {
            PhraseQuery phraseQuery = (PhraseQuery)query;
            Term[] phraseQueryTerms = phraseQuery.getTerms();
            SpanQuery[] clauses = new SpanQuery[phraseQueryTerms.length];
            for (int i = 0; i < phraseQueryTerms.length; ++i) {
                clauses[i] = new SpanTermQuery(phraseQueryTerms[i]);
            }
            int slop = phraseQuery.getSlop();
            int[] positions = phraseQuery.getPositions();
            if (positions.length > 0) {
                int lastPos = positions[0];
                int largestInc = 0;
                int sz = positions.length;
                for (int i = 1; i < sz; ++i) {
                    int pos = positions[i];
                    int inc = pos - lastPos;
                    if (inc > largestInc) {
                        largestInc = inc;
                    }
                    lastPos = pos;
                }
                if (largestInc > 1) {
                    slop += largestInc;
                }
            }
            boolean inorder = false;
            if (slop == 0) {
                inorder = true;
            }
            SpanNearQuery sp = new SpanNearQuery(clauses, slop, inorder);
            sp.setBoost(query.getBoost());
            this.extractWeightedSpanTerms(terms, (SpanQuery)sp);
        } else if (query instanceof TermQuery) {
            this.extractWeightedTerms(terms, query);
        } else if (query instanceof SpanQuery) {
            this.extractWeightedSpanTerms(terms, (SpanQuery)query);
        } else if (query instanceof FilteredQuery) {
            this.extract(((FilteredQuery)query).getQuery(), terms);
        } else if (query instanceof ConstantScoreQuery) {
            Query q = ((ConstantScoreQuery)query).getQuery();
            if (q != null) {
                this.extract(q, terms);
            }
        } else if (query instanceof CommonTermsQuery) {
            this.extractWeightedTerms(terms, query);
        } else if (query instanceof DisjunctionMaxQuery) {
            Iterator iterator = ((DisjunctionMaxQuery)query).iterator();
            while (iterator.hasNext()) {
                this.extract((Query)iterator.next(), terms);
            }
        } else if (query instanceof MultiPhraseQuery) {
            MultiPhraseQuery mpq = (MultiPhraseQuery)query;
            List termArrays = mpq.getTermArrays();
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
                    Term[] termArray = (Term[])termArrays.get(i);
                    ArrayList<SpanTermQuery> disjuncts = disjunctLists[positions[i]];
                    if (disjuncts == null) {
                        ArrayList<SpanTermQuery> arrayList = new ArrayList<SpanTermQuery>(termArray.length);
                        disjunctLists[positions[i]] = arrayList;
                        disjuncts = arrayList;
                        ++distinctPositions;
                    }
                    for (int j = 0; j < termArray.length; ++j) {
                        disjuncts.add(new SpanTermQuery(termArray[j]));
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
                this.extractWeightedSpanTerms(terms, (SpanQuery)sp);
            }
        } else {
            AtomicReader reader;
            Query rewritten;
            Query origQuery = query;
            if (query instanceof MultiTermQuery) {
                if (!this.expandMultiTermQuery) {
                    return;
                }
                MultiTermQuery copy = (MultiTermQuery)query.clone();
                copy.setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);
                origQuery = copy;
            }
            if ((rewritten = origQuery.rewrite((IndexReader)(reader = this.getLeafContext().reader()))) != origQuery) {
                this.extract(rewritten, terms);
            }
        }
        this.extractUnknownQuery(query, terms);
    }

    protected void extractUnknownQuery(Query query, Map<String, WeightedSpanTerm> terms) throws IOException {
    }

    protected void extractWeightedSpanTerms(Map<String, WeightedSpanTerm> terms, SpanQuery spanQuery) throws IOException {
        HashSet<String> fieldNames;
        if (this.fieldName == null) {
            fieldNames = new HashSet<String>();
            this.collectSpanQueryFields(spanQuery, fieldNames);
        } else {
            fieldNames = new HashSet(1);
            fieldNames.add(this.fieldName);
        }
        if (this.defaultField != null) {
            fieldNames.add(this.defaultField);
        }
        HashMap<String, SpanQuery> queries = new HashMap<String, SpanQuery>();
        HashSet nonWeightedTerms = new HashSet();
        boolean mustRewriteQuery = this.mustRewriteQuery(spanQuery);
        if (mustRewriteQuery) {
            for (String field : fieldNames) {
                SpanQuery rewrittenQuery = (SpanQuery)spanQuery.rewrite((IndexReader)this.getLeafContext().reader());
                queries.put(field, rewrittenQuery);
                rewrittenQuery.extractTerms(nonWeightedTerms);
            }
        } else {
            spanQuery.extractTerms(nonWeightedTerms);
        }
        ArrayList<PositionSpan> spanPositions = new ArrayList<PositionSpan>();
        for (String field : fieldNames) {
            SpanQuery q = mustRewriteQuery ? (SpanQuery)queries.get(field) : spanQuery;
            AtomicReaderContext context = this.getLeafContext();
            HashMap<Term, TermContext> termContexts = new HashMap<Term, TermContext>();
            TreeSet extractedTerms = new TreeSet();
            q.extractTerms(extractedTerms);
            for (Term term : extractedTerms) {
                termContexts.put(term, TermContext.build((IndexReaderContext)context, (Term)term, (boolean)true));
            }
            Bits acceptDocs = context.reader().getLiveDocs();
            Spans spans = q.getSpans(context, acceptDocs, termContexts);
            while (spans.next()) {
                spanPositions.add(new PositionSpan(spans.start(), spans.end() - 1));
            }
        }
        if (spanPositions.size() == 0) {
            return;
        }
        for (Term queryTerm : nonWeightedTerms) {
            if (!this.fieldNameComparator(queryTerm.field())) continue;
            WeightedSpanTerm weightedSpanTerm = terms.get(queryTerm.text());
            if (weightedSpanTerm == null) {
                weightedSpanTerm = new WeightedSpanTerm(spanQuery.getBoost(), queryTerm.text());
                weightedSpanTerm.addPositionSpans(spanPositions);
                weightedSpanTerm.positionSensitive = true;
                terms.put(queryTerm.text(), weightedSpanTerm);
                continue;
            }
            if (spanPositions.size() <= 0) continue;
            weightedSpanTerm.addPositionSpans(spanPositions);
        }
    }

    protected void extractWeightedTerms(Map<String, WeightedSpanTerm> terms, Query query) throws IOException {
        HashSet nonWeightedTerms = new HashSet();
        query.extractTerms(nonWeightedTerms);
        for (Term queryTerm : nonWeightedTerms) {
            if (!this.fieldNameComparator(queryTerm.field())) continue;
            WeightedSpanTerm weightedSpanTerm = new WeightedSpanTerm(query.getBoost(), queryTerm.text());
            terms.put(queryTerm.text(), weightedSpanTerm);
        }
    }

    protected boolean fieldNameComparator(String fieldNameToCheck) {
        boolean rv = this.fieldName == null || this.fieldName.equals(fieldNameToCheck) || this.defaultField != null && this.defaultField.equals(fieldNameToCheck);
        return rv;
    }

    protected AtomicReaderContext getLeafContext() throws IOException {
        if (this.internalReader == null) {
            if (this.wrapToCaching && !(this.tokenStream instanceof CachingTokenFilter)) {
                assert (!this.cachedTokenStream);
                this.tokenStream = new CachingTokenFilter((TokenStream)new OffsetLimitTokenFilter(this.tokenStream, this.maxDocCharsToAnalyze));
                this.cachedTokenStream = true;
            }
            MemoryIndex indexer = new MemoryIndex(true);
            indexer.addField("shadowed_field", this.tokenStream);
            this.tokenStream.reset();
            IndexSearcher searcher = indexer.createSearcher();
            this.internalReader = new DelegatingAtomicReader(((AtomicReaderContext)searcher.getTopReaderContext()).reader());
        }
        return this.internalReader.getContext();
    }

    public Map<String, WeightedSpanTerm> getWeightedSpanTerms(Query query, TokenStream tokenStream) throws IOException {
        return this.getWeightedSpanTerms(query, tokenStream, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, WeightedSpanTerm> getWeightedSpanTerms(Query query, TokenStream tokenStream, String fieldName) throws IOException {
        this.fieldName = fieldName != null ? fieldName : null;
        PositionCheckingMap<String> terms = new PositionCheckingMap<String>();
        this.tokenStream = tokenStream;
        try {
            this.extract(query, terms);
        }
        catch (Throwable throwable) {
            IOUtils.close((Closeable[])new Closeable[]{this.internalReader});
            throw throwable;
        }
        IOUtils.close((Closeable[])new Closeable[]{this.internalReader});
        return terms;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, WeightedSpanTerm> getWeightedSpanTermsWithScores(Query query, TokenStream tokenStream, String fieldName, IndexReader reader) throws IOException {
        this.fieldName = fieldName != null ? fieldName : null;
        this.tokenStream = tokenStream;
        PositionCheckingMap<String> terms = new PositionCheckingMap<String>();
        this.extract(query, terms);
        int totalNumDocs = reader.maxDoc();
        Set weightedTerms = terms.keySet();
        Iterator it = weightedTerms.iterator();
        try {
            while (it.hasNext()) {
                WeightedSpanTerm weightedSpanTerm = (WeightedSpanTerm)terms.get(it.next());
                int docFreq = reader.docFreq(new Term(fieldName, weightedSpanTerm.term));
                float idf = (float)(Math.log((double)totalNumDocs / (double)(docFreq + 1)) + 1.0);
                weightedSpanTerm.weight *= idf;
            }
        }
        catch (Throwable throwable) {
            IOUtils.close((Closeable[])new Closeable[]{this.internalReader});
            throw throwable;
        }
        IOUtils.close((Closeable[])new Closeable[]{this.internalReader});
        return terms;
    }

    protected void collectSpanQueryFields(SpanQuery spanQuery, Set<String> fieldNames) {
        if (spanQuery instanceof FieldMaskingSpanQuery) {
            this.collectSpanQueryFields(((FieldMaskingSpanQuery)spanQuery).getMaskedQuery(), fieldNames);
        } else if (spanQuery instanceof SpanFirstQuery) {
            this.collectSpanQueryFields(((SpanFirstQuery)spanQuery).getMatch(), fieldNames);
        } else if (spanQuery instanceof SpanNearQuery) {
            for (SpanQuery clause : ((SpanNearQuery)spanQuery).getClauses()) {
                this.collectSpanQueryFields(clause, fieldNames);
            }
        } else if (spanQuery instanceof SpanNotQuery) {
            this.collectSpanQueryFields(((SpanNotQuery)spanQuery).getInclude(), fieldNames);
        } else if (spanQuery instanceof SpanOrQuery) {
            for (SpanQuery clause : ((SpanOrQuery)spanQuery).getClauses()) {
                this.collectSpanQueryFields(clause, fieldNames);
            }
        } else {
            fieldNames.add(spanQuery.getField());
        }
    }

    protected boolean mustRewriteQuery(SpanQuery spanQuery) {
        if (!this.expandMultiTermQuery) {
            return false;
        }
        if (spanQuery instanceof FieldMaskingSpanQuery) {
            return this.mustRewriteQuery(((FieldMaskingSpanQuery)spanQuery).getMaskedQuery());
        }
        if (spanQuery instanceof SpanFirstQuery) {
            return this.mustRewriteQuery(((SpanFirstQuery)spanQuery).getMatch());
        }
        if (spanQuery instanceof SpanNearQuery) {
            for (SpanQuery clause : ((SpanNearQuery)spanQuery).getClauses()) {
                if (!this.mustRewriteQuery(clause)) continue;
                return true;
            }
            return false;
        }
        if (spanQuery instanceof SpanNotQuery) {
            SpanNotQuery spanNotQuery = (SpanNotQuery)spanQuery;
            return this.mustRewriteQuery(spanNotQuery.getInclude()) || this.mustRewriteQuery(spanNotQuery.getExclude());
        }
        if (spanQuery instanceof SpanOrQuery) {
            for (SpanQuery clause : ((SpanOrQuery)spanQuery).getClauses()) {
                if (!this.mustRewriteQuery(clause)) continue;
                return true;
            }
            return false;
        }
        return !(spanQuery instanceof SpanTermQuery);
    }

    public boolean getExpandMultiTermQuery() {
        return this.expandMultiTermQuery;
    }

    public void setExpandMultiTermQuery(boolean expandMultiTermQuery) {
        this.expandMultiTermQuery = expandMultiTermQuery;
    }

    public boolean isCachedTokenStream() {
        return this.cachedTokenStream;
    }

    public TokenStream getTokenStream() {
        return this.tokenStream;
    }

    public void setWrapIfNotCachingTokenFilter(boolean wrap) {
        this.wrapToCaching = wrap;
    }

    protected final void setMaxDocCharsToAnalyze(int maxDocCharsToAnalyze) {
        this.maxDocCharsToAnalyze = maxDocCharsToAnalyze;
    }

    protected static class PositionCheckingMap<K>
    extends HashMap<K, WeightedSpanTerm> {
        protected PositionCheckingMap() {
        }

        @Override
        public void putAll(Map<? extends K, ? extends WeightedSpanTerm> m) {
            for (Map.Entry<K, WeightedSpanTerm> entry : m.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public WeightedSpanTerm put(K key, WeightedSpanTerm value) {
            WeightedSpanTerm prev = super.put(key, value);
            if (prev == null) {
                return prev;
            }
            WeightedSpanTerm prevTerm = prev;
            WeightedSpanTerm newTerm = value;
            if (!prevTerm.positionSensitive) {
                newTerm.positionSensitive = false;
            }
            return prev;
        }
    }

    static final class DelegatingAtomicReader
    extends FilterAtomicReader {
        private static final String FIELD_NAME = "shadowed_field";

        DelegatingAtomicReader(AtomicReader in) {
            super(in);
        }

        public FieldInfos getFieldInfos() {
            throw new UnsupportedOperationException();
        }

        public Fields fields() throws IOException {
            return new FilterAtomicReader.FilterFields(super.fields()){

                public Terms terms(String field) throws IOException {
                    return super.terms(DelegatingAtomicReader.FIELD_NAME);
                }

                public Iterator<String> iterator() {
                    return Collections.singletonList(DelegatingAtomicReader.FIELD_NAME).iterator();
                }

                public int size() {
                    return 1;
                }
            };
        }

        public NumericDocValues getNumericDocValues(String field) throws IOException {
            return super.getNumericDocValues(FIELD_NAME);
        }

        public BinaryDocValues getBinaryDocValues(String field) throws IOException {
            return super.getBinaryDocValues(FIELD_NAME);
        }

        public SortedDocValues getSortedDocValues(String field) throws IOException {
            return super.getSortedDocValues(FIELD_NAME);
        }

        public NumericDocValues getNormValues(String field) throws IOException {
            return super.getNormValues(FIELD_NAME);
        }
    }
}

