/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.runtime;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class TypeConverter {
    private TypeConverter() {
    }

    public static <T, U> U convert(T toConvert, Function<? super T, ? extends U> converter) {
        if (toConvert == null) {
            return null;
        }
        return converter.apply(toConvert);
    }

    public static <T, U> List<U> convert(List<T> toConvert, Function<? super T, ? extends U> converter) {
        if (toConvert == null) {
            return null;
        }
        List result = toConvert.stream().map(converter).collect(Collectors.toList());
        return Collections.unmodifiableList(result);
    }

    public static <T1, T2, U1, U2> Map<U1, U2> convert(Map<T1, T2> toConvert, Function<? super T1, ? extends U1> keyConverter, Function<? super T2, ? extends U2> valueConverter, BiPredicate<U1, U2> resultFilter) {
        if (toConvert == null) {
            return null;
        }
        Map<Object, Object> result = toConvert.entrySet().stream().map(e -> new AbstractMap.SimpleImmutableEntry(keyConverter.apply((Object)e.getKey()), valueConverter.apply((Object)e.getValue()))).filter(p -> resultFilter.test(p.getKey(), p.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return Collections.unmodifiableMap(result);
    }
}

