/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ToString;

@SdkPublicApi
public class Lazy<T>
implements SdkAutoCloseable {
    private final Supplier<T> initializer;
    private volatile T value;

    public Lazy(Supplier<T> initializer) {
        this.initializer = initializer;
    }

    public static <T> Lazy<T> withValue(T initialValue) {
        return new ResolvedLazy(initialValue);
    }

    public boolean hasValue() {
        return this.value != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T getValue() {
        T result = this.value;
        if (result == null) {
            Lazy lazy = this;
            synchronized (lazy) {
                result = this.value;
                if (result == null) {
                    this.value = result = this.initializer.get();
                }
            }
        }
        return result;
    }

    public String toString() {
        T value = this.value;
        return ToString.builder("Lazy").add("value", value == null ? "Uninitialized" : value).build();
    }

    @Override
    public void close() {
        try {
            this.getValue();
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        IoUtils.closeIfCloseable(this.initializer, null);
        IoUtils.closeIfCloseable(this.value, null);
    }

    private static class ResolvedLazy<T>
    extends Lazy<T> {
        private final T initialValue;

        private ResolvedLazy(T initialValue) {
            super(null);
            this.initialValue = initialValue;
        }

        @Override
        public boolean hasValue() {
            return true;
        }

        @Override
        public T getValue() {
            return this.initialValue;
        }

        @Override
        public String toString() {
            return ToString.builder("Lazy").add("value", this.initialValue).build();
        }

        @Override
        public void close() {
            IoUtils.closeIfCloseable(this.initialValue, null);
        }
    }
}

