/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.Clock;
import brave.ScopedSpan;
import brave.handler.MutableSpan;
import brave.internal.recorder.PendingSpans;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;

final class RealScopedSpan
extends ScopedSpan {
    final TraceContext context;
    final CurrentTraceContext.Scope scope;
    final MutableSpan state;
    final Clock clock;
    final PendingSpans pendingSpans;

    RealScopedSpan(TraceContext context, CurrentTraceContext.Scope scope, MutableSpan state, Clock clock, PendingSpans pendingSpans) {
        this.context = context;
        this.scope = scope;
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
    public ScopedSpan name(String name) {
        this.state.name(name);
        return this;
    }

    @Override
    public ScopedSpan tag(String key, String value) {
        this.state.tag(key, value);
        return this;
    }

    @Override
    public ScopedSpan annotate(String value) {
        this.state.annotate(this.clock.currentTimeMicroseconds(), value);
        return this;
    }

    @Override
    public ScopedSpan error(Throwable throwable) {
        this.state.error(throwable);
        return this;
    }

    @Override
    public void finish() {
        this.scope.close();
        this.pendingSpans.finish(this.context, 0L);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RealScopedSpan)) {
            return false;
        }
        RealScopedSpan that = (RealScopedSpan)o;
        return this.context.equals(that.context) && this.scope.equals(that.scope);
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.context.hashCode();
        h *= 1000003;
        return h ^= this.scope.hashCode();
    }
}

