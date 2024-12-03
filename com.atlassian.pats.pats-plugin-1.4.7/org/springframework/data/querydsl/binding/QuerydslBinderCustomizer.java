/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.querydsl.binding;

import com.querydsl.core.types.EntityPath;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface QuerydslBinderCustomizer<T extends EntityPath<?>> {
    public void customize(QuerydslBindings var1, T var2);
}

