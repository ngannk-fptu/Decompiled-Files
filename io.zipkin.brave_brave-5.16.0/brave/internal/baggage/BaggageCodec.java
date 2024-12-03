/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.baggage;

import brave.baggage.BaggageField;
import brave.internal.Nullable;
import brave.propagation.TraceContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface BaggageCodec {
    public static final BaggageCodec NOOP = new BaggageCodec(){

        @Override
        public List<String> extractKeyNames() {
            return Collections.emptyList();
        }

        @Override
        public List<String> injectKeyNames() {
            return Collections.emptyList();
        }

        @Override
        public boolean decode(BaggageField.ValueUpdater valueUpdater, Object request, String value) {
            return false;
        }

        @Override
        public String encode(Map<String, String> values, TraceContext context, Object request) {
            return null;
        }

        public String toString() {
            return "NoopBaggageCodec";
        }
    };

    public List<String> extractKeyNames();

    public List<String> injectKeyNames();

    public boolean decode(BaggageField.ValueUpdater var1, Object var2, String var3);

    @Nullable
    public String encode(Map<String, String> var1, TraceContext var2, Object var3);
}

