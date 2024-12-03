/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.Tracing;

public interface TracingCustomizer {
    public static final TracingCustomizer NOOP = new TracingCustomizer(){

        @Override
        public void customize(Tracing.Builder builder) {
        }

        public String toString() {
            return "NoopTracingCustomizer{}";
        }
    };

    public void customize(Tracing.Builder var1);
}

