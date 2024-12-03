/*
 * Decompiled with CFR 0.152.
 */
package brave.baggage;

import brave.baggage.CorrelationScopeDecorator;

public interface CorrelationScopeCustomizer {
    public static final CorrelationScopeCustomizer NOOP = new CorrelationScopeCustomizer(){

        @Override
        public void customize(CorrelationScopeDecorator.Builder builder) {
        }

        public String toString() {
            return "NoopCorrelationScopeCustomizer{}";
        }
    };

    public void customize(CorrelationScopeDecorator.Builder var1);
}

