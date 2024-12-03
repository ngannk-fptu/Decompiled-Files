/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tag
 *  brave.handler.SpanHandler
 *  zipkin2.Span
 *  zipkin2.reporter.Reporter
 */
package zipkin2.reporter.brave;

import brave.Tag;
import brave.handler.SpanHandler;
import zipkin2.Span;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.brave.ConvertingSpanReporter;
import zipkin2.reporter.brave.ZipkinSpanHandler;

final class ConvertingZipkinSpanHandler
extends ZipkinSpanHandler {
    @Override
    public ZipkinSpanHandler.Builder toBuilder() {
        return new Builder(((ConvertingSpanReporter)this.spanReporter).delegate);
    }

    ConvertingZipkinSpanHandler(Builder builder) {
        super(new ConvertingSpanReporter(builder.spanReporter, (Tag<Throwable>)builder.errorTag), (Tag<Throwable>)builder.errorTag, builder.alwaysReportSpans);
    }

    static final class Builder
    extends ZipkinSpanHandler.Builder {
        final Reporter<Span> spanReporter;

        Builder(Reporter<Span> spanReporter) {
            this.spanReporter = spanReporter;
        }

        @Override
        public SpanHandler build() {
            if (this.spanReporter == Reporter.NOOP) {
                return SpanHandler.NOOP;
            }
            return new ConvertingZipkinSpanHandler(this);
        }
    }
}

