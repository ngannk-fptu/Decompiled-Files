/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.util.ReflectionUtils
 */
package com.atlassian.confluence.internal.util.reflection;

import java.lang.reflect.Method;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.util.ReflectionUtils;

public final class ReflectionUtil {
    public static @NonNull Optional<Method> findMethod(Class<?> owningClass, String name) {
        return Optional.ofNullable(ReflectionUtils.findMethod(owningClass, (String)name));
    }

    private ReflectionUtil() {
    }
}

