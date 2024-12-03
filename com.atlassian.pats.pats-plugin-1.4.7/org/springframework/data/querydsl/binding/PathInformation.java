/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.querydsl.binding;

import com.querydsl.core.types.Path;
import java.beans.PropertyDescriptor;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.lang.Nullable;

interface PathInformation {
    public Class<?> getRootParentType();

    public Class<?> getLeafType();

    public Class<?> getLeafParentType();

    public String getLeafProperty();

    @Nullable
    public PropertyDescriptor getLeafPropertyDescriptor();

    public String toDotPath();

    public Path<?> reifyPath(EntityPathResolver var1);
}

