/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tag
 *  brave.handler.MutableSpan
 *  zipkin2.codec.BytesEncoder
 *  zipkin2.reporter.AsyncReporter
 *  zipkin2.reporter.AsyncReporter$Builder
 *  zipkin2.reporter.Reporter
 *  zipkin2.reporter.ReporterMetrics
 *  zipkin2.reporter.Sender
 *  zipkin2.reporter.internal.InternalReporter
 */
package zipkin2.reporter.brave;

import brave.Tag;
import brave.handler.MutableSpan;
import java.io.Closeable;
import java.io.Flushable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import zipkin2.codec.BytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.ReporterMetrics;
import zipkin2.reporter.Sender;
import zipkin2.reporter.brave.JsonV2Encoder;
import zipkin2.reporter.brave.ZipkinSpanHandler;
import zipkin2.reporter.internal.InternalReporter;

public final class AsyncZipkinSpanHandler
extends ZipkinSpanHandler
implements Closeable,
Flushable {
    public static AsyncZipkinSpanHandler create(Sender sender) {
        return AsyncZipkinSpanHandler.newBuilder(sender).build();
    }

    public static Builder newBuilder(Sender sender) {
        if (sender == null) {
            throw new NullPointerException("sender == null");
        }
        return new Builder(sender);
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    AsyncZipkinSpanHandler(Builder builder) {
        super((Reporter<MutableSpan>)builder.delegate.build((BytesEncoder)new JsonV2Encoder((Tag<Throwable>)builder.errorTag)), (Tag<Throwable>)builder.errorTag, builder.alwaysReportSpans);
    }

    @Override
    public void flush() {
        ((AsyncReporter)this.spanReporter).flush();
    }

    @Override
    public void close() {
        ((AsyncReporter)this.spanReporter).close();
    }

    public static final class Builder
    extends ZipkinSpanHandler.Builder {
        final AsyncReporter.Builder delegate;

        Builder(AsyncZipkinSpanHandler zipkinSpanHandler) {
            super(zipkinSpanHandler);
            this.delegate = InternalReporter.instance.toBuilder((AsyncReporter)zipkinSpanHandler.spanReporter);
        }

        Builder(Sender sender) {
            this.delegate = AsyncReporter.builder((Sender)sender);
        }

        public Builder threadFactory(ThreadFactory threadFactory) {
            this.delegate.threadFactory(threadFactory);
            return this;
        }

        public Builder metrics(ReporterMetrics metrics) {
            this.delegate.metrics(metrics);
            return this;
        }

        public Builder messageMaxBytes(int messageMaxBytes) {
            this.delegate.messageMaxBytes(messageMaxBytes);
            return this;
        }

        public Builder messageTimeout(long timeout, TimeUnit unit) {
            this.delegate.messageTimeout(timeout, unit);
            return this;
        }

        public Builder closeTimeout(long timeout, TimeUnit unit) {
            this.delegate.closeTimeout(timeout, unit);
            return this;
        }

        public Builder queuedMaxSpans(int queuedMaxSpans) {
            this.delegate.queuedMaxSpans(queuedMaxSpans);
            return this;
        }

        public Builder queuedMaxBytes(int queuedMaxBytes) {
            this.delegate.queuedMaxBytes(queuedMaxBytes);
            return this;
        }

        @Override
        public Builder errorTag(Tag<Throwable> errorTag) {
            return (Builder)super.errorTag(errorTag);
        }

        @Override
        public Builder alwaysReportSpans(boolean alwaysReportSpans) {
            return (Builder)super.alwaysReportSpans(alwaysReportSpans);
        }

        @Override
        public AsyncZipkinSpanHandler build() {
            return new AsyncZipkinSpanHandler(this);
        }
    }
}

