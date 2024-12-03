/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;

public class ThreadLocalCurrentTraceContext
extends CurrentTraceContext {
    static final ThreadLocal<TraceContext> DEFAULT = new ThreadLocal();
    final ThreadLocal<TraceContext> local;
    final RevertToNullScope revertToNull;

    public static CurrentTraceContext create() {
        return new Builder(DEFAULT).build();
    }

    public static Builder newBuilder() {
        return new Builder(DEFAULT);
    }

    public void clear() {
        this.local.remove();
    }

    ThreadLocalCurrentTraceContext(Builder builder) {
        super(builder);
        if (builder.local == null) {
            throw new NullPointerException("local == null");
        }
        this.local = builder.local;
        this.revertToNull = new RevertToNullScope(this.local);
    }

    @Override
    public TraceContext get() {
        return this.local.get();
    }

    @Override
    public CurrentTraceContext.Scope newScope(@Nullable TraceContext currentSpan) {
        TraceContext previous = this.local.get();
        this.local.set(currentSpan);
        CurrentTraceContext.Scope result = previous != null ? new RevertToPreviousScope(this.local, previous) : this.revertToNull;
        return this.decorateScope(currentSpan, result);
    }

    static final class RevertToPreviousScope
    implements CurrentTraceContext.Scope {
        final ThreadLocal<TraceContext> local;
        final TraceContext previous;

        RevertToPreviousScope(ThreadLocal<TraceContext> local, TraceContext previous) {
            this.local = local;
            this.previous = previous;
        }

        @Override
        public void close() {
            this.local.set(this.previous);
        }
    }

    static final class RevertToNullScope
    implements CurrentTraceContext.Scope {
        final ThreadLocal<TraceContext> local;

        RevertToNullScope(ThreadLocal<TraceContext> local) {
            this.local = local;
        }

        @Override
        public void close() {
            this.local.set(null);
        }
    }

    public static final class Builder
    extends CurrentTraceContext.Builder {
        final ThreadLocal<TraceContext> local;

        Builder(ThreadLocal<TraceContext> local) {
            this.local = local;
        }

        @Override
        public Builder addScopeDecorator(CurrentTraceContext.ScopeDecorator scopeDecorator) {
            return (Builder)super.addScopeDecorator(scopeDecorator);
        }

        @Override
        public ThreadLocalCurrentTraceContext build() {
            return new ThreadLocalCurrentTraceContext(this);
        }
    }
}

