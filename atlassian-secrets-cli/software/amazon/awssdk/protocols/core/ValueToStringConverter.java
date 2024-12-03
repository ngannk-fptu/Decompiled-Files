/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.core;

import java.math.BigDecimal;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkProtectedApi
public final class ValueToStringConverter {
    public static final SimpleValueToString<String> FROM_STRING = val -> val;
    public static final SimpleValueToString<Integer> FROM_INTEGER = Object::toString;
    public static final SimpleValueToString<Long> FROM_LONG = Object::toString;
    public static final SimpleValueToString<Short> FROM_SHORT = Object::toString;
    public static final SimpleValueToString<Float> FROM_FLOAT = Object::toString;
    public static final SimpleValueToString<Double> FROM_DOUBLE = Object::toString;
    public static final SimpleValueToString<BigDecimal> FROM_BIG_DECIMAL = Object::toString;
    public static final SimpleValueToString<Boolean> FROM_BOOLEAN = Object::toString;
    public static final SimpleValueToString<SdkBytes> FROM_SDK_BYTES = b -> BinaryUtils.toBase64(b.asByteArray());

    private ValueToStringConverter() {
    }

    @FunctionalInterface
    public static interface SimpleValueToString<T>
    extends ValueToString<T> {
        @Override
        default public String convert(T t, SdkField<T> field) {
            return this.convert(t);
        }

        public String convert(T var1);
    }

    @FunctionalInterface
    public static interface ValueToString<T> {
        public String convert(T var1, SdkField<T> var2);
    }
}

