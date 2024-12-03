/*
 * Decompiled with CFR 0.152.
 */
package brave.baggage;

import brave.baggage.BaggagePropagation;

public interface BaggagePropagationCustomizer {
    public static final BaggagePropagationCustomizer NOOP = new BaggagePropagationCustomizer(){

        @Override
        public void customize(BaggagePropagation.FactoryBuilder builder) {
        }

        public String toString() {
            return "NoopBaggagePropagationCustomizer{}";
        }
    };

    public void customize(BaggagePropagation.FactoryBuilder var1);
}

