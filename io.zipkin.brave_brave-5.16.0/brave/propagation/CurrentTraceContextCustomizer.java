/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.propagation.CurrentTraceContext;

public interface CurrentTraceContextCustomizer {
    public static final CurrentTraceContextCustomizer NOOP = new CurrentTraceContextCustomizer(){

        @Override
        public void customize(CurrentTraceContext.Builder builder) {
        }

        public String toString() {
            return "NoopCurrentTraceContextCustomizer{}";
        }
    };

    public void customize(CurrentTraceContext.Builder var1);
}

