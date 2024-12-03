/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.querydsl;

import com.querydsl.core.types.EntityPath;

public interface EntityPathResolver {
    public <T> EntityPath<T> createPath(Class<T> var1);
}

