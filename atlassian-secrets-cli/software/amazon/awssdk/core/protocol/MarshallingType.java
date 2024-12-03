/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.protocol;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.document.Document;

@SdkProtectedApi
public interface MarshallingType<T> {
    public static final MarshallingType<Void> NULL = MarshallingType.newType(Void.class);
    public static final MarshallingType<String> STRING = MarshallingType.newType(String.class);
    public static final MarshallingType<Integer> INTEGER = MarshallingType.newType(Integer.class);
    public static final MarshallingType<Long> LONG = MarshallingType.newType(Long.class);
    public static final MarshallingType<Float> FLOAT = MarshallingType.newType(Float.class);
    public static final MarshallingType<Double> DOUBLE = MarshallingType.newType(Double.class);
    public static final MarshallingType<BigDecimal> BIG_DECIMAL = MarshallingType.newType(BigDecimal.class);
    public static final MarshallingType<Boolean> BOOLEAN = MarshallingType.newType(Boolean.class);
    public static final MarshallingType<Instant> INSTANT = MarshallingType.newType(Instant.class);
    public static final MarshallingType<SdkBytes> SDK_BYTES = MarshallingType.newType(SdkBytes.class);
    public static final MarshallingType<SdkPojo> SDK_POJO = MarshallingType.newType(SdkPojo.class);
    public static final MarshallingType<List<?>> LIST = MarshallingType.newType(List.class);
    public static final MarshallingType<Map<String, ?>> MAP = MarshallingType.newType(Map.class);
    public static final MarshallingType<Short> SHORT = MarshallingType.newType(Short.class);
    public static final MarshallingType<Document> DOCUMENT = MarshallingType.newType(Document.class);

    public Class<? super T> getTargetClass();

    public static <T> MarshallingType<T> newType(final Class<? super T> clzz) {
        return new MarshallingType<T>(){

            @Override
            public Class<? super T> getTargetClass() {
                return clzz;
            }

            public String toString() {
                return clzz.getSimpleName();
            }
        };
    }
}

