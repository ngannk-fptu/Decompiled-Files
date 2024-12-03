/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.NoopSpan;
import brave.RealSpan;
import brave.Span;
import brave.SpanCustomizer;
import brave.SpanCustomizerShield;
import brave.Tracer;
import brave.propagation.TraceContext;

final class LazySpan
extends Span {
    final Tracer tracer;
    TraceContext context;
    Span delegate;

    LazySpan(Tracer tracer, TraceContext context) {
        this.tracer = tracer;
        this.context = context;
    }

    @Override
    public boolean isNoop() {
        return this.span().isNoop();
    }

    @Override
    public TraceContext context() {
        return this.span().context();
    }

    @Override
    public SpanCustomizer customizer() {
        return new SpanCustomizerShield(this);
    }

    @Override
    public Span start() {
        return this.span().start();
    }

    @Override
    public Span start(long timestamp) {
        return this.span().start(timestamp);
    }

    @Override
    public Span name(String name) {
        return this.span().name(name);
    }

    @Override
    public Span kind(Span.Kind kind) {
        return this.span().kind(kind);
    }

    @Override
    public Span annotate(String value) {
        return this.span().annotate(value);
    }

    @Override
    public Span annotate(long timestamp, String value) {
        return this.span().annotate(timestamp, value);
    }

    @Override
    public Span tag(String key, String value) {
        return this.span().tag(key, value);
    }

    @Override
    public Span error(Throwable throwable) {
        return this.span().error(throwable);
    }

    @Override
    public Span remoteServiceName(String remoteServiceName) {
        return this.span().remoteServiceName(remoteServiceName);
    }

    @Override
    public boolean remoteIpAndPort(String remoteIp, int remotePort) {
        return this.span().remoteIpAndPort(remoteIp, remotePort);
    }

    @Override
    public void finish() {
        this.span().finish();
    }

    @Override
    public void finish(long timestamp) {
        this.span().finish(timestamp);
    }

    @Override
    public void abandon() {
        if (this.delegate == null) {
            return;
        }
        this.span().abandon();
    }

    @Override
    public void flush() {
        if (this.delegate == null) {
            return;
        }
        this.span().flush();
    }

    public String toString() {
        return "LazySpan(" + this.context + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof LazySpan) {
            return this.context.equals(((LazySpan)o).context);
        }
        if (o instanceof RealSpan) {
            return this.context.equals(((RealSpan)o).context);
        }
        if (o instanceof NoopSpan) {
            return this.context.equals(((NoopSpan)o).context);
        }
        return false;
    }

    public int hashCode() {
        return this.context.hashCode();
    }

    Span span() {
        Span result = this.delegate;
        if (result != null) {
            return result;
        }
        this.delegate = this.tracer.toSpan(this.context);
        this.context = this.delegate.context();
        return this.delegate;
    }
}

