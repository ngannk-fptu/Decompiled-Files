/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.pipesiterator;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.pipesiterator.PipesIterator;

public class CallablePipesIterator
implements Callable<Long> {
    private final PipesIterator pipesIterator;
    private final ArrayBlockingQueue<FetchEmitTuple> queue;
    private final long timeoutMillis;
    private final int numConsumers;

    public CallablePipesIterator(PipesIterator pipesIterator, ArrayBlockingQueue<FetchEmitTuple> queue) {
        this(pipesIterator, queue, -1L);
    }

    public CallablePipesIterator(PipesIterator pipesIterator, ArrayBlockingQueue<FetchEmitTuple> queue, long timeoutMillis) {
        this(pipesIterator, queue, timeoutMillis, 1);
    }

    public CallablePipesIterator(PipesIterator pipesIterator, ArrayBlockingQueue<FetchEmitTuple> queue, long timeoutMillis, int numConsumers) {
        this.pipesIterator = pipesIterator;
        this.queue = queue;
        this.timeoutMillis = timeoutMillis;
        this.numConsumers = numConsumers;
    }

    @Override
    public Long call() throws Exception {
        long added = 0L;
        if (this.timeoutMillis > 0L) {
            for (FetchEmitTuple t : this.pipesIterator) {
                boolean offered = this.queue.offer(t, this.timeoutMillis, TimeUnit.MILLISECONDS);
                if (!offered) {
                    throw new TimeoutException("timed out trying to offer tuple");
                }
                ++added;
            }
            for (int i = 0; i < this.numConsumers; ++i) {
                boolean offered = this.queue.offer(PipesIterator.COMPLETED_SEMAPHORE);
                if (offered) continue;
                throw new TimeoutException("timed out trying to offer tuple");
            }
        } else {
            for (FetchEmitTuple t : this.pipesIterator) {
                this.queue.put(t);
                ++added;
            }
            for (int i = 0; i < this.numConsumers; ++i) {
                this.queue.put(PipesIterator.COMPLETED_SEMAPHORE);
            }
        }
        return added;
    }
}

