/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tag
 *  brave.Tags
 *  brave.handler.MutableSpan
 *  brave.handler.SpanHandler
 *  brave.handler.SpanHandler$Cause
 *  brave.propagation.TraceContext
 *  zipkin2.Span
 *  zipkin2.reporter.Reporter
 */
package zipkin2.reporter.brave;

import brave.Tag;
import brave.Tags;
import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import java.io.Closeable;
import zipkin2.Span;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.brave.ConvertingZipkinSpanHandler;

public class ZipkinSpanHandler
extends SpanHandler
implements Closeable {
    final Reporter<MutableSpan> spanReporter;
    final Tag<Throwable> errorTag;
    final boolean alwaysReportSpans;

    public static SpanHandler create(Reporter<Span> spanReporter) {
        return ZipkinSpanHandler.newBuilder(spanReporter).build();
    }

    public static Builder newBuilder(Reporter<Span> spanReporter) {
        if (spanReporter == null) {
            throw new NullPointerException("spanReporter == null");
        }
        return new ConvertingZipkinSpanHandler.Builder(spanReporter);
    }

    public Builder toBuilder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
    }

    ZipkinSpanHandler(Reporter<MutableSpan> spanReporter, Tag<Throwable> errorTag, boolean alwaysReportSpans) {
        this.spanReporter = spanReporter;
        this.errorTag = errorTag;
        this.alwaysReportSpans = alwaysReportSpans;
    }

    public boolean end(TraceContext context, MutableSpan span, SpanHandler.Cause cause) {
        if (!this.alwaysReportSpans && !Boolean.TRUE.equals(context.sampled())) {
            return true;
        }
        this.spanReporter.report((Object)span);
        return true;
    }

    public String toString() {
        return this.spanReporter.toString();
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ZipkinSpanHandler)) {
            return false;
        }
        return this.spanReporter.equals(((ZipkinSpanHandler)o).spanReporter);
    }

    public final int hashCode() {
        return this.spanReporter.hashCode();
    }

    public static abstract class Builder {
        Tag<Throwable> errorTag = Tags.ERROR;
        boolean alwaysReportSpans;

        Builder(ZipkinSpanHandler zipkinSpanHandler) {
            this.errorTag = zipkinSpanHandler.errorTag;
            this.alwaysReportSpans = zipkinSpanHandler.alwaysReportSpans;
        }

        Builder() {
        }

        public Builder errorTag(Tag<Throwable> errorTag) {
            if (errorTag == null) {
                throw new NullPointerException("errorTag == null");
            }
            this.errorTag = errorTag;
            return this;
        }

        public Builder alwaysReportSpans(boolean alwaysReportSpans) {
            this.alwaysReportSpans = alwaysReportSpans;
            return this;
        }

        public abstract SpanHandler build();
    }
}

