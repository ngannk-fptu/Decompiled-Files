/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.internal;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.ImmutableMap;

@SdkInternalApi
public final class ReflectionUtils {
    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new ImmutableMap.Builder<Class<Boolean>, Class<Boolean>>().put(Boolean.TYPE, Boolean.class).put(Byte.TYPE, Byte.class).put(Character.TYPE, Character.class).put(Double.TYPE, Double.class).put(Float.TYPE, Float.class).put(Integer.TYPE, Integer.class).put(Long.TYPE, Long.class).put(Short.TYPE, Short.class).put(Void.TYPE, Void.class).build();

    private ReflectionUtils() {
    }

    public static Class<?> getWrappedClass(Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            return clazz;
        }
        return PRIMITIVES_TO_WRAPPERS.getOrDefault(clazz, clazz);
    }
}

