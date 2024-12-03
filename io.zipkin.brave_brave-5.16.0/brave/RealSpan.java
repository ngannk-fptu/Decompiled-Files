/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.Clock;
import brave.LazySpan;
import brave.Span;
import brave.SpanCustomizer;
import brave.SpanCustomizerShield;
import brave.handler.MutableSpan;
import brave.internal.recorder.PendingSpans;
import brave.propagation.TraceContext;

final class RealSpan
extends Span {
    final TraceContext context;
    final PendingSpans pendingSpans;
    final MutableSpan state;
    final Clock clock;

    RealSpan(TraceContext context, PendingSpans pendingSpans, MutableSpan state, Clock clock) {
        this.context = context;
        this.pendingSpans = pendingSpans;
        this.state = state;
        this.clock = clock;
    }

    @Override
    public boolean isNoop() {
        return false;
    }

    @Override
    public TraceContext context() {
        return this.context;
    }

    @Override
    public SpanCustomizer customizer() {
        return new SpanCustomizerShield(this);
    }

    @Override
    public Span start() {
        return this.start(this.clock.currentTimeMicroseconds());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Span start(long timestamp) {
        MutableSpan mutableSpan = this.state;
        synchronized (mutableSpan) {
            this.state.startTimestamp(timestamp);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Span name(String name) {
        MutableSpan mutableSpan = this.state;
        synchronized (mutableSpan) {
            this.state.name(name);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Span kind(Span.Kind kind) {
        MutableSpan mutableSpan = this.state;
        synchronized (mutableSpan) {
            this.state.kind(kind);
        }
        return this;
    }

    @Override
    public Span annotate(String value) {
        return this.annotate(this.clock.currentTimeMicroseconds(), value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Span annotate(long timestamp, String value) {
        if ("cs".equals(value)) {
            MutableSpan mutableSpan = this.state;
            synchronized (mutableSpan) {
                this.state.kind(Span.Kind.CLIENT);
                this.state.startTimestamp(timestamp);
            }
        }
        if ("sr".equals(value)) {
            MutableSpan mutableSpan = this.state;
            synchronized (mutableSpan) {
                this.state.kind(Span.Kind.SERVER);
                this.state.startTimestamp(timestamp);
            }
        }
        if ("cr".equals(value)) {
            MutableSpan mutableSpan = this.state;
            synchronized (mutableSpan) {
                this.state.kind(Span.Kind.CLIENT);
            }
            this.finish(timestamp);
        } else if ("ss".equals(value)) {
            MutableSpan mutableSpan = this.state;
            synchronized (mutableSpan) {
                this.state.kind(Span.Kind.SERVER);
            }
            this.finish(timestamp);
        } else {
            MutableSpan mutableSpan = this.state;
            synchronized (mutableSpan) {
                this.state.annotate(timestamp, value);
            }
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Span tag(String key, String value) {
        MutableSpan mutableSpan = this.state;
        synchronized (mutableSpan) {
            this.state.tag(key, value);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Span error(Throwable throwable) {
        MutableSpan mutableSpan = this.state;
        synchronized (mutableSpan) {
            this.state.error(throwable);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Span remoteServiceName(String remoteServiceName) {
        MutableSpan mutableSpan = this.state;
        synchronized (mutableSpan) {
            this.state.remoteServiceName(remoteServiceName);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remoteIpAndPort(String remoteIp, int remotePort) {
        MutableSpan mutableSpan = this.state;
        synchronized (mutableSpan) {
            return this.state.remoteIpAndPort(remoteIp, remotePort);
        }
    }

    @Override
    public void finish() {
        this.finish(0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void finish(long timestamp) {
        MutableSpan mutableSpan = this.state;
        synchronized (mutableSpan) {
            this.pendingSpans.finish(this.context, timestamp);
        }
    }

    @Override
    public void abandon() {
        this.pendingSpans.abandon(this.context);
    }

    @Override
    public void flush() {
        this.pendingSpans.flush(this.context);
    }

    public String toString() {
        return "RealSpan(" + this.context + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return RealSpan.isEqualToRealOrLazySpan(this.context, o);
    }

    static boolean isEqualToRealOrLazySpan(TraceContext context, Object o) {
        if (o instanceof LazySpan) {
            return context.equals(((LazySpan)o).context);
        }
        if (o instanceof RealSpan) {
            return context.equals(((RealSpan)o).context);
        }
        return false;
    }

    public int hashCode() {
        return this.context.hashCode();
    }
}

