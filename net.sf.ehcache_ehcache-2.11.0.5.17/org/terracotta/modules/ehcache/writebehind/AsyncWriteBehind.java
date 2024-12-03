/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.writebehind;

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.OperationsFilter;
import net.sf.ehcache.writer.writebehind.WriteBehind;
import org.terracotta.modules.ehcache.async.AsyncCoordinator;
import org.terracotta.modules.ehcache.async.scatterpolicies.ItemScatterPolicy;
import org.terracotta.modules.ehcache.writebehind.CacheWriterProcessor;
import org.terracotta.modules.ehcache.writebehind.OperationsFilterWrapper;
import org.terracotta.modules.ehcache.writebehind.operations.DeleteAsyncOperation;
import org.terracotta.modules.ehcache.writebehind.operations.SingleAsyncOperation;
import org.terracotta.modules.ehcache.writebehind.operations.WriteAsyncOperation;

public class AsyncWriteBehind
implements WriteBehind {
    public static final ItemScatterPolicy<SingleAsyncOperation> POLICY = new SingleAsyncOperationItemScatterPolicy();
    private final AsyncCoordinator<SingleAsyncOperation> async;
    private final int concurrency;

    public AsyncWriteBehind(AsyncCoordinator async, int writeBehindConcurrency) {
        if (async == null) {
            throw new IllegalArgumentException("AsyncCoordinator can't be null");
        }
        if (writeBehindConcurrency < 1) {
            throw new IllegalArgumentException("writeBehindConcurrency has to be at least one");
        }
        this.async = async;
        this.concurrency = writeBehindConcurrency;
    }

    @Override
    public void start(CacheWriter writer) throws CacheException {
        this.async.start(new CacheWriterProcessor(writer), this.concurrency, POLICY);
    }

    @Override
    public void write(Element element) {
        this.async.add(new WriteAsyncOperation(element));
    }

    @Override
    public void delete(CacheEntry entry) {
        this.async.add(new DeleteAsyncOperation(entry.getKey(), entry.getElement()));
    }

    @Override
    public void setOperationsFilter(OperationsFilter filter) {
        OperationsFilterWrapper filterWrapper = new OperationsFilterWrapper(filter);
        this.async.setOperationsFilter(filterWrapper);
    }

    @Override
    public void stop() throws CacheException {
        this.async.stop();
    }

    @Override
    public long getQueueSize() {
        return this.async.getQueueSize();
    }

    private static class SingleAsyncOperationItemScatterPolicy
    implements ItemScatterPolicy<SingleAsyncOperation> {
        private SingleAsyncOperationItemScatterPolicy() {
        }

        @Override
        public int selectBucket(int count, SingleAsyncOperation item) {
            Object key;
            try {
                key = item.getKey();
            }
            catch (Exception e) {
                throw new CacheException(e);
            }
            return Math.abs(key.hashCode() % count);
        }
    }
}

