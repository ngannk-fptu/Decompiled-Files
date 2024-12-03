/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.dml;

import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import java.util.Map;

public interface Mapper<T> {
    public Map<Path<?>, Object> createMap(RelationalPath<?> var1, T var2);
}

