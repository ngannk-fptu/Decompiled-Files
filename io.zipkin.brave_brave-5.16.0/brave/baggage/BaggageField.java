/*
 * Decompiled with CFR 0.152.
 */
package brave.baggage;

import brave.Tracing;
import brave.baggage.CorrelationFlushScope;
import brave.internal.Nullable;
import brave.internal.baggage.BaggageContext;
import brave.internal.baggage.ExtraBaggageContext;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class BaggageField {
    final String name;
    final String lcName;
    final BaggageContext context;

    public static BaggageField create(String name) {
        return new BaggageField(name, ExtraBaggageContext.get());
    }

    @Deprecated
    public static List<BaggageField> getAll(@Nullable TraceContext context) {
        if (context == null) {
            return Collections.emptyList();
        }
        return ExtraBaggageContext.getAllFields(context);
    }

    @Deprecated
    public static List<BaggageField> getAll(TraceContextOrSamplingFlags extracted) {
        if (extracted == null) {
            throw new NullPointerException("extracted == null");
        }
        return ExtraBaggageContext.getAllFields(extracted);
    }

    @Deprecated
    @Nullable
    public static List<BaggageField> getAll() {
        return BaggageField.getAll(BaggageField.currentTraceContext());
    }

    public static Map<String, String> getAllValues(@Nullable TraceContext context) {
        if (context == null) {
            return Collections.emptyMap();
        }
        return ExtraBaggageContext.getAllValues(context);
    }

    public static Map<String, String> getAllValues(TraceContextOrSamplingFlags extracted) {
        if (extracted == null) {
            throw new NullPointerException("extracted == null");
        }
        return ExtraBaggageContext.getAllValues(extracted);
    }

    @Nullable
    public static Map<String, String> getAllValues() {
        return BaggageField.getAllValues(BaggageField.currentTraceContext());
    }

    @Nullable
    public static BaggageField getByName(@Nullable TraceContext context, String name) {
        if (context == null) {
            return null;
        }
        return ExtraBaggageContext.getFieldByName(context, BaggageField.validateName(name));
    }

    @Nullable
    public static BaggageField getByName(TraceContextOrSamplingFlags extracted, String name) {
        if (extracted == null) {
            throw new NullPointerException("extracted == null");
        }
        return ExtraBaggageContext.getFieldByName(extracted, BaggageField.validateName(name));
    }

    @Nullable
    public static BaggageField getByName(String name) {
        return BaggageField.getByName(BaggageField.currentTraceContext(), name);
    }

    BaggageField(String name, BaggageContext context) {
        this.name = BaggageField.validateName(name);
        this.lcName = name.toLowerCase(Locale.ROOT);
        this.context = context;
    }

    public final String name() {
        return this.name;
    }

    @Nullable
    public String getValue(@Nullable TraceContext context) {
        if (context == null) {
            return null;
        }
        return this.context.getValue(this, context);
    }

    @Nullable
    public String getValue() {
        return this.getValue(BaggageField.currentTraceContext());
    }

    @Nullable
    public String getValue(TraceContextOrSamplingFlags extracted) {
        if (extracted == null) {
            throw new NullPointerException("extracted == null");
        }
        return this.context.getValue(this, extracted);
    }

    public boolean updateValue(@Nullable TraceContext context, @Nullable String value) {
        if (context == null) {
            return false;
        }
        if (this.context.updateValue(this, context, value)) {
            CorrelationFlushScope.flush(this, value);
            return true;
        }
        return false;
    }

    public boolean updateValue(TraceContextOrSamplingFlags extracted, @Nullable String value) {
        if (extracted == null) {
            throw new NullPointerException("extracted == null");
        }
        if (this.context.updateValue(this, extracted, value)) {
            CorrelationFlushScope.flush(this, value);
            return true;
        }
        return false;
    }

    public boolean updateValue(String value) {
        return this.updateValue(BaggageField.currentTraceContext(), value);
    }

    public String toString() {
        return "BaggageField{" + this.name + "}";
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BaggageField)) {
            return false;
        }
        return this.lcName.equals(((BaggageField)o).lcName);
    }

    public final int hashCode() {
        return this.lcName.hashCode();
    }

    static String validateName(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if ((name = name.trim()).isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        }
        return name;
    }

    @Nullable
    static TraceContext currentTraceContext() {
        Tracing tracing = Tracing.current();
        return tracing != null ? tracing.currentTraceContext().get() : null;
    }

    public static interface ValueUpdater {
        public static final ValueUpdater NOOP = new ValueUpdater(){

            @Override
            public boolean updateValue(BaggageField field, String value) {
                return false;
            }

            public String toString() {
                return "NoopValueUpdater{}";
            }
        };

        public boolean updateValue(BaggageField var1, @Nullable String var2);
    }
}

