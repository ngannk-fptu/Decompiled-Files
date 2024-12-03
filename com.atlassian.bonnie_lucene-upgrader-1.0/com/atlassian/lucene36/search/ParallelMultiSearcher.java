/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.FieldDocSortedHitQueue;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.search.HitQueue;
import com.atlassian.lucene36.search.MultiSearcher;
import com.atlassian.lucene36.search.ScoreDoc;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searchable;
import com.atlassian.lucene36.search.Sort;
import com.atlassian.lucene36.search.TopDocs;
import com.atlassian.lucene36.search.TopFieldDocs;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.util.NamedThreadFactory;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class ParallelMultiSearcher
extends MultiSearcher {
    private final ExecutorService executor;
    private final Searchable[] searchables;
    private final int[] starts;

    public ParallelMultiSearcher(Searchable ... searchables) throws IOException {
        this(Executors.newCachedThreadPool(new NamedThreadFactory(ParallelMultiSearcher.class.getSimpleName())), searchables);
    }

    public ParallelMultiSearcher(ExecutorService executor, Searchable ... searchables) throws IOException {
        super(searchables);
        this.searchables = searchables;
        this.starts = this.getStarts();
        this.executor = executor;
    }

    @Override
    public int docFreq(final Term term) throws IOException {
        ExecutionHelper<Integer> runner = new ExecutionHelper<Integer>(this.executor);
        for (int i = 0; i < this.searchables.length; ++i) {
            final Searchable searchable = this.searchables[i];
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
    public TopDocs search(Weight weight, Filter filter, int nDocs) throws IOException {
        HitQueue hq = new HitQueue(nDocs, false);
        ReentrantLock lock = new ReentrantLock();
        ExecutionHelper<TopDocs> runner = new ExecutionHelper<TopDocs>(this.executor);
        for (int i = 0; i < this.searchables.length; ++i) {
            runner.submit(new MultiSearcher.MultiSearcherCallableNoSort(lock, this.searchables[i], weight, filter, nDocs, hq, i, this.starts));
        }
        int totalHits = 0;
        float maxScore = Float.NEGATIVE_INFINITY;
        for (TopDocs topDocs : runner) {
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
        if (sort == null) {
            throw new NullPointerException();
        }
        FieldDocSortedHitQueue hq = new FieldDocSortedHitQueue(nDocs);
        ReentrantLock lock = new ReentrantLock();
        ExecutionHelper<TopFieldDocs> runner = new ExecutionHelper<TopFieldDocs>(this.executor);
        for (int i = 0; i < this.searchables.length; ++i) {
            runner.submit(new MultiSearcher.MultiSearcherCallableWithSort(lock, this.searchables[i], weight, filter, nDocs, hq, sort, i, this.starts));
        }
        int totalHits = 0;
        float maxScore = Float.NEGATIVE_INFINITY;
        for (TopFieldDocs topFieldDocs : runner) {
            totalHits += topFieldDocs.totalHits;
            maxScore = Math.max(maxScore, topFieldDocs.getMaxScore());
        }
        ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
        for (int i = hq.size() - 1; i >= 0; --i) {
            scoreDocs[i] = (ScoreDoc)hq.pop();
        }
        return new TopFieldDocs(totalHits, scoreDocs, hq.getFields(), maxScore);
    }

    @Override
    public void search(Weight weight, Filter filter, final Collector collector) throws IOException {
        for (int i = 0; i < this.searchables.length; ++i) {
            final int start = this.starts[i];
            Collector hc = new Collector(){

                public void setScorer(Scorer scorer) throws IOException {
                    collector.setScorer(scorer);
                }

                public void collect(int doc) throws IOException {
                    collector.collect(doc);
                }

                public void setNextReader(IndexReader reader, int docBase) throws IOException {
                    collector.setNextReader(reader, start + docBase);
                }

                public boolean acceptsDocsOutOfOrder() {
                    return collector.acceptsDocsOutOfOrder();
                }
            };
            this.searchables[i].search(weight, filter, hc);
        }
    }

    @Override
    public void close() throws IOException {
        this.executor.shutdown();
        super.close();
    }

    HashMap<Term, Integer> createDocFrequencyMap(Set<Term> terms) throws IOException {
        Term[] allTermsArray = terms.toArray(new Term[terms.size()]);
        int[] aggregatedDocFreqs = new int[terms.size()];
        ExecutionHelper<int[]> runner = new ExecutionHelper<int[]>(this.executor);
        for (Searchable searchable : this.searchables) {
            runner.submit(new DocumentFrequencyCallable(searchable, allTermsArray));
        }
        int docFreqLen = aggregatedDocFreqs.length;
        for (int[] docFreqs : runner) {
            for (int i = 0; i < docFreqLen; ++i) {
                int n = i;
                aggregatedDocFreqs[n] = aggregatedDocFreqs[n] + docFreqs[i];
            }
        }
        HashMap<Term, Integer> dfMap = new HashMap<Term, Integer>();
        for (int i = 0; i < allTermsArray.length; ++i) {
            dfMap.put(allTermsArray[i], aggregatedDocFreqs[i]);
        }
        return dfMap;
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
    private static final class DocumentFrequencyCallable
    implements Callable<int[]> {
        private final Searchable searchable;
        private final Term[] terms;

        public DocumentFrequencyCallable(Searchable searchable, Term[] terms) {
            this.searchable = searchable;
            this.terms = terms;
        }

        @Override
        public int[] call() throws Exception {
            return this.searchable.docFreqs(this.terms);
        }
    }
}

