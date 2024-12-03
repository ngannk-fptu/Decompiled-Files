/*
 * Decompiled with CFR 0.152.
 */
package brave.handler;

import brave.handler.MutableSpan;
import brave.internal.Nullable;
import brave.propagation.TraceContext;

public abstract class SpanHandler {
    public static final SpanHandler NOOP = new SpanHandler(){

        public String toString() {
            return "NoopSpanHandler{}";
        }
    };

    public boolean begin(TraceContext context, MutableSpan span, @Nullable TraceContext parent) {
        return true;
    }

    public boolean end(TraceContext context, MutableSpan span, Cause cause) {
        return true;
    }

    public boolean handlesAbandoned() {
        return false;
    }

    public static enum Cause {
        ABANDONED,
        FINISHED,
        FLUSHED,
        ORPHANED;

    }
}

