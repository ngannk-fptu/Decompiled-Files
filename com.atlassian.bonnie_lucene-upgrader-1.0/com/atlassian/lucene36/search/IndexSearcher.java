/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.FieldDoc;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.search.FilteredQuery;
import com.atlassian.lucene36.search.HitQueue;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.ScoreDoc;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Sort;
import com.atlassian.lucene36.search.TopDocs;
import com.atlassian.lucene36.search.TopFieldCollector;
import com.atlassian.lucene36.search.TopFieldDocs;
import com.atlassian.lucene36.search.TopScoreDocCollector;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.util.ReaderUtil;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class IndexSearcher
extends Searcher {
    IndexReader reader;
    private boolean closeReader;
    protected final IndexReader[] subReaders;
    protected final int[] docStarts;
    private final ExecutorService executor;
    protected final IndexSearcher[] subSearchers;
    private final int docBase;
    private boolean fieldSortDoTrackScores;
    private boolean fieldSortDoMaxScore;

    @Deprecated
    public IndexSearcher(Directory path) throws CorruptIndexException, IOException {
        this(IndexReader.open(path, true), true, null);
    }

    @Deprecated
    public IndexSearcher(Directory path, boolean readOnly) throws CorruptIndexException, IOException {
        this(IndexReader.open(path, readOnly), true, null);
    }

    public IndexSearcher(IndexReader r) {
        this(r, false, null);
    }

    public IndexSearcher(IndexReader r, ExecutorService executor) {
        this(r, false, executor);
    }

    public IndexSearcher(IndexReader reader, IndexReader[] subReaders, int[] docStarts) {
        this(reader, subReaders, docStarts, null);
    }

    private IndexSearcher(IndexReader r, int docBase) {
        this.reader = r;
        this.executor = null;
        this.closeReader = false;
        this.docBase = docBase;
        this.subReaders = new IndexReader[]{r};
        this.docStarts = new int[]{0};
        this.subSearchers = null;
    }

    public IndexSearcher(IndexReader reader, IndexReader[] subReaders, int[] docStarts, ExecutorService executor) {
        this.reader = reader;
        this.subReaders = subReaders;
        this.docStarts = docStarts;
        if (executor == null) {
            this.subSearchers = null;
        } else {
            this.subSearchers = new IndexSearcher[subReaders.length];
            for (int i = 0; i < subReaders.length; ++i) {
                this.subSearchers[i] = new IndexSearcher(subReaders[i], docStarts[i]);
            }
        }
        this.closeReader = false;
        this.executor = executor;
        this.docBase = 0;
    }

    private IndexSearcher(IndexReader r, boolean closeReader, ExecutorService executor) {
        int i;
        this.reader = r;
        this.executor = executor;
        this.closeReader = closeReader;
        ArrayList<IndexReader> subReadersList = new ArrayList<IndexReader>();
        this.gatherSubReaders(subReadersList, this.reader);
        this.subReaders = subReadersList.toArray(new IndexReader[subReadersList.size()]);
        this.docStarts = new int[this.subReaders.length];
        int maxDoc = 0;
        for (i = 0; i < this.subReaders.length; ++i) {
            this.docStarts[i] = maxDoc;
            maxDoc += this.subReaders[i].maxDoc();
        }
        if (executor == null) {
            this.subSearchers = null;
        } else {
            this.subSearchers = new IndexSearcher[this.subReaders.length];
            for (i = 0; i < this.subReaders.length; ++i) {
                this.subSearchers[i] = new IndexSearcher(this.subReaders[i], this.docStarts[i]);
            }
        }
        this.docBase = 0;
    }

    protected void gatherSubReaders(List<IndexReader> allSubReaders, IndexReader r) {
        ReaderUtil.gatherSubReaders(allSubReaders, r);
    }

    public IndexReader getIndexReader() {
        return this.reader;
    }

    public IndexReader[] getSubReaders() {
        return this.subReaders;
    }

    @Override
    public int maxDoc() {
        return this.reader.maxDoc();
    }

    @Override
    public int docFreq(final Term term) throws IOException {
        if (this.executor == null) {
            return this.reader.docFreq(term);
        }
        ExecutionHelper<Integer> runner = new ExecutionHelper<Integer>(this.executor);
        for (int i = 0; i < this.subReaders.length; ++i) {
            final IndexSearcher searchable = this.subSearchers[i];
            runner.submit(new Callable<Integer>(){

                @Override
                public Integer call() throws IOException {
                    return searchable.docFreq(term);
                }
            });
        }
        int docFreq = 0;
        for (Integer num : runner) {
            docFreq += num.intValue();
        }
        return docFreq;
    }

    @Override
    public Document doc(int docID) throws CorruptIndexException, IOException {
        return this.reader.document(docID);
    }

    @Override
    public Document doc(int docID, FieldSelector fieldSelector) throws CorruptIndexException, IOException {
        return this.reader.document(docID, fieldSelector);
    }

    @Override
    public void setSimilarity(Similarity similarity) {
        super.setSimilarity(similarity);
    }

    @Override
    public Similarity getSimilarity() {
        return super.getSimilarity();
    }

    @Override
    public void close() throws IOException {
        if (this.closeReader) {
            this.reader.close();
        }
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, int n) throws IOException {
        return this.searchAfter(after, query, null, n);
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, Filter filter, int n) throws IOException {
        return this.search(this.createNormalizedWeight(query), filter, after, n);
    }

    @Override
    public TopDocs search(Query query, int n) throws IOException {
        return this.search(query, null, n);
    }

    @Override
    public TopDocs search(Query query, Filter filter, int n) throws IOException {
        return this.search(this.createNormalizedWeight(query), filter, n);
    }

    @Override
    public void search(Query query, Filter filter, Collector results) throws IOException {
        this.search(this.createNormalizedWeight(query), filter, results);
    }

    @Override
    public void search(Query query, Collector results) throws IOException {
        this.search(this.createNormalizedWeight(query), null, results);
    }

    @Override
    public TopFieldDocs search(Query query, Filter filter, int n, Sort sort) throws IOException {
        return this.search(this.createNormalizedWeight(query), filter, n, sort);
    }

    @Override
    public TopFieldDocs search(Query query, int n, Sort sort) throws IOException {
        return this.search(this.createNormalizedWeight(query), null, n, sort);
    }

    @Override
    public TopDocs search(Weight weight, Filter filter, int nDocs) throws IOException {
        return this.search(weight, filter, null, nDocs);
    }

    protected TopDocs search(Weight weight, Filter filter, ScoreDoc after, int nDocs) throws IOException {
        if (this.executor == null) {
            int limit = this.reader.maxDoc();
            if (limit == 0) {
                limit = 1;
            }
            nDocs = Math.min(nDocs, limit);
            TopScoreDocCollector collector = TopScoreDocCollector.create(nDocs, after, !weight.scoresDocsOutOfOrder());
            this.search(weight, filter, (Collector)collector);
            return collector.topDocs();
        }
        HitQueue hq = new HitQueue(nDocs, false);
        ReentrantLock lock = new ReentrantLock();
        ExecutionHelper<TopDocs> runner = new ExecutionHelper<TopDocs>(this.executor);
        for (int i = 0; i < this.subReaders.length; ++i) {
            runner.submit(new MultiSearcherCallableNoSort(lock, this.subSearchers[i], weight, filter, after, nDocs, hq));
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

    @Override
    public TopFieldDocs search(Weight weight, Filter filter, int nDocs, Sort sort) throws IOException {
        return this.search(weight, filter, nDocs, sort, true);
    }

    protected TopFieldDocs search(Weight weight, Filter filter, int nDocs, Sort sort, boolean fillFields) throws IOException {
        if (sort == null) {
            throw new NullPointerException();
        }
        if (this.executor == null) {
            int limit = this.reader.maxDoc();
            if (limit == 0) {
                limit = 1;
            }
            nDocs = Math.min(nDocs, limit);
            TopFieldCollector collector = TopFieldCollector.create(sort, nDocs, fillFields, this.fieldSortDoTrackScores, this.fieldSortDoMaxScore, !weight.scoresDocsOutOfOrder());
            this.search(weight, filter, (Collector)collector);
            return (TopFieldDocs)collector.topDocs();
        }
        TopFieldCollector topCollector = TopFieldCollector.create(sort, nDocs, fillFields, this.fieldSortDoTrackScores, this.fieldSortDoMaxScore, false);
        ReentrantLock lock = new ReentrantLock();
        ExecutionHelper<TopFieldDocs> runner = new ExecutionHelper<TopFieldDocs>(this.executor);
        for (int i = 0; i < this.subReaders.length; ++i) {
            runner.submit(new MultiSearcherCallableWithSort(lock, this.subSearchers[i], weight, filter, nDocs, topCollector, sort));
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

    @Override
    public void search(Weight weight, Filter filter, Collector collector) throws IOException {
        for (int i = 0; i < this.subReaders.length; ++i) {
            Scorer scorer;
            collector.setNextReader(this.subReaders[i], this.docBase + this.docStarts[i]);
            Scorer scorer2 = filter == null ? weight.scorer(this.subReaders[i], !collector.acceptsDocsOutOfOrder(), true) : (scorer = FilteredQuery.getFilteredScorer(this.subReaders[i], this.getSimilarity(), weight, weight, filter));
            if (scorer == null) continue;
            scorer.score(collector);
        }
    }

    @Override
    public Query rewrite(Query original) throws IOException {
        Query query = original;
        Query rewrittenQuery = query.rewrite(this.reader);
        while (rewrittenQuery != query) {
            query = rewrittenQuery;
            rewrittenQuery = query.rewrite(this.reader);
        }
        return query;
    }

    @Override
    public Explanation explain(Query query, int doc) throws IOException {
        return this.explain(this.createNormalizedWeight(query), doc);
    }

    @Override
    public Explanation explain(Weight weight, int doc) throws IOException {
        int n = ReaderUtil.subIndex(doc, this.docStarts);
        int deBasedDoc = doc - this.docStarts[n];
        return weight.explain(this.subReaders[n], deBasedDoc);
    }

    public void setDefaultFieldSortScoring(boolean doTrackScores, boolean doMaxScore) {
        this.fieldSortDoTrackScores = doTrackScores;
        this.fieldSortDoMaxScore = doMaxScore;
        if (this.subSearchers != null) {
            for (IndexSearcher sub : this.subSearchers) {
                sub.setDefaultFieldSortScoring(doTrackScores, doMaxScore);
            }
        }
    }

    @Override
    public Weight createNormalizedWeight(Query query) throws IOException {
        return super.createNormalizedWeight(query);
    }

    public String toString() {
        return "IndexSearcher(" + this.reader + ")";
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
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

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                try {
                    T t = this.service.take().get();
                    Object var3_4 = null;
                    --this.numTasks;
                    return t;
                }
                catch (InterruptedException e) {
                    throw new ThreadInterruptedException(e);
                }
                catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            catch (Throwable throwable) {
                Object var3_5 = null;
                --this.numTasks;
                throw throwable;
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class MultiSearcherCallableWithSort
    implements Callable<TopFieldDocs> {
        private final Lock lock;
        private final IndexSearcher searchable;
        private final Weight weight;
        private final Filter filter;
        private final int nDocs;
        private final TopFieldCollector hq;
        private final Sort sort;
        private final FakeScorer fakeScorer = new FakeScorer();

        public MultiSearcherCallableWithSort(Lock lock, IndexSearcher searchable, Weight weight, Filter filter, int nDocs, TopFieldCollector hq, Sort sort) {
            this.lock = lock;
            this.searchable = searchable;
            this.weight = weight;
            this.filter = filter;
            this.nDocs = nDocs;
            this.hq = hq;
            this.sort = sort;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopFieldDocs call() throws IOException {
            TopFieldDocs docs = this.searchable.search(this.weight, this.filter, this.nDocs, this.sort);
            for (int j = 0; j < docs.fields.length; ++j) {
                if (docs.fields[j].getType() != 1) continue;
                for (int j2 = 0; j2 < docs.scoreDocs.length; ++j2) {
                    FieldDoc fd = (FieldDoc)docs.scoreDocs[j2];
                    fd.fields[j] = (int)((Integer)fd.fields[j]);
                }
                break;
            }
            this.lock.lock();
            try {
                this.hq.setNextReader(this.searchable.getIndexReader(), this.searchable.docBase);
                this.hq.setScorer(this.fakeScorer);
                for (ScoreDoc scoreDoc : docs.scoreDocs) {
                    int docID;
                    this.fakeScorer.doc = docID = scoreDoc.doc - this.searchable.docBase;
                    this.fakeScorer.score = scoreDoc.score;
                    this.hq.collect(docID);
                }
                Object var8_9 = null;
                this.lock.unlock();
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                this.lock.unlock();
                throw throwable;
            }
            return docs;
        }

        private final class FakeScorer
        extends Scorer {
            float score;
            int doc;

            public FakeScorer() {
                super(null, null);
            }

            public int advance(int target) {
                throw new UnsupportedOperationException();
            }

            public int docID() {
                return this.doc;
            }

            public float freq() {
                throw new UnsupportedOperationException();
            }

            public int nextDoc() {
                throw new UnsupportedOperationException();
            }

            public float score() {
                return this.score;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class MultiSearcherCallableNoSort
    implements Callable<TopDocs> {
        private final Lock lock;
        private final IndexSearcher searchable;
        private final Weight weight;
        private final Filter filter;
        private final ScoreDoc after;
        private final int nDocs;
        private final HitQueue hq;

        public MultiSearcherCallableNoSort(Lock lock, IndexSearcher searchable, Weight weight, Filter filter, ScoreDoc after, int nDocs, HitQueue hq) {
            this.lock = lock;
            this.searchable = searchable;
            this.weight = weight;
            this.filter = filter;
            this.after = after;
            this.nDocs = nDocs;
            this.hq = hq;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TopDocs call() throws IOException {
            TopDocs docs = this.after == null ? this.searchable.search(this.weight, this.filter, this.nDocs) : this.searchable.search(this.weight, this.filter, this.after, this.nDocs);
            ScoreDoc[] scoreDocs = docs.scoreDocs;
            this.lock.lock();
            try {
                ScoreDoc scoreDoc;
                for (int j = 0; j < scoreDocs.length && (scoreDoc = scoreDocs[j]) != this.hq.insertWithOverflow(scoreDoc); ++j) {
                }
                Object var6_5 = null;
                this.lock.unlock();
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                this.lock.unlock();
                throw throwable;
            }
            return docs;
        }
    }
}

