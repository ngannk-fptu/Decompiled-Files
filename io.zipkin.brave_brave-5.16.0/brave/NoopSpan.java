/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.LazySpan;
import brave.NoopSpanCustomizer;
import brave.Span;
import brave.SpanCustomizer;
import brave.propagation.TraceContext;

final class NoopSpan
extends Span {
    final TraceContext context;

    NoopSpan(TraceContext context) {
        this.context = context;
    }

    @Override
    public SpanCustomizer customizer() {
        return NoopSpanCustomizer.INSTANCE;
    }

    @Override
    public boolean isNoop() {
        return true;
    }

    @Override
    public TraceContext context() {
        return this.context;
    }

    @Override
    public Span start() {
        return this;
    }

    @Override
    public Span start(long timestamp) {
        return this;
    }

    @Override
    public Span name(String name) {
        return this;
    }

    @Override
    public Span kind(Span.Kind kind) {
        return this;
    }

    @Override
    public Span annotate(String value) {
        return this;
    }

    @Override
    public Span annotate(long timestamp, String value) {
        return this;
    }

    @Override
    public Span remoteServiceName(String remoteServiceName) {
        return this;
    }

    @Override
    public boolean remoteIpAndPort(String remoteIp, int port) {
        return true;
    }

    @Override
    public Span tag(String key, String value) {
        return this;
    }

    @Override
    public Span error(Throwable throwable) {
        return this;
    }

    @Override
    public void finish() {
    }

    @Override
    public void finish(long timestamp) {
    }

    @Override
    public void abandon() {
    }

    @Override
    public void flush() {
    }

    public String toString() {
        return "NoopSpan(" + this.context + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return NoopSpan.isEqualToNoopOrLazySpan(this.context, o);
    }

    static boolean isEqualToNoopOrLazySpan(TraceContext context, Object o) {
        if (o instanceof LazySpan) {
            return context.equals(((LazySpan)o).context);
        }
        if (o instanceof NoopSpan) {
            return context.equals(((NoopSpan)o).context);
        }
        return false;
    }

    public int hashCode() {
        return this.context.hashCode();
    }
}

