/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

import java.lang.annotation.Annotation;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public abstract class KotlinDetector {
    @Nullable
    private static final Class<? extends Annotation> kotlinMetadata;

    public static boolean isKotlinPresent() {
        return kotlinMetadata != null;
    }

    public static boolean isKotlinType(Class<?> clazz) {
        return kotlinMetadata != null && clazz.getDeclaredAnnotation(kotlinMetadata) != null;
    }

    static {
        Class<?> metadata;
        try {
            metadata = ClassUtils.forName("kotlin.Metadata", KotlinDetector.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            metadata = null;
        }
        kotlinMetadata = metadata;
    }
}

