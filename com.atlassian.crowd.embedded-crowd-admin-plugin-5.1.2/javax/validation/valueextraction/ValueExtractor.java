/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.valueextraction;

public interface ValueExtractor<T> {
    public void extractValues(T var1, ValueReceiver var2);

    public static interface ValueReceiver {
        public void value(String var1, Object var2);

        public void iterableValue(String var1, Object var2);

        public void indexedValue(String var1, int var2, Object var3);

        public void keyedValue(String var1, Object var2, Object var3);
    }
}

