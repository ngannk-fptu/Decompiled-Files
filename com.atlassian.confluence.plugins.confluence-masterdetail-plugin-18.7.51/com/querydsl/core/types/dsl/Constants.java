/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.TimePath;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class Constants {
    private static final Set<Class<?>> typedClasses = new HashSet<Class>(Arrays.asList(ArrayPath.class, PathBuilder.class, ComparablePath.class, EnumPath.class, DatePath.class, DateTimePath.class, BeanPath.class, EntityPathBase.class, NumberPath.class, SimplePath.class, TimePath.class));

    public static boolean isTyped(Class<?> cl) {
        return typedClasses.contains(cl);
    }

    private Constants() {
    }
}

