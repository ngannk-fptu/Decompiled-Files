/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.handler;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.internal.Platform;
import brave.internal.Throwables;
import brave.propagation.TraceContext;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NoopAwareSpanHandler
extends SpanHandler {
    final SpanHandler delegate;
    final AtomicBoolean noop;

    public static SpanHandler create(SpanHandler[] handlers, AtomicBoolean noop) {
        if (handlers.length == 0) {
            return SpanHandler.NOOP;
        }
        if (handlers.length == 1) {
            return new NoopAwareSpanHandler(handlers[0], noop);
        }
        return new NoopAwareSpanHandler(new CompositeSpanHandler(handlers), noop);
    }

    NoopAwareSpanHandler(SpanHandler delegate, AtomicBoolean noop) {
        this.delegate = delegate;
        this.noop = noop;
    }

    @Override
    public boolean begin(TraceContext context, MutableSpan span, TraceContext parent) {
        if (this.noop.get()) {
            return false;
        }
        try {
            return this.delegate.begin(context, span, parent);
        }
        catch (Throwable t) {
            Throwables.propagateIfFatal(t);
            Platform.get().log("error handling begin {0}", context, t);
            return true;
        }
    }

    @Override
    public boolean end(TraceContext context, MutableSpan span, SpanHandler.Cause cause) {
        if (this.noop.get()) {
            return false;
        }
        try {
            return this.delegate.end(context, span, cause);
        }
        catch (Throwable t) {
            Throwables.propagateIfFatal(t);
            Platform.get().log("error handling end {0}", context, t);
            return true;
        }
    }

    @Override
    public boolean handlesAbandoned() {
        return this.delegate.handlesAbandoned();
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    public String toString() {
        return this.delegate.toString();
    }

    static final class CompositeSpanHandler
    extends SpanHandler {
        final boolean handlesAbandoned;
        final SpanHandler[] handlers;

        CompositeSpanHandler(SpanHandler[] handlers) {
            this.handlers = handlers;
            boolean handlesAbandoned = false;
            for (SpanHandler handler : handlers) {
                if (!handler.handlesAbandoned()) continue;
                handlesAbandoned = true;
                break;
            }
            this.handlesAbandoned = handlesAbandoned;
        }

        @Override
        public boolean begin(TraceContext context, MutableSpan span, TraceContext parent) {
            for (SpanHandler handler : this.handlers) {
                if (handler.begin(context, span, parent)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean end(TraceContext context, MutableSpan span, SpanHandler.Cause cause) {
            for (SpanHandler handler : this.handlers) {
                if (cause == SpanHandler.Cause.ABANDONED && !handler.handlesAbandoned() || handler.end(context, span, cause)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean handlesAbandoned() {
            return this.handlesAbandoned;
        }

        public int hashCode() {
            return Arrays.hashCode(this.handlers);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof CompositeSpanHandler)) {
                return false;
            }
            return Arrays.equals(((CompositeSpanHandler)obj).handlers, this.handlers);
        }

        public String toString() {
            return Arrays.toString(this.handlers);
        }
    }
}

