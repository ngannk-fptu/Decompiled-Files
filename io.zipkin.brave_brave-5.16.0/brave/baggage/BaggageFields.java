/*
 * Decompiled with CFR 0.152.
 */
package brave.baggage;

import brave.baggage.BaggageField;
import brave.internal.Nullable;
import brave.internal.baggage.BaggageContext;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;

public final class BaggageFields {
    public static final BaggageField TRACE_ID = new BaggageField("traceId", new TraceId());
    public static final BaggageField PARENT_ID = new BaggageField("parentId", new ParentId());
    public static final BaggageField SPAN_ID = new BaggageField("spanId", new SpanId());
    public static final BaggageField SAMPLED = new BaggageField("sampled", new Sampled());

    public static BaggageField constant(String name, @Nullable String value) {
        return new BaggageField(name, new Constant(value));
    }

    static final class Constant
    extends BaggageContext.ReadOnly {
        @Nullable
        final String value;

        Constant(String value) {
            this.value = value;
        }

        @Override
        public String getValue(BaggageField field, TraceContextOrSamplingFlags extracted) {
            return this.value;
        }

        @Override
        public String getValue(BaggageField field, TraceContext context) {
            return this.value;
        }
    }

    static final class Sampled
    extends BaggageContext.ReadOnly {
        Sampled() {
        }

        @Override
        public String getValue(BaggageField field, TraceContextOrSamplingFlags extracted) {
            return Sampled.getValue(extracted.sampled());
        }

        @Override
        public String getValue(BaggageField field, TraceContext context) {
            return Sampled.getValue(context.sampled());
        }

        @Nullable
        static String getValue(@Nullable Boolean sampled) {
            return sampled != null ? sampled.toString() : null;
        }
    }

    static final class SpanId
    extends BaggageContext.ReadOnly {
        SpanId() {
        }

        @Override
        public String getValue(BaggageField field, TraceContextOrSamplingFlags extracted) {
            if (extracted.context() != null) {
                return this.getValue(field, extracted.context());
            }
            return null;
        }

        @Override
        public String getValue(BaggageField field, TraceContext context) {
            return context.spanIdString();
        }
    }

    static final class ParentId
    extends BaggageContext.ReadOnly {
        ParentId() {
        }

        @Override
        public String getValue(BaggageField field, TraceContextOrSamplingFlags extracted) {
            if (extracted.context() != null) {
                return this.getValue(field, extracted.context());
            }
            return null;
        }

        @Override
        public String getValue(BaggageField field, TraceContext context) {
            return context.parentIdString();
        }
    }

    static final class TraceId
    extends BaggageContext.ReadOnly {
        TraceId() {
        }

        @Override
        public String getValue(BaggageField field, TraceContextOrSamplingFlags extracted) {
            if (extracted.context() != null) {
                return this.getValue(field, extracted.context());
            }
            if (extracted.traceIdContext() != null) {
                return extracted.traceIdContext().traceIdString();
            }
            return null;
        }

        @Override
        public String getValue(BaggageField field, TraceContext context) {
            return context.traceIdString();
        }
    }
}

