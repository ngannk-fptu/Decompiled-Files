/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.baggage;

import brave.baggage.BaggageField;
import brave.internal.Nullable;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;

public abstract class BaggageContext {
    @Nullable
    public abstract String getValue(BaggageField var1, TraceContextOrSamplingFlags var2);

    @Nullable
    public abstract String getValue(BaggageField var1, TraceContext var2);

    public abstract boolean updateValue(BaggageField var1, TraceContextOrSamplingFlags var2, @Nullable String var3);

    public abstract boolean updateValue(BaggageField var1, TraceContext var2, @Nullable String var3);

    public static abstract class ReadOnly
    extends BaggageContext {
        @Override
        public boolean updateValue(BaggageField field, TraceContextOrSamplingFlags extracted, @Nullable String value) {
            return false;
        }

        @Override
        public boolean updateValue(BaggageField field, TraceContext context, String value) {
            return false;
        }
    }
}

