/*
 * Decompiled with CFR 0.152.
 */
package brave.http;

import brave.http.HttpTracing;

public interface HttpTracingCustomizer {
    public static final HttpTracingCustomizer NOOP = new HttpTracingCustomizer(){

        @Override
        public void customize(HttpTracing.Builder builder) {
        }

        public String toString() {
            return "NoopHttpTracingCustomizer{}";
        }
    };

    public void customize(HttpTracing.Builder var1);
}

