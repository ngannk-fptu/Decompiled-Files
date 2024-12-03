/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.SpanCustomizer;
import brave.Tag;
import brave.Tags;
import brave.handler.MutableSpan;
import brave.propagation.TraceContext;

public class ErrorParser
extends Tag<Throwable> {
    public static final ErrorParser NOOP = new ErrorParser(){

        @Override
        protected void error(Throwable error, Object customizer) {
        }
    };

    public ErrorParser() {
        super("error");
    }

    public final void error(Throwable error, SpanCustomizer customizer) {
        this.error(error, (Object)customizer);
    }

    public final void error(Throwable error, MutableSpan span) {
        this.error(error, (Object)span);
    }

    protected void error(Throwable error, Object span) {
        Tags.ERROR.tag(span, error, null);
    }

    static String parse(Throwable error) {
        if (error == null) {
            throw new NullPointerException("error == null");
        }
        String message = error.getMessage();
        if (message != null) {
            return message;
        }
        if (error.getClass().isAnonymousClass()) {
            return error.getClass().getSuperclass().getSimpleName();
        }
        return error.getClass().getSimpleName();
    }

    protected final void annotate(Object span, String value) {
        if (span instanceof SpanCustomizer) {
            ((SpanCustomizer)span).annotate(value);
        }
    }

    protected final void tag(Object span, String key, String message) {
        if (span instanceof SpanCustomizer) {
            ((SpanCustomizer)span).tag(key, message);
        } else if (span instanceof MutableSpan) {
            ((MutableSpan)span).tag(key, message);
        } else if (span instanceof KeyValueAdapter) {
            KeyValueAdapter keyValueAdapter = (KeyValueAdapter)span;
            keyValueAdapter.key = key;
            keyValueAdapter.value = message;
        }
    }

    @Override
    protected final String key(Throwable input) {
        if (this.getClass() == ErrorParser.class) {
            return Tags.ERROR.key();
        }
        KeyValueAdapter kv = new KeyValueAdapter();
        this.error(input, kv);
        return kv.key;
    }

    @Override
    protected final String parseValue(Throwable input, TraceContext context) {
        if (this.getClass() == ErrorParser.class) {
            return ErrorParser.parse(input);
        }
        KeyValueAdapter kv = new KeyValueAdapter();
        this.error(input, kv);
        return kv.value;
    }

    static final class KeyValueAdapter {
        String key;
        String value;

        KeyValueAdapter() {
        }
    }
}

