/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.internal;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.CollectionUtils;

@SdkInternalApi
public final class EnumUtils {
    private EnumUtils() {
    }

    public static <K, V extends Enum<V>> Map<K, V> uniqueIndex(Class<V> enumType, Function<? super V, K> indexFunction) {
        return CollectionUtils.uniqueIndex(EnumSet.allOf(enumType), indexFunction);
    }
}

