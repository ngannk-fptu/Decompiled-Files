/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.CollectionTerminatedException;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.HitQueue;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.ThreadInterruptedException;

public class IndexSearcher {
    final IndexReader reader;
    protected final IndexReaderContext readerContext;
    protected final List<AtomicReaderContext> leafContexts;
    protected final LeafSlice[] leafSlices;
    private final ExecutorService executor;
    private static final Similarity defaultSimilarity = new DefaultSimilarity();
    private Similarity similarity = defaultSimilarity;

    public static Similarity getDefaultSimilarity() {
        return defaultSimilarity;
    }

    public IndexSearcher(IndexReader r) {
        this(r, null);
    }

    public IndexSearcher(IndexReader r, ExecutorService executor) {
        this(r.getContext(), executor);
    }

    public IndexSearcher(IndexReaderContext context, ExecutorService executor) {
        assert (context.isTopLevel) : "IndexSearcher's ReaderContext must be topLevel for reader" + context.reader();
        this.reader = context.reader();
        this.executor = executor;
        this.readerContext = context;
        this.leafContexts = context.leaves();
        this.leafSlices = executor == null ? null : this.slices(this.leafContexts);
    }

    public IndexSearcher(IndexReaderContext context) {
        this(context, null);
    }

    protected LeafSlice[] slices(List<AtomicReaderContext> leaves) {
        LeafSlice[] slices = new LeafSlice[leaves.size()];
        for (int i = 0; i < slices.length; ++i) {
            slices[i] = new LeafSlice(leaves.get(i));
        }
        return slices;
    }

    public IndexReader getIndexReader() {
        return this.reader;
    }

    public Document doc(int docID) throws IOException {
        return this.reader.document(docID);
    }

    public void doc(int docID, StoredFieldVisitor fieldVisitor) throws IOException {
        this.reader.document(docID, fieldVisitor);
    }

    public Document doc(int docID, Set<String> fieldsToLoad) throws IOException {
        return this.reader.document(docID, fieldsToLoad);
    }

    @Deprecated
    public final Document document(int docID, Set<String> fieldsToLoad) throws IOException {
        return this.doc(docID, fieldsToLoad);
    }

    public void setSimilarity(Similarity similarity) {
        this.similarity = similarity;
    }

    public Similarity getSimilarity() {
        return this.similarity;
    }

    protected Query wrapFilter(Query query, Filter filter) {
        return filter == null ? query : new FilteredQuery(query, filter);
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, int n) throws IOException {
        return this.search(this.createNormalizedWeight(query), after, n);
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, Filter filter, int n) throws IOException {
        return this.search(this.createNormalizedWeight(this.wrapFilter(query, filter)), after, n);
    }

    public TopDocs search(Query query, int n) throws IOException {
        return this.search(query, null, n);
    }

    public TopDocs search(Query query, Filter filter, int n) throws IOException {
        return this.search(this.createNormalizedWeight(this.wrapFilter(query, filter)), null, n);
    }

    public void search(Query query, Filter filter, Collector results) throws IOException {
        this.search(this.leafContexts, this.createNormalizedWeight(this.wrapFilter(query, filter)), results);
    }

    public void search(Query query, Collector results) throws IOException {
        this.search(this.leafContexts, this.createNormalizedWeight(query), results);
    }

    public TopFieldDocs search(Query query, Filter filter, int n, Sort sort) throws IOException {
        return this.search(this.createNormalizedWeight(this.wrapFilter(query, filter)), n, sort, false, false);
    }

    public TopFieldDocs search(Query query, Filter filter, int n, Sort sort, boolean doDocScores, boolean doMaxScore) throws IOException {
        return this.search(this.createNormalizedWeight(this.wrapFilter(query, filter)), n, sort, doDocScores, doMaxScore);
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, Filter filter, int n, Sort sort) throws IOException {
        if (after != null && !(after instanceof FieldDoc)) {
            throw new IllegalArgumentException("after must be a FieldDoc; got " + after);
        }
        return this.search(this.createNormalizedWeight(this.wrapFilter(query, filter)), (FieldDoc)after, n, sort, true, false, false);
    }

    public TopFieldDocs search(Query query, int n, Sort sort) throws IOException {
        return this.search(this.createNormalizedWeight(query), n, sort, false, false);
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, int n, Sort sort) throws IOException {
        if (after != null && !(after instanceof FieldDoc)) {
            throw new IllegalArgumentException("after must be a FieldDoc; got " + after);
        }
        return this.search(this.createNormalizedWeight(query), (FieldDoc)after, n, sort, true, false, false);
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, Filter filter, int n, Sort sort, boolean doDocScores, boolean doMaxScore) throws IOException {
        if (after != null && !(after instanceof FieldDoc)) {
            throw new IllegalArgumentException("after must be a FieldDoc; got " + after);
        }
        return this.search(this.createNormalizedWeight(this.wrapFilter(query, filter)), (FieldDoc)after, n, sort, true, doDocScores, doMaxScore);
    }

    protected TopDocs search(Weight weight, ScoreDoc after, int nDocs) throws IOException {
        int limit = this.reader.maxDoc();
        if (limit == 0) {
            limit = 1;
        }
        nDocs = Math.min(nDocs, limit);
        if (this.executor == null) {
            return this.search(this.leafContexts, weight, after, nDocs);
        }
        HitQueue hq = new HitQueue(nDocs, false);
        ReentrantLock lock = new ReentrantLock();
        ExecutionHelper<TopDocs> runner = new ExecutionHelper<TopDocs>(this.executor);
        for (int i = 0; i < this.leafSlices.length; ++i) {
            runner.submit(new SearcherCallableNoSort(lock, this, this.leafSlices[i], weight, after, nDocs, hq));
        }
        int totalHits = 0;
        float maxScore = Float.NEGATIVE_INFINITY;
        for (TopDocs topDocs : runner) {
            if (topDocs.totalHits == 0) continue;
            totalHits += topDocs.totalHits;
            maxScore = Math.max(maxScore, topDocs.getMaxScore());
        }
        ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
        for (int i = hq.size() - 1; i >= 0; --i) {
            scoreDocs[i] = (ScoreDoc)hq.pop();
        }
        return new TopDocs(totalHits, scoreDocs, maxScore);
    }

    protected TopDocs search(List<AtomicReaderContext> leaves, Weight weight, ScoreDoc after, int nDocs) throws IOException {
        int limit = this.reader.maxDoc();
        if (limit == 0) {
            limit = 1;
        }
        nDocs = Math.min(nDocs, limit);
        TopScoreDocCollector collector = TopScoreDocCollector.create(nDocs, after, !weight.scoresDocsOutOfOrder());
        this.search(leaves, weight, (Collector)collector);
        return collector.topDocs();
    }

    protected TopFieldDocs search(Weight weight, int nDocs, Sort sort, boolean doDocScores, boolean doMaxScore) throws IOException {
        return this.search(weight, null, nDocs, sort, true, doDocScores, doMaxScore);
    }

    protected TopFieldDocs search(Weight weight, FieldDoc after, int nDocs, Sort sort, boolean fillFields, boolean doDocScores, boolean doMaxScore) throws IOException {
        if (sort == null) {
            throw new NullPointerException("Sort must not be null");
        }
        int limit = this.reader.maxDoc();
        if (limit == 0) {
            limit = 1;
        }
        nDocs = Math.min(nDocs, limit);
        if (this.executor == null) {
            return this.search(this.leafContexts, weight, after, nDocs, sort, fillFields, doDocScores, doMaxScore);
        }
        TopFieldCollector topCollector = TopFieldCollector.create(sort, nDocs, after, fillFields, doDocScores, doMaxScore, false);
        ReentrantLock lock = new ReentrantLock();
        ExecutionHelper<TopFieldDocs> runner = new ExecutionHelper<TopFieldDocs>(this.executor);
        for (int i = 0; i < this.leafSlices.length; ++i) {
            runner.submit(new SearcherCallableWithSort(lock, this, this.leafSlices[i], weight, after, nDocs, topCollector, sort, doDocScores, doMaxScore));
        }
        int totalHits = 0;
        float maxScore = Float.NEGATIVE_INFINITY;
        for (TopFieldDocs topFieldDocs : runner) {
            if (topFieldDocs.totalHits == 0) continue;
            totalHits += topFieldDocs.totalHits;
            maxScore = Math.max(maxScore, topFieldDocs.getMaxScore());
        }
        TopFieldDocs topDocs = (TopFieldDocs)topCollector.topDocs();
        return new TopFieldDocs(totalHits, topDocs.scoreDocs, topDocs.fields, topDocs.getMaxScore());
    }

    protected TopFieldDocs search(List<AtomicReaderContext> leaves, Weight weight, FieldDoc after, int nDocs, Sort sort, boolean fillFields, boolean doDocScores, boolean doMaxScore) throws IOException {
        int limit = this.reader.maxDoc();
        if (limit == 0) {
            limit = 1;
        }
        nDocs = Math.min(nDocs, limit);
        TopFieldCollector collector = TopFieldCollector.create(sort, nDocs, after, fillFields, doDocScores, doMaxScore, !weight.scoresDocsOutOfOrder());
        this.search(leaves, weight, (Collector)collector);
        return (TopFieldDocs)collector.topDocs();
    }

    protected void search(List<AtomicReaderContext> leaves, Weight weight, Collector collector) throws IOException {
        for (AtomicReaderContext ctx : leaves) {
            try {
                collector.setNextReader(ctx);
            }
            catch (CollectionTerminatedException e) {
                continue;
            }
            Scorer scorer = weight.scorer(ctx, !collector.acceptsDocsOutOfOrder(), true, ctx.reader().getLiveDocs());
            if (scorer == null) continue;
            try {
                scorer.score(collector);
            }
            catch (CollectionTerminatedException collectionTerminatedException) {}
        }
    }

    public Query rewrite(Query original) throws IOException {
        Query query = original;
        Query rewrittenQuery = query.rewrite(this.reader);
        while (rewrittenQuery != query) {
            query = rewrittenQuery;
            rewrittenQuery = query.rewrite(this.reader);
        }
        return query;
    }

    public Explanation explain(Query query, int doc) throws IOException {
        return this.explain(this.createNormalizedWeight(query), doc);
    }

    protected Explanation explain(Weight weight, int doc) throws IOException {
        int n = ReaderUtil.subIndex(doc, this.leafContexts);
        AtomicReaderContext ctx = this.leafContexts.get(n);
        int deBasedDoc = doc - ctx.docBase;
        return weight.explain(ctx, deBasedDoc);
    }

    public Weight createNormalizedWeight(Query query) throws IOException {
        query = this.rewrite(query);
        Weight weight = query.createWeight(this);
        float v = weight.getValueForNormalization();
        float norm = this.getSimilarity().queryNorm(v);
        if (Float.isInfinite(norm) || Float.isNaN(norm)) {
            norm = 1.0f;
        }
        weight.normalize(norm, 1.0f);
        return weight;
    }

    public IndexReaderContext getTopReaderContext() {
        return this.readerContext;
    }

    public String toString() {
        return "IndexSearcher(" + this.reader + "; executor=" + this.executor + ")";
    }

    public TermStatistics termStatistics(Term term, TermContext context) throws IOException {
        return new TermStatistics(term.bytes(), context.docFreq(), context.totalTermFreq());
    }

    public CollectionStatistics collectionStatistics(String field) throws IOException {
        long sumDocFreq;
        long sumTotalTermFreq;
        int docCount;
        assert (field != null);
        Terms terms = MultiFields.getTerms(this.reader, field);
        if (terms == null) {
            docCount = 0;
            sumTotalTermFreq = 0L;
            sumDocFreq = 0L;
        } else {
            docCount = terms.getDocCount();
            sumTotalTermFreq = terms.getSumTotalTermFreq();
            sumDocFreq = terms.getSumDocFreq();
        }
        return new CollectionStatistics(field, this.reader.maxDoc(), docCount, sumTotalTermFreq, sumDocFreq);
    }

    public static class LeafSlice {
        final AtomicReaderContext[] leaves;

        public LeafSlice(AtomicReaderContext ... leaves) {
            this.leaves = leaves;
        }
    }

    private static final class ExecutionHelper<T>
    implements Iterator<T>,
    Iterable<T> {
        private final CompletionService<T> service;
        private int numTasks;

        ExecutionHelper(Executor executor) {
            this.service = new ExecutorCompletionService<T>(executor);
        }

        @Override
        public boolean hasNext() {
            return this.numTasks > 0;
        }

        public void submit(Callable<T> task) {
            this.service.submit(task);
            ++this.numTasks;
        }

        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("next() is called but hasNext() returned false");
            }
            try {
                T t = this.service.take().get();
                return t;
            }
            catch (InterruptedException e) {
                throw new ThreadInterruptedException(e);
            }
            catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            finally {
                --this.numTasks;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }

    private static final class SearcherCallableWithSort
    implements Callable<TopFieldDocs> {
        private final Lock lock;
        private final IndexSearcher searcher;
        private final Weight weight;
        private final int nDocs;
        private final TopFieldCollector hq;
        private final Sort sort;
        private final LeafSlice slice;
        private final FieldDoc after;
        private final boolean doDocScores;
        private final boolean doMaxScore;
        private final FakeScorer fakeScorer = new FakeScorer();

        public SearcherCallableWithSort(Lock lock, IndexSearcher searcher, LeafSlice slice, Weight weight, FieldDoc after, int nDocs, TopFieldCollector hq, Sort sort, boolean doDocScores, boolean doMaxScore) {
            this.lock = lock;
            this.searcher = searcher;
            this.weight = weight;
            this.nDocs = nDocs;
            this.hq = hq;
            this.sort = sort;
            this.slice = slice;
            this.after = after;
            this.doDocScores = doDocScores;
            this.doMaxScore = doMaxScore;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopFieldDocs call() throws IOException {
            assert (this.slice.leaves.length == 1);
            TopFieldDocs docs = this.searcher.search(Arrays.asList(this.slice.leaves), this.weight, this.after, this.nDocs, this.sort, true, this.doDocScores || this.sort.needsScores(), this.doMaxScore);
            this.lock.lock();
            try {
                AtomicReaderContext ctx = this.slice.leaves[0];
                int base = ctx.docBase;
                this.hq.setNextReader(ctx);
                this.hq.setScorer(this.fakeScorer);
                for (ScoreDoc scoreDoc : docs.scoreDocs) {
                    this.fakeScorer.doc = scoreDoc.doc - base;
                    this.fakeScorer.score = scoreDoc.score;
                    this.hq.collect(scoreDoc.doc - base);
                }
                if (this.doMaxScore && docs.getMaxScore() > this.hq.maxScore) {
                    this.hq.maxScore = docs.getMaxScore();
                }
            }
            finally {
                this.lock.unlock();
            }
            return docs;
        }

        private final class FakeScorer
        extends Scorer {
            float score;
            int doc;

            public FakeScorer() {
                super(null);
            }

            @Override
            public int advance(int target) {
                throw new UnsupportedOperationException("FakeScorer doesn't support advance(int)");
            }

            @Override
            public int docID() {
                return this.doc;
            }

            @Override
            public int freq() {
                throw new UnsupportedOperationException("FakeScorer doesn't support freq()");
            }

            @Override
            public int nextDoc() {
                throw new UnsupportedOperationException("FakeScorer doesn't support nextDoc()");
            }

            @Override
            public float score() {
                return this.score;
            }

            @Override
            public long cost() {
                return 1L;
            }
        }
    }

    private static final class SearcherCallableNoSort
    implements Callable<TopDocs> {
        private final Lock lock;
        private final IndexSearcher searcher;
        private final Weight weight;
        private final ScoreDoc after;
        private final int nDocs;
        private final HitQueue hq;
        private final LeafSlice slice;

        public SearcherCallableNoSort(Lock lock, IndexSearcher searcher, LeafSlice slice, Weight weight, ScoreDoc after, int nDocs, HitQueue hq) {
            this.lock = lock;
            this.searcher = searcher;
            this.weight = weight;
            this.after = after;
            this.nDocs = nDocs;
            this.hq = hq;
            this.slice = slice;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopDocs call() throws IOException {
            TopDocs docs = this.searcher.search(Arrays.asList(this.slice.leaves), this.weight, this.after, this.nDocs);
            ScoreDoc[] scoreDocs = docs.scoreDocs;
            this.lock.lock();
            try {
                for (int j = 0; j < scoreDocs.length; ++j) {
                    ScoreDoc scoreDoc = scoreDocs[j];
                    if (scoreDoc != this.hq.insertWithOverflow(scoreDoc)) continue;
                    break;
                }
            }
            finally {
                this.lock.unlock();
            }
            return docs;
        }
    }
}

