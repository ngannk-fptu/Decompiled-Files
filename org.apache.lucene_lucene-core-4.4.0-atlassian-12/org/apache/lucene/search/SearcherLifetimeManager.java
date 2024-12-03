/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.util.IOUtils;

public class SearcherLifetimeManager
implements Closeable {
    static final double NANOS_PER_SEC = 1.0E9;
    private volatile boolean closed;
    private final ConcurrentHashMap<Long, SearcherTracker> searchers = new ConcurrentHashMap();

    private void ensureOpen() {
        if (this.closed) {
            throw new AlreadyClosedException("this SearcherLifetimeManager instance is closed");
        }
    }

    public long record(IndexSearcher searcher) throws IOException {
        this.ensureOpen();
        long version = ((DirectoryReader)searcher.getIndexReader()).getVersion();
        SearcherTracker tracker = this.searchers.get(version);
        if (tracker == null) {
            tracker = new SearcherTracker(searcher);
            if (this.searchers.putIfAbsent(version, tracker) != null) {
                tracker.close();
            }
        } else if (tracker.searcher != searcher) {
            throw new IllegalArgumentException("the provided searcher has the same underlying reader version yet the searcher instance differs from before (new=" + searcher + " vs old=" + tracker.searcher);
        }
        return version;
    }

    public IndexSearcher acquire(long version) {
        this.ensureOpen();
        SearcherTracker tracker = this.searchers.get(version);
        if (tracker != null && tracker.searcher.getIndexReader().tryIncRef()) {
            return tracker.searcher;
        }
        return null;
    }

    public void release(IndexSearcher s) throws IOException {
        s.getIndexReader().decRef();
    }

    public synchronized void prune(Pruner pruner) throws IOException {
        ArrayList<SearcherTracker> trackers = new ArrayList<SearcherTracker>();
        for (SearcherTracker tracker : this.searchers.values()) {
            trackers.add(tracker);
        }
        Collections.sort(trackers);
        double lastRecordTimeSec = 0.0;
        double now = (double)System.nanoTime() / 1.0E9;
        for (SearcherTracker tracker : trackers) {
            double ageSec = lastRecordTimeSec == 0.0 ? 0.0 : now - lastRecordTimeSec;
            if (pruner.doPrune(ageSec, tracker.searcher)) {
                this.searchers.remove(tracker.version);
                tracker.close();
            }
            lastRecordTimeSec = tracker.recordTimeSec;
        }
    }

    @Override
    public synchronized void close() throws IOException {
        this.closed = true;
        ArrayList<SearcherTracker> toClose = new ArrayList<SearcherTracker>(this.searchers.values());
        for (SearcherTracker tracker : toClose) {
            this.searchers.remove(tracker.version);
        }
        IOUtils.close(toClose);
        if (this.searchers.size() != 0) {
            throw new IllegalStateException("another thread called record while this SearcherLifetimeManager instance was being closed; not all searchers were closed");
        }
    }

    public static final class PruneByAge
    implements Pruner {
        private final double maxAgeSec;

        public PruneByAge(double maxAgeSec) {
            if (maxAgeSec < 0.0) {
                throw new IllegalArgumentException("maxAgeSec must be > 0 (got " + maxAgeSec + ")");
            }
            this.maxAgeSec = maxAgeSec;
        }

        @Override
        public boolean doPrune(double ageSec, IndexSearcher searcher) {
            return ageSec > this.maxAgeSec;
        }
    }

    public static interface Pruner {
        public boolean doPrune(double var1, IndexSearcher var3);
    }

    private static class SearcherTracker
    implements Comparable<SearcherTracker>,
    Closeable {
        public final IndexSearcher searcher;
        public final double recordTimeSec;
        public final long version;

        public SearcherTracker(IndexSearcher searcher) {
            this.searcher = searcher;
            this.version = ((DirectoryReader)searcher.getIndexReader()).getVersion();
            searcher.getIndexReader().incRef();
            this.recordTimeSec = (double)System.nanoTime() / 1.0E9;
        }

        @Override
        public int compareTo(SearcherTracker other) {
            return Double.compare(other.recordTimeSec, this.recordTimeSec);
        }

        @Override
        public synchronized void close() throws IOException {
            this.searcher.getIndexReader().decRef();
        }
    }
}

