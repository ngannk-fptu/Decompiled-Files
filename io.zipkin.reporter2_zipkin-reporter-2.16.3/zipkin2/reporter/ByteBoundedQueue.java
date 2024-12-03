/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.reporter;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import zipkin2.reporter.SpanWithSizeConsumer;

final class ByteBoundedQueue<S>
implements SpanWithSizeConsumer<S> {
    final ReentrantLock lock = new ReentrantLock(false);
    final Condition available = this.lock.newCondition();
    final int maxSize;
    final int maxBytes;
    final S[] elements;
    final int[] sizesInBytes;
    int count;
    int sizeInBytes;
    int writePos;
    int readPos;

    ByteBoundedQueue(int maxSize, int maxBytes) {
        this.elements = new Object[maxSize];
        this.sizesInBytes = new int[maxSize];
        this.maxSize = maxSize;
        this.maxBytes = maxBytes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean offer(S next, int nextSizeInBytes) {
        this.lock.lock();
        try {
            if (this.count == this.maxSize) {
                boolean bl = false;
                return bl;
            }
            if (this.sizeInBytes + nextSizeInBytes > this.maxBytes) {
                boolean bl = false;
                return bl;
            }
            this.elements[this.writePos] = next;
            this.sizesInBytes[this.writePos++] = nextSizeInBytes;
            if (this.writePos == this.maxSize) {
                this.writePos = 0;
            }
            ++this.count;
            this.sizeInBytes += nextSizeInBytes;
            this.available.signal();
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    int drainTo(SpanWithSizeConsumer<S> consumer, long nanosTimeout) {
        try {
            this.lock.lockInterruptibly();
            try {
                long nanosLeft = nanosTimeout;
                while (this.count == 0) {
                    if (nanosLeft <= 0L) {
                        int n = 0;
                        return n;
                    }
                    nanosLeft = this.available.awaitNanos(nanosLeft);
                }
                int n = this.doDrain(consumer);
                return n;
            }
            finally {
                this.lock.unlock();
            }
        }
        catch (InterruptedException e) {
            return 0;
        }
    }

    int clear() {
        this.lock.lock();
        try {
            int result = this.count;
            this.writePos = 0;
            this.readPos = 0;
            this.sizeInBytes = 0;
            this.count = 0;
            Arrays.fill(this.elements, null);
            int n = result;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    int doDrain(SpanWithSizeConsumer<S> consumer) {
        int drainedCount = 0;
        int drainedSizeInBytes = 0;
        while (drainedCount < this.count) {
            S next = this.elements[this.readPos];
            int nextSizeInBytes = this.sizesInBytes[this.readPos];
            if (next == null || !consumer.offer(next, nextSizeInBytes)) break;
            ++drainedCount;
            drainedSizeInBytes += nextSizeInBytes;
            this.elements[this.readPos] = null;
            if (++this.readPos != this.elements.length) continue;
            this.readPos = 0;
        }
        this.count -= drainedCount;
        this.sizeInBytes -= drainedSizeInBytes;
        return drainedCount;
    }
}

