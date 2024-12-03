/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.propagation.ExtraFieldPropagation;

@Deprecated
public interface ExtraFieldCustomizer {
    public static final ExtraFieldCustomizer NOOP = new ExtraFieldCustomizer(){

        @Override
        public void customize(ExtraFieldPropagation.FactoryBuilder builder) {
        }

        public String toString() {
            return "NoopExtraFieldCustomizer{}";
        }
    };

    public void customize(ExtraFieldPropagation.FactoryBuilder var1);
}

