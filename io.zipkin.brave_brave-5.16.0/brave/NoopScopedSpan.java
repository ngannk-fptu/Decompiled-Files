/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.ScopedSpan;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;

final class NoopScopedSpan
extends ScopedSpan {
    final TraceContext context;
    final CurrentTraceContext.Scope scope;

    NoopScopedSpan(TraceContext context, CurrentTraceContext.Scope scope) {
        this.context = context;
        this.scope = scope;
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
    public ScopedSpan name(String name) {
        return this;
    }

    @Override
    public ScopedSpan tag(String key, String value) {
        return this;
    }

    @Override
    public ScopedSpan annotate(String value) {
        return this;
    }

    @Override
    public ScopedSpan error(Throwable throwable) {
        return this;
    }

    @Override
    public void finish() {
        this.scope.close();
    }

    public String toString() {
        return "NoopScopedSpan(" + this.context + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NoopScopedSpan)) {
            return false;
        }
        NoopScopedSpan that = (NoopScopedSpan)o;
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

