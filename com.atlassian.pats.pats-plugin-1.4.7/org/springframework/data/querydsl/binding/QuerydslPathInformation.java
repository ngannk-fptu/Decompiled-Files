/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.querydsl.binding;

import com.querydsl.core.types.Path;
import java.beans.PropertyDescriptor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.data.querydsl.binding.PathInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

class QuerydslPathInformation
implements PathInformation {
    private final Path<?> path;

    private QuerydslPathInformation(Path<?> path) {
        this.path = path;
    }

    public static QuerydslPathInformation of(Path<?> path) {
        return new QuerydslPathInformation(path);
    }

    @Override
    public Class<?> getRootParentType() {
        return this.path.getRoot().getType();
    }

    @Override
    public Class<?> getLeafType() {
        return this.path.getType();
    }

    @Override
    public Class<?> getLeafParentType() {
        Path<?> parent = this.path.getMetadata().getParent();
        if (parent == null) {
            throw new IllegalStateException(String.format("Could not obtain metadata for parent node of %s!", this.path));
        }
        return parent.getType();
    }

    @Override
    public String getLeafProperty() {
        return this.path.getMetadata().getElement().toString();
    }

    @Override
    @Nullable
    public PropertyDescriptor getLeafPropertyDescriptor() {
        return BeanUtils.getPropertyDescriptor(this.getLeafParentType(), (String)this.getLeafProperty());
    }

    @Override
    public String toDotPath() {
        return QuerydslUtils.toDotPath(this.path);
    }

    @Override
    public Path<?> reifyPath(EntityPathResolver resolver) {
        return this.path;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PathInformation)) {
            return false;
        }
        PathInformation that = (PathInformation)o;
        return ObjectUtils.nullSafeEquals(this.getRootParentType(), that.getRootParentType()) && ObjectUtils.nullSafeEquals((Object)this.toDotPath(), (Object)that.toDotPath());
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.getRootParentType());
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.toDotPath());
        return result;
    }

    public String toString() {
        return "QuerydslPathInformation(path=" + this.path + ")";
    }
}

