/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CaseFormat
 *  com.google.common.collect.Maps
 */
package com.querydsl.core.types.dsl;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.Map;

public final class PathBuilderFactory {
    private final Map<Class<?>, PathBuilder<?>> paths = Maps.newConcurrentMap();
    private final String suffix;

    public PathBuilderFactory() {
        this("");
    }

    public PathBuilderFactory(String suffix) {
        this.suffix = suffix;
    }

    public <T> PathBuilder<T> create(Class<T> type) {
        PathBuilder<Object> rv = this.paths.get(type);
        if (rv == null) {
            rv = new PathBuilder<T>(type, this.variableName(type));
            this.paths.put(type, rv);
        }
        return rv;
    }

    private String variableName(Class<?> type) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, type.getSimpleName()) + this.suffix;
    }
}

