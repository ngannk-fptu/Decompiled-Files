/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Lazy
 */
package software.amazon.awssdk.services.s3.internal;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Lazy;

@SdkInternalApi
public abstract class FieldWithDefault<T> {
    private FieldWithDefault() {
    }

    public static <T> FieldWithDefault<T> create(T field, T defaultValue) {
        return new Impl(field, defaultValue);
    }

    public static <T> FieldWithDefault<T> createLazy(T field, Supplier<T> defaultValue) {
        return new LazyImpl(field, defaultValue);
    }

    public abstract T value();

    public abstract boolean isDefault();

    public abstract T valueOrNullIfDefault();

    private static class LazyImpl<T>
    extends FieldWithDefault<T> {
        private final Lazy<T> value;
        private final boolean isDefault;

        private LazyImpl(T field, Supplier<T> defaultValue) {
            this.value = field != null ? new Lazy(() -> field) : new Lazy(defaultValue);
            this.isDefault = field == null;
        }

        @Override
        public T value() {
            return (T)this.value.getValue();
        }

        @Override
        public boolean isDefault() {
            return this.isDefault;
        }

        @Override
        public T valueOrNullIfDefault() {
            return (T)(this.isDefault ? null : this.value.getValue());
        }
    }

    private static class Impl<T>
    extends FieldWithDefault<T> {
        private final T value;
        private final boolean isDefault;

        private Impl(T field, T defaultValue) {
            this.value = field != null ? field : defaultValue;
            this.isDefault = field == null;
        }

        @Override
        public T value() {
            return this.value;
        }

        @Override
        public boolean isDefault() {
            return this.isDefault;
        }

        @Override
        public T valueOrNullIfDefault() {
            return this.isDefault ? null : (T)this.value;
        }
    }
}

