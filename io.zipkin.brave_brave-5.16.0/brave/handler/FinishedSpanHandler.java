/*
 * Decompiled with CFR 0.152.
 */
package brave.handler;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;

@Deprecated
public abstract class FinishedSpanHandler
extends SpanHandler {
    public static final FinishedSpanHandler NOOP = new FinishedSpanHandler(){

        @Override
        public boolean handle(TraceContext context, MutableSpan span) {
            return true;
        }

        public String toString() {
            return "NoopFinishedSpanHandler{}";
        }
    };

    public abstract boolean handle(TraceContext var1, MutableSpan var2);

    public boolean supportsOrphans() {
        return false;
    }

    @Deprecated
    public boolean alwaysSampleLocal() {
        return false;
    }

    @Override
    public boolean end(TraceContext context, MutableSpan span, SpanHandler.Cause cause) {
        switch (cause) {
            case FLUSHED: 
            case FINISHED: {
                return this.handle(context, span);
            }
            case ORPHANED: {
                return !this.supportsOrphans() || this.handle(context, span);
            }
        }
        assert (false) : "Bug!: missing state handling for " + (Object)((Object)cause);
        return true;
    }
}

