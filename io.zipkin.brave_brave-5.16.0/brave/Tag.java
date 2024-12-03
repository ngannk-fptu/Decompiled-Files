/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.NoopSpanCustomizer;
import brave.ScopedSpan;
import brave.Span;
import brave.SpanCustomizer;
import brave.handler.MutableSpan;
import brave.internal.Nullable;
import brave.internal.Platform;
import brave.internal.Throwables;
import brave.propagation.TraceContext;

public abstract class Tag<I> {
    final String key;

    public final String key() {
        return this.key;
    }

    @Nullable
    protected abstract String parseValue(I var1, @Nullable TraceContext var2);

    @Nullable
    public String value(@Nullable I input, @Nullable TraceContext context) {
        if (input == null) {
            return null;
        }
        return this.parseValue(input, context);
    }

    protected String key(I input) {
        return this.key;
    }

    public final void tag(I input, ScopedSpan span) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (span == null) {
            throw new NullPointerException("span == null");
        }
        if (span.isNoop()) {
            return;
        }
        this.tag(span, input, span.context());
    }

    public final void tag(I input, Span span) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (span == null) {
            throw new NullPointerException("span == null");
        }
        if (span.isNoop()) {
            return;
        }
        this.tag(span, input, span.context());
    }

    public final void tag(I input, @Nullable TraceContext context, SpanCustomizer span) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (span == null) {
            throw new NullPointerException("span == null");
        }
        if (span == NoopSpanCustomizer.INSTANCE) {
            return;
        }
        this.tag(span, input, context);
    }

    public final void tag(I input, SpanCustomizer span) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (span == null) {
            throw new NullPointerException("span == null");
        }
        if (span == NoopSpanCustomizer.INSTANCE) {
            return;
        }
        this.tag(span, input, null);
    }

    public final void tag(I input, @Nullable TraceContext context, MutableSpan span) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (span == null) {
            throw new NullPointerException("span == null");
        }
        this.tag(span, input, context);
    }

    protected Tag(String key) {
        this.key = Tag.validateNonEmpty("key", key);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{" + this.key + "}";
    }

    final void tag(Object span, I input, @Nullable TraceContext context) {
        String key = null;
        String value = null;
        Throwable error = null;
        try {
            key = this.key(input);
            value = this.parseValue(input, context);
        }
        catch (Throwable e) {
            error = e;
            Throwables.propagateIfFatal(e);
        }
        if (key == null || key.isEmpty()) {
            Platform.get().log("Error parsing tag key of input %s", input, error);
            return;
        }
        if (error != null) {
            Platform.get().log("Error parsing tag value of input %s", input, error);
            return;
        }
        if (value == null) {
            return;
        }
        if (span instanceof SpanCustomizer) {
            ((SpanCustomizer)span).tag(key, value);
        } else if (span instanceof MutableSpan) {
            ((MutableSpan)span).tag(key, value);
        }
    }

    protected static String validateNonEmpty(String label, String value) {
        if (value == null) {
            throw new NullPointerException(label + " == null");
        }
        if ((value = value.trim()).isEmpty()) {
            throw new IllegalArgumentException(label + " is empty");
        }
        return value;
    }
}

