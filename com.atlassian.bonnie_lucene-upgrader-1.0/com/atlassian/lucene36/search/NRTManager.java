/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.IndexSearcher;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.ReferenceManager;
import com.atlassian.lucene36.search.SearcherFactory;
import com.atlassian.lucene36.search.SearcherManager;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class NRTManager
extends ReferenceManager<IndexSearcher> {
    private static final long MAX_SEARCHER_GEN = Long.MAX_VALUE;
    private final TrackingIndexWriter writer;
    private final List<WaitingListener> waitingListeners = new CopyOnWriteArrayList<WaitingListener>();
    private final ReentrantLock genLock = new ReentrantLock();
    private final Condition newGeneration = this.genLock.newCondition();
    private final SearcherFactory searcherFactory;
    private volatile long searchingGen;
    private long lastRefreshGen;

    public NRTManager(TrackingIndexWriter writer, SearcherFactory searcherFactory) throws IOException {
        this(writer, searcherFactory, true);
    }

    public NRTManager(TrackingIndexWriter writer, SearcherFactory searcherFactory, boolean applyAllDeletes) throws IOException {
        this.writer = writer;
        if (searcherFactory == null) {
            searcherFactory = new SearcherFactory();
        }
        this.searcherFactory = searcherFactory;
        this.current = SearcherManager.getSearcher(searcherFactory, IndexReader.open(writer.getIndexWriter(), applyAllDeletes));
    }

    @Override
    protected void decRef(IndexSearcher reference) throws IOException {
        reference.getIndexReader().decRef();
    }

    @Override
    protected boolean tryIncRef(IndexSearcher reference) {
        return reference.getIndexReader().tryIncRef();
    }

    public void addWaitingListener(WaitingListener l) {
        this.waitingListeners.add(l);
    }

    public void removeWaitingListener(WaitingListener l) {
        this.waitingListeners.remove(l);
    }

    public void waitForGeneration(long targetGen) {
        this.waitForGeneration(targetGen, -1L, TimeUnit.NANOSECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void waitForGeneration(long targetGen, long time, TimeUnit unit) {
        try {
            long curGen = this.writer.getGeneration();
            if (targetGen > curGen) {
                throw new IllegalArgumentException("targetGen=" + targetGen + " was never returned by this NRTManager instance (current gen=" + curGen + ")");
            }
            this.genLock.lockInterruptibly();
            try {
                if (targetGen > this.searchingGen) {
                    for (WaitingListener listener : this.waitingListeners) {
                        listener.waiting(targetGen);
                    }
                    while (targetGen > this.searchingGen) {
                        if (this.waitOnGenCondition(time, unit)) continue;
                        Object var11_7 = null;
                        this.genLock.unlock();
                        return;
                    }
                }
                Object var11_8 = null;
                this.genLock.unlock();
            }
            catch (Throwable throwable) {
                Object var11_9 = null;
                this.genLock.unlock();
                throw throwable;
            }
        }
        catch (InterruptedException ie) {
            throw new ThreadInterruptedException(ie);
        }
    }

    private boolean waitOnGenCondition(long time, TimeUnit unit) throws InterruptedException {
        assert (this.genLock.isHeldByCurrentThread());
        if (time < 0L) {
            this.newGeneration.await();
            return true;
        }
        return this.newGeneration.await(time, unit);
    }

    public long getCurrentSearchingGen() {
        return this.searchingGen;
    }

    @Override
    protected IndexSearcher refreshIfNeeded(IndexSearcher referenceToRefresh) throws IOException {
        IndexReader newReader;
        this.lastRefreshGen = this.writer.getAndIncrementGeneration();
        IndexReader r = referenceToRefresh.getIndexReader();
        IndexSearcher newSearcher = null;
        if (!r.isCurrent() && (newReader = IndexReader.openIfChanged(r)) != null) {
            newSearcher = SearcherManager.getSearcher(this.searcherFactory, newReader);
        }
        return newSearcher;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void afterRefresh() {
        this.genLock.lock();
        try {
            if (this.searchingGen != Long.MAX_VALUE) {
                assert (this.lastRefreshGen >= this.searchingGen);
                this.searchingGen = this.lastRefreshGen;
            }
            this.newGeneration.signalAll();
            Object var2_1 = null;
            this.genLock.unlock();
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            this.genLock.unlock();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected synchronized void afterClose() throws IOException {
        this.genLock.lock();
        try {
            this.searchingGen = Long.MAX_VALUE;
            this.newGeneration.signalAll();
            Object var2_1 = null;
            this.genLock.unlock();
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            this.genLock.unlock();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isSearcherCurrent() throws IOException {
        boolean bl;
        IndexSearcher searcher = (IndexSearcher)this.acquire();
        try {
            bl = searcher.getIndexReader().isCurrent();
            Object var4_3 = null;
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            this.release(searcher);
            throw throwable;
        }
        this.release(searcher);
        return bl;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TrackingIndexWriter {
        private final IndexWriter writer;
        private final AtomicLong indexingGen = new AtomicLong(1L);

        public TrackingIndexWriter(IndexWriter writer) {
            this.writer = writer;
        }

        public long updateDocument(Term t, Document d, Analyzer a) throws IOException {
            this.writer.updateDocument(t, d, a);
            return this.indexingGen.get();
        }

        public long updateDocument(Term t, Document d) throws IOException {
            this.writer.updateDocument(t, d);
            return this.indexingGen.get();
        }

        public long updateDocuments(Term t, Collection<Document> docs, Analyzer a) throws IOException {
            this.writer.updateDocuments(t, docs, a);
            return this.indexingGen.get();
        }

        public long updateDocuments(Term t, Collection<Document> docs) throws IOException {
            this.writer.updateDocuments(t, docs);
            return this.indexingGen.get();
        }

        public long deleteDocuments(Term t) throws IOException {
            this.writer.deleteDocuments(t);
            return this.indexingGen.get();
        }

        public long deleteDocuments(Term ... terms) throws IOException {
            this.writer.deleteDocuments(terms);
            return this.indexingGen.get();
        }

        public long deleteDocuments(Query q) throws IOException {
            this.writer.deleteDocuments(q);
            return this.indexingGen.get();
        }

        public long deleteDocuments(Query ... queries) throws IOException {
            this.writer.deleteDocuments(queries);
            return this.indexingGen.get();
        }

        public long deleteAll() throws IOException {
            this.writer.deleteAll();
            return this.indexingGen.get();
        }

        public long addDocument(Document d, Analyzer a) throws IOException {
            this.writer.addDocument(d, a);
            return this.indexingGen.get();
        }

        public long addDocuments(Collection<Document> docs, Analyzer a) throws IOException {
            this.writer.addDocuments(docs, a);
            return this.indexingGen.get();
        }

        public long addDocument(Document d) throws IOException {
            this.writer.addDocument(d);
            return this.indexingGen.get();
        }

        public long addDocuments(Collection<Document> docs) throws IOException {
            this.writer.addDocuments(docs);
            return this.indexingGen.get();
        }

        public long addIndexes(Directory ... dirs) throws CorruptIndexException, IOException {
            this.writer.addIndexes(dirs);
            return this.indexingGen.get();
        }

        public long addIndexes(IndexReader ... readers) throws CorruptIndexException, IOException {
            this.writer.addIndexes(readers);
            return this.indexingGen.get();
        }

        public long getGeneration() {
            return this.indexingGen.get();
        }

        public IndexWriter getIndexWriter() {
            return this.writer;
        }

        long getAndIncrementGeneration() {
            return this.indexingGen.getAndIncrement();
        }
    }

    public static interface WaitingListener {
        public void waiting(long var1);
    }
}

