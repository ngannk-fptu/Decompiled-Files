/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;
import org.apache.jackrabbit.api.stats.QueryStatDto;
import org.apache.jackrabbit.stats.QueryStatCore;
import org.apache.jackrabbit.stats.QueryStatDtoComparator;
import org.apache.jackrabbit.stats.QueryStatDtoImpl;
import org.apache.jackrabbit.stats.QueryStatDtoOccurrenceComparator;

public class QueryStatImpl
implements QueryStatCore {
    private static final Comparator<QueryStatDto> comparator = new QueryStatDtoComparator();
    private final BoundedPriorityBlockingQueue<QueryStatDto> slowQueries = new BoundedPriorityBlockingQueue<QueryStatDto>(15, comparator);
    private static final Comparator<QueryStatDtoImpl> comparatorOccurrence = new QueryStatDtoOccurrenceComparator();
    private static final int POPULAR_QUEUE_MULTIPLIER = 5;
    private final BoundedPriorityBlockingQueue<QueryStatDtoImpl> popularQueries = new BoundedPriorityBlockingQueue<QueryStatDtoImpl>(75, comparatorOccurrence);
    private boolean enabled = false;

    @Override
    public int getSlowQueriesQueueSize() {
        return this.slowQueries.getMaxSize();
    }

    @Override
    public void setSlowQueriesQueueSize(int size) {
        this.slowQueries.setMaxSize(size);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public synchronized void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void logQuery(String language, String statement, long durationMs) {
        if (!this.enabled) {
            return;
        }
        QueryStatDtoImpl qs = new QueryStatDtoImpl(language, statement, durationMs);
        this.slowQueries.offer(qs);
        BoundedPriorityBlockingQueue<QueryStatDtoImpl> boundedPriorityBlockingQueue = this.popularQueries;
        synchronized (boundedPriorityBlockingQueue) {
            Iterator iterator = this.popularQueries.iterator();
            while (iterator.hasNext()) {
                QueryStatDtoImpl qsdi = (QueryStatDtoImpl)iterator.next();
                if (!qsdi.equals(qs)) continue;
                qs.setOccurrenceCount(qsdi.getOccurrenceCount() + 1);
                iterator.remove();
                break;
            }
            this.popularQueries.offer(qs);
        }
    }

    @Override
    public void clearSlowQueriesQueue() {
        this.slowQueries.clear();
    }

    @Override
    public QueryStatDto[] getSlowQueries() {
        QueryStatDto[] top = this.slowQueries.toArray(new QueryStatDto[this.slowQueries.size()]);
        Arrays.sort(top, Collections.reverseOrder(comparator));
        for (int i = 0; i < top.length; ++i) {
            top[i].setPosition(i + 1);
        }
        return top;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public QueryStatDto[] getPopularQueries() {
        QueryStatDtoImpl[] top;
        int size = 0;
        int maxSize = 0;
        BoundedPriorityBlockingQueue<QueryStatDtoImpl> boundedPriorityBlockingQueue = this.popularQueries;
        synchronized (boundedPriorityBlockingQueue) {
            top = this.popularQueries.toArray(new QueryStatDtoImpl[this.popularQueries.size()]);
            size = this.popularQueries.size();
            maxSize = this.popularQueries.getMaxSize();
        }
        Arrays.sort(top, Collections.reverseOrder(comparatorOccurrence));
        int retSize = Math.min(size, maxSize / 5);
        QueryStatDto[] retval = new QueryStatDto[retSize];
        for (int i = 0; i < retSize; ++i) {
            retval[i] = top[i];
            retval[i].setPosition(i + 1);
        }
        return retval;
    }

    @Override
    public int getPopularQueriesQueueSize() {
        return this.popularQueries.getMaxSize() / 5;
    }

    @Override
    public void setPopularQueriesQueueSize(int size) {
        this.popularQueries.setMaxSize(size * 5);
    }

    @Override
    public void clearPopularQueriesQueue() {
        this.popularQueries.clear();
    }

    @Override
    public void reset() {
        this.clearSlowQueriesQueue();
        this.clearPopularQueriesQueue();
    }

    private static final class BoundedPriorityBlockingQueue<E>
    extends PriorityBlockingQueue<E> {
        private static final long serialVersionUID = 1L;
        private int maxSize;

        public BoundedPriorityBlockingQueue(int maxSize, Comparator<? super E> comparator) {
            super(maxSize + 1, comparator);
            this.maxSize = maxSize;
        }

        @Override
        public boolean offer(E e) {
            boolean s = super.offer(e);
            if (!s) {
                return false;
            }
            if (this.size() > this.maxSize) {
                this.poll();
            }
            return true;
        }

        public synchronized void setMaxSize(int maxSize) {
            if (maxSize < this.maxSize) {
                Object t;
                int delta = super.size() - maxSize;
                for (int i = 0; i < delta && (t = this.poll()) != null; ++i) {
                }
            }
            this.maxSize = maxSize;
        }

        public int getMaxSize() {
            return this.maxSize;
        }
    }
}

