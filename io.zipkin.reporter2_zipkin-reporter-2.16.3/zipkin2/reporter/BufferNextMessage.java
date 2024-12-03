/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zipkin2.codec.Encoding
 */
package zipkin2.reporter;

import java.util.ArrayList;
import java.util.Iterator;
import zipkin2.codec.Encoding;
import zipkin2.reporter.SpanWithSizeConsumer;

abstract class BufferNextMessage<S>
implements SpanWithSizeConsumer<S> {
    final int maxBytes;
    final long timeoutNanos;
    final ArrayList<S> spans = new ArrayList();
    final ArrayList<Integer> sizes = new ArrayList();
    long deadlineNanoTime;
    int messageSizeInBytes;
    boolean bufferFull;

    static <S> BufferNextMessage<S> create(Encoding encoding, int maxBytes, long timeoutNanos) {
        switch (encoding) {
            case JSON: {
                return new BufferNextJsonMessage(maxBytes, timeoutNanos);
            }
            case THRIFT: {
                return new BufferNextThriftMessage(maxBytes, timeoutNanos);
            }
            case PROTO3: {
                return new BufferNextProto3Message(maxBytes, timeoutNanos);
            }
        }
        throw new UnsupportedOperationException("encoding: " + encoding);
    }

    BufferNextMessage(int maxBytes, long timeoutNanos) {
        this.maxBytes = maxBytes;
        this.timeoutNanos = timeoutNanos;
    }

    abstract int messageSizeInBytes(int var1);

    abstract void resetMessageSizeInBytes();

    @Override
    public boolean offer(S next, int nextSizeInBytes) {
        int includingNextVsMaxBytes;
        int y;
        int x = this.messageSizeInBytes(nextSizeInBytes);
        int n = x < (y = this.maxBytes) ? -1 : (includingNextVsMaxBytes = x == y ? 0 : 1);
        if (includingNextVsMaxBytes > 0) {
            this.bufferFull = true;
            return false;
        }
        this.addSpanToBuffer(next, nextSizeInBytes);
        this.messageSizeInBytes = x;
        if (includingNextVsMaxBytes == 0) {
            this.bufferFull = true;
        }
        return true;
    }

    void addSpanToBuffer(S next, int nextSizeInBytes) {
        this.spans.add(next);
        this.sizes.add(nextSizeInBytes);
    }

    long remainingNanos() {
        if (this.spans.isEmpty()) {
            this.deadlineNanoTime = System.nanoTime() + this.timeoutNanos;
        }
        return Math.max(this.deadlineNanoTime - System.nanoTime(), 0L);
    }

    boolean isReady() {
        return this.bufferFull || this.remainingNanos() <= 0L;
    }

    void drain(SpanWithSizeConsumer<S> consumer) {
        Iterator<S> spanIterator = this.spans.iterator();
        Iterator<Integer> sizeIterator = this.sizes.iterator();
        while (spanIterator.hasNext()) {
            if (!consumer.offer(spanIterator.next(), sizeIterator.next())) continue;
            this.bufferFull = false;
            spanIterator.remove();
            sizeIterator.remove();
        }
        this.resetMessageSizeInBytes();
        this.deadlineNanoTime = 0L;
    }

    int count() {
        return this.spans.size();
    }

    int sizeInBytes() {
        return this.messageSizeInBytes;
    }

    static final class BufferNextProto3Message<S>
    extends BufferNextMessage<S> {
        BufferNextProto3Message(int maxBytes, long timeoutNanos) {
            super(maxBytes, timeoutNanos);
        }

        @Override
        int messageSizeInBytes(int nextSizeInBytes) {
            return this.messageSizeInBytes += nextSizeInBytes;
        }

        @Override
        void resetMessageSizeInBytes() {
            this.messageSizeInBytes = 0;
            int length = this.sizes.size();
            for (int i = 0; i < length; ++i) {
                this.messageSizeInBytes += ((Integer)this.sizes.get(i)).intValue();
            }
        }
    }

    static final class BufferNextThriftMessage<S>
    extends BufferNextMessage<S> {
        BufferNextThriftMessage(int maxBytes, long timeoutNanos) {
            super(maxBytes, timeoutNanos);
            this.messageSizeInBytes = 5;
        }

        @Override
        int messageSizeInBytes(int nextSizeInBytes) {
            return this.messageSizeInBytes + nextSizeInBytes;
        }

        @Override
        void resetMessageSizeInBytes() {
            this.messageSizeInBytes = 5;
            int length = this.sizes.size();
            for (int i = 0; i < length; ++i) {
                this.messageSizeInBytes += ((Integer)this.sizes.get(i)).intValue();
            }
        }
    }

    static final class BufferNextJsonMessage<S>
    extends BufferNextMessage<S> {
        boolean hasAtLeastOneSpan;

        BufferNextJsonMessage(int maxBytes, long timeoutNanos) {
            super(maxBytes, timeoutNanos);
            this.messageSizeInBytes = 2;
            this.hasAtLeastOneSpan = false;
        }

        @Override
        int messageSizeInBytes(int nextSizeInBytes) {
            return this.messageSizeInBytes + nextSizeInBytes + (this.hasAtLeastOneSpan ? 1 : 0);
        }

        @Override
        void resetMessageSizeInBytes() {
            int length = this.sizes.size();
            boolean bl = this.hasAtLeastOneSpan = length > 0;
            if (length < 2) {
                this.messageSizeInBytes = 2;
                if (this.hasAtLeastOneSpan) {
                    this.messageSizeInBytes += ((Integer)this.sizes.get(0)).intValue();
                }
            } else {
                this.messageSizeInBytes = 2 + length - 1;
                for (int i = 0; i < length; ++i) {
                    this.messageSizeInBytes += ((Integer)this.sizes.get(i)).intValue();
                }
            }
        }

        @Override
        void addSpanToBuffer(S next, int nextSizeInBytes) {
            super.addSpanToBuffer(next, nextSizeInBytes);
            this.hasAtLeastOneSpan = true;
        }
    }
}

