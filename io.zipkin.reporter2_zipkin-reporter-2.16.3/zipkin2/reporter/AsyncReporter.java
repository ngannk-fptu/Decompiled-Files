/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zipkin2.Call
 *  zipkin2.CheckResult
 *  zipkin2.Component
 *  zipkin2.Span
 *  zipkin2.codec.BytesEncoder
 *  zipkin2.codec.SpanBytesEncoder
 */
package zipkin2.reporter;

import java.io.Flushable;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import zipkin2.Call;
import zipkin2.CheckResult;
import zipkin2.Component;
import zipkin2.Span;
import zipkin2.codec.BytesEncoder;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.BufferNextMessage;
import zipkin2.reporter.ByteBoundedQueue;
import zipkin2.reporter.ClosedSenderException;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.ReporterMetrics;
import zipkin2.reporter.Sender;
import zipkin2.reporter.SpanWithSizeConsumer;

public abstract class AsyncReporter<S>
extends Component
implements Reporter<S>,
Flushable {
    public static AsyncReporter<Span> create(Sender sender) {
        return new Builder(sender).build();
    }

    public static Builder builder(Sender sender) {
        return new Builder(sender);
    }

    @Override
    public abstract void flush();

    public abstract void close();

    static final class Flusher<S>
    implements Runnable {
        static final Logger logger = Logger.getLogger(Flusher.class.getName());
        final BoundedAsyncReporter<S> result;
        final BufferNextMessage<S> consumer;

        Flusher(BoundedAsyncReporter<S> result, BufferNextMessage<S> consumer) {
            this.result = result;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try {
                while (!this.result.closed.get()) {
                    this.result.flush(this.consumer);
                }
            }
            catch (Error | RuntimeException e) {
                logger.log(Level.WARNING, "Unexpected error flushing spans", e);
                throw e;
            }
            finally {
                int count = this.consumer.count();
                if (count > 0) {
                    this.result.metrics.incrementSpansDropped(count);
                    logger.warning("Dropped " + count + " spans due to AsyncReporter.close()");
                }
                this.result.close.countDown();
            }
        }

        public String toString() {
            return "AsyncReporter{" + (Object)((Object)this.result.sender) + "}";
        }
    }

    static final class BoundedAsyncReporter<S>
    extends AsyncReporter<S> {
        static final Logger logger = Logger.getLogger(BoundedAsyncReporter.class.getName());
        final AtomicBoolean started;
        final AtomicBoolean closed;
        final BytesEncoder<S> encoder;
        final ByteBoundedQueue<S> pending;
        final Sender sender;
        final int messageMaxBytes;
        final long messageTimeoutNanos;
        final long closeTimeoutNanos;
        final CountDownLatch close;
        final ReporterMetrics metrics;
        final ThreadFactory threadFactory;
        private boolean shouldWarnException = true;

        BoundedAsyncReporter(Builder builder, BytesEncoder<S> encoder) {
            this.pending = new ByteBoundedQueue(builder.queuedMaxSpans, builder.queuedMaxBytes);
            this.sender = builder.sender;
            this.messageMaxBytes = builder.messageMaxBytes;
            this.messageTimeoutNanos = builder.messageTimeoutNanos;
            this.closeTimeoutNanos = builder.closeTimeoutNanos;
            this.closed = new AtomicBoolean(false);
            this.started = new AtomicBoolean(builder.messageTimeoutNanos == 0L);
            this.close = new CountDownLatch(builder.messageTimeoutNanos > 0L ? 1 : 0);
            this.metrics = builder.metrics;
            this.threadFactory = builder.threadFactory;
            this.encoder = encoder;
        }

        void startFlusherThread() {
            BufferNextMessage consumer = BufferNextMessage.create(this.encoder.encoding(), this.messageMaxBytes, this.messageTimeoutNanos);
            Thread flushThread = this.threadFactory.newThread(new Flusher(this, consumer));
            flushThread.setName("AsyncReporter{" + (Object)((Object)this.sender) + "}");
            flushThread.setDaemon(true);
            flushThread.start();
        }

        @Override
        public void report(S next) {
            if (next == null) {
                throw new NullPointerException("span == null");
            }
            if (this.started.compareAndSet(false, true)) {
                this.startFlusherThread();
            }
            this.metrics.incrementSpans(1);
            int nextSizeInBytes = this.encoder.sizeInBytes(next);
            int messageSizeOfNextSpan = this.sender.messageSizeInBytes(nextSizeInBytes);
            this.metrics.incrementSpanBytes(nextSizeInBytes);
            if (this.closed.get() || messageSizeOfNextSpan > this.messageMaxBytes || !this.pending.offer(next, nextSizeInBytes)) {
                this.metrics.incrementSpansDropped(1);
            }
        }

        @Override
        public final void flush() {
            if (this.closed.get()) {
                throw new ClosedSenderException();
            }
            this.flush(BufferNextMessage.create(this.encoder.encoding(), this.messageMaxBytes, 0L));
        }

        void flush(BufferNextMessage<S> bundler) {
            block6: {
                this.pending.drainTo(bundler, bundler.remainingNanos());
                this.metrics.updateQueuedSpans(this.pending.count);
                this.metrics.updateQueuedBytes(this.pending.sizeInBytes);
                if (!bundler.isReady() && !this.closed.get()) {
                    return;
                }
                this.metrics.incrementMessages();
                this.metrics.incrementMessageBytes(bundler.sizeInBytes());
                final ArrayList<byte[]> nextMessage = new ArrayList<byte[]>(bundler.count());
                bundler.drain(new SpanWithSizeConsumer<S>(){

                    @Override
                    public boolean offer(S next, int nextSizeInBytes) {
                        nextMessage.add(BoundedAsyncReporter.this.encoder.encode(next));
                        if (BoundedAsyncReporter.this.sender.messageSizeInBytes(nextMessage) > BoundedAsyncReporter.this.messageMaxBytes) {
                            nextMessage.remove(nextMessage.size() - 1);
                            return false;
                        }
                        return true;
                    }
                });
                try {
                    this.sender.sendSpans(nextMessage).execute();
                }
                catch (Throwable t) {
                    int count = nextMessage.size();
                    Call.propagateIfFatal((Throwable)t);
                    this.metrics.incrementMessagesDropped(t);
                    this.metrics.incrementSpansDropped(count);
                    Level logLevel = Level.FINE;
                    if (this.shouldWarnException) {
                        logger.log(Level.WARNING, "Spans were dropped due to exceptions. All subsequent errors will be logged at FINE level.");
                        logLevel = Level.WARNING;
                        this.shouldWarnException = false;
                    }
                    if (logger.isLoggable(logLevel)) {
                        logger.log(logLevel, String.format("Dropped %s spans due to %s(%s)", count, t.getClass().getSimpleName(), t.getMessage() == null ? "" : t.getMessage()), t);
                    }
                    if (t instanceof ClosedSenderException) {
                        throw (ClosedSenderException)t;
                    }
                    if (!(t instanceof IllegalStateException) || !t.getMessage().equals("closed")) break block6;
                    throw (IllegalStateException)t;
                }
            }
        }

        public CheckResult check() {
            return this.sender.check();
        }

        @Override
        public void close() {
            if (!this.closed.compareAndSet(false, true)) {
                return;
            }
            this.started.set(true);
            try {
                if (!this.close.await(this.closeTimeoutNanos, TimeUnit.NANOSECONDS)) {
                    logger.warning("Timed out waiting for in-flight spans to send");
                }
            }
            catch (InterruptedException e) {
                logger.warning("Interrupted waiting for in-flight spans to send");
                Thread.currentThread().interrupt();
            }
            int count = this.pending.clear();
            if (count > 0) {
                this.metrics.incrementSpansDropped(count);
                logger.warning("Dropped " + count + " spans due to AsyncReporter.close()");
            }
        }

        Builder toBuilder() {
            return new Builder(this);
        }

        public String toString() {
            return "AsyncReporter{" + (Object)((Object)this.sender) + "}";
        }
    }

    public static final class Builder {
        final Sender sender;
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ReporterMetrics metrics = ReporterMetrics.NOOP_METRICS;
        int messageMaxBytes;
        long messageTimeoutNanos = TimeUnit.SECONDS.toNanos(1L);
        long closeTimeoutNanos = TimeUnit.SECONDS.toNanos(1L);
        int queuedMaxSpans = 10000;
        int queuedMaxBytes = Builder.onePercentOfMemory();

        Builder(BoundedAsyncReporter<?> asyncReporter) {
            this.sender = asyncReporter.sender;
            this.threadFactory = asyncReporter.threadFactory;
            this.metrics = asyncReporter.metrics;
            this.messageMaxBytes = asyncReporter.messageMaxBytes;
            this.messageTimeoutNanos = asyncReporter.messageTimeoutNanos;
            this.closeTimeoutNanos = asyncReporter.closeTimeoutNanos;
            this.queuedMaxSpans = asyncReporter.pending.maxSize;
            this.queuedMaxBytes = asyncReporter.pending.maxBytes;
        }

        static int onePercentOfMemory() {
            long result = (long)((double)Runtime.getRuntime().totalMemory() * 0.01);
            return (int)Math.max(Math.min(Integer.MAX_VALUE, result), Integer.MIN_VALUE);
        }

        Builder(Sender sender) {
            if (sender == null) {
                throw new NullPointerException("sender == null");
            }
            this.sender = sender;
            this.messageMaxBytes = sender.messageMaxBytes();
        }

        public Builder threadFactory(ThreadFactory threadFactory) {
            if (threadFactory == null) {
                throw new NullPointerException("threadFactory == null");
            }
            this.threadFactory = threadFactory;
            return this;
        }

        public Builder metrics(ReporterMetrics metrics) {
            if (metrics == null) {
                throw new NullPointerException("metrics == null");
            }
            this.metrics = metrics;
            return this;
        }

        public Builder messageMaxBytes(int messageMaxBytes) {
            if (messageMaxBytes < 0) {
                throw new IllegalArgumentException("messageMaxBytes < 0: " + messageMaxBytes);
            }
            this.messageMaxBytes = Math.min(messageMaxBytes, this.sender.messageMaxBytes());
            return this;
        }

        public Builder messageTimeout(long timeout, TimeUnit unit) {
            if (timeout < 0L) {
                throw new IllegalArgumentException("messageTimeout < 0: " + timeout);
            }
            if (unit == null) {
                throw new NullPointerException("unit == null");
            }
            this.messageTimeoutNanos = unit.toNanos(timeout);
            return this;
        }

        public Builder closeTimeout(long timeout, TimeUnit unit) {
            if (timeout < 0L) {
                throw new IllegalArgumentException("closeTimeout < 0: " + timeout);
            }
            if (unit == null) {
                throw new NullPointerException("unit == null");
            }
            this.closeTimeoutNanos = unit.toNanos(timeout);
            return this;
        }

        public Builder queuedMaxSpans(int queuedMaxSpans) {
            this.queuedMaxSpans = queuedMaxSpans;
            return this;
        }

        public Builder queuedMaxBytes(int queuedMaxBytes) {
            this.queuedMaxBytes = queuedMaxBytes;
            return this;
        }

        public AsyncReporter<Span> build() {
            switch (this.sender.encoding()) {
                case JSON: {
                    return this.build((BytesEncoder)SpanBytesEncoder.JSON_V2);
                }
                case PROTO3: {
                    return this.build((BytesEncoder)SpanBytesEncoder.PROTO3);
                }
                case THRIFT: {
                    return this.build((BytesEncoder)SpanBytesEncoder.THRIFT);
                }
            }
            throw new UnsupportedOperationException(this.sender.encoding().name());
        }

        public <S> AsyncReporter<S> build(BytesEncoder<S> encoder) {
            if (encoder == null) {
                throw new NullPointerException("encoder == null");
            }
            if (encoder.encoding() != this.sender.encoding()) {
                throw new IllegalArgumentException(String.format("Encoder doesn't match Sender: %s %s", encoder.encoding(), this.sender.encoding()));
            }
            return new BoundedAsyncReporter<S>(this, encoder);
        }
    }
}

