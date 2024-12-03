/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import java.io.Closeable;

public final class StrictCurrentTraceContext
extends CurrentTraceContext
implements Closeable {
    final CurrentTraceContext delegate;
    final StrictScopeDecorator strictScopeDecorator;

    public static StrictCurrentTraceContext create() {
        return new StrictCurrentTraceContext();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public StrictCurrentTraceContext() {
        this(new Builder());
    }

    StrictCurrentTraceContext(Builder builder) {
        super(builder);
        this.delegate = builder.delegate;
        this.strictScopeDecorator = builder.strictScopeDecorator;
    }

    @Override
    public TraceContext get() {
        return this.delegate.get();
    }

    @Override
    public CurrentTraceContext.Scope newScope(@Nullable TraceContext context) {
        return this.decorateScope(context, this.delegate.newScope(context));
    }

    @Override
    public CurrentTraceContext.Scope maybeScope(TraceContext context) {
        return this.decorateScope(context, this.delegate.maybeScope(context));
    }

    @Override
    public void close() {
        this.strictScopeDecorator.close();
    }

    public static final class Builder
    extends CurrentTraceContext.Builder {
        final ThreadLocal<TraceContext> local = new ThreadLocal();
        CurrentTraceContext delegate = new ThreadLocalCurrentTraceContext.Builder(this.local).build();
        StrictScopeDecorator strictScopeDecorator = new StrictScopeDecorator();

        @Override
        public StrictCurrentTraceContext build() {
            this.delegate = new ThreadLocalCurrentTraceContext.Builder(this.local).addScopeDecorator(this.strictScopeDecorator).build();
            return new StrictCurrentTraceContext(this);
        }

        @Override
        public Builder addScopeDecorator(CurrentTraceContext.ScopeDecorator scopeDecorator) {
            if (scopeDecorator instanceof StrictScopeDecorator) {
                this.strictScopeDecorator = (StrictScopeDecorator)scopeDecorator;
                return this;
            }
            return (Builder)super.addScopeDecorator(scopeDecorator);
        }

        Builder() {
        }
    }
}

