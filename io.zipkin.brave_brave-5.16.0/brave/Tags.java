/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.ErrorParser;
import brave.Tag;
import brave.baggage.BaggageField;
import brave.propagation.TraceContext;

public final class Tags {
    public static final Tag<Throwable> ERROR = new Tag<Throwable>("error"){

        @Override
        protected String parseValue(Throwable input, TraceContext context) {
            return ErrorParser.parse(input);
        }
    };
    public static final Tag<BaggageField> BAGGAGE_FIELD = new Tag<BaggageField>("baggageField"){

        @Override
        protected String key(BaggageField input) {
            return input.name();
        }

        @Override
        protected String parseValue(BaggageField input, TraceContext context) {
            return input.getValue(context);
        }
    };

    Tags() {
    }
}

