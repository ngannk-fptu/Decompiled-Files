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
public final class StringToValueConverter {
    public static final SimpleStringToValue<String> TO_STRING = val -> val;
    public static final SimpleStringToValue<Integer> TO_INTEGER = Integer::parseInt;
    public static final SimpleStringToValue<Long> TO_LONG = Long::parseLong;
    public static final SimpleStringToValue<Short> TO_SHORT = Short::parseShort;
    public static final SimpleStringToValue<Float> TO_FLOAT = Float::parseFloat;
    public static final SimpleStringToValue<Double> TO_DOUBLE = Double::parseDouble;
    public static final SimpleStringToValue<BigDecimal> TO_BIG_DECIMAL = BigDecimal::new;
    public static final SimpleStringToValue<Boolean> TO_BOOLEAN = Boolean::parseBoolean;
    public static final SimpleStringToValue<SdkBytes> TO_SDK_BYTES = StringToValueConverter::toSdkBytes;

    private StringToValueConverter() {
    }

    private static SdkBytes toSdkBytes(String s) {
        return SdkBytes.fromByteArray(BinaryUtils.fromBase64(s));
    }

    @FunctionalInterface
    public static interface SimpleStringToValue<T>
    extends StringToValue<T> {
        @Override
        default public T convert(String s, SdkField<T> sdkField) {
            return this.convert(s);
        }

        public T convert(String var1);
    }

    @FunctionalInterface
    public static interface StringToValue<T> {
        public T convert(String var1, SdkField<T> var2);
    }
}

