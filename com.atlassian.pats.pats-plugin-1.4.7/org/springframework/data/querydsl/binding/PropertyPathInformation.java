/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.querydsl.binding;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.CollectionPathBase;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.binding.PathInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

class PropertyPathInformation
implements PathInformation {
    private final PropertyPath path;

    private PropertyPathInformation(PropertyPath path) {
        this.path = path;
    }

    public static PropertyPathInformation of(String path, Class<?> type) {
        return PropertyPathInformation.of(PropertyPath.from(path, type));
    }

    public static PropertyPathInformation of(String path, TypeInformation<?> type) {
        return PropertyPathInformation.of(PropertyPath.from(path, type));
    }

    private static PropertyPathInformation of(PropertyPath path) {
        return new PropertyPathInformation(path);
    }

    @Override
    public Class<?> getRootParentType() {
        return this.path.getOwningType().getType();
    }

    @Override
    public Class<?> getLeafType() {
        return this.path.getLeafProperty().getType();
    }

    @Override
    public Class<?> getLeafParentType() {
        return this.path.getLeafProperty().getOwningType().getType();
    }

    @Override
    public String getLeafProperty() {
        return this.path.getLeafProperty().getSegment();
    }

    @Override
    @Nullable
    public PropertyDescriptor getLeafPropertyDescriptor() {
        return BeanUtils.getPropertyDescriptor(this.getLeafParentType(), (String)this.getLeafProperty());
    }

    @Override
    public String toDotPath() {
        return this.path.toDotPath();
    }

    @Override
    public Path<?> reifyPath(EntityPathResolver resolver) {
        return PropertyPathInformation.reifyPath(resolver, this.path, Optional.empty());
    }

    private static Path<?> reifyPath(EntityPathResolver resolver, PropertyPath path, Optional<Path<?>> base) {
        Optional<Path> map = base.filter(it -> it instanceof CollectionPathBase).map(CollectionPathBase.class::cast).map(CollectionPathBase::any).map(Path.class::cast).map(it -> PropertyPathInformation.reifyPath(resolver, path, Optional.of(it)));
        return map.orElseGet(() -> {
            Path entityPath = base.orElseGet(() -> resolver.createPath(path.getOwningType().getType()));
            Field field = org.springframework.data.util.ReflectionUtils.findRequiredField(entityPath.getClass(), path.getSegment());
            Object value = ReflectionUtils.getField((Field)field, (Object)entityPath);
            PropertyPath next = path.next();
            if (next != null) {
                return PropertyPathInformation.reifyPath(resolver, next, Optional.of((Path)value));
            }
            return (Path)value;
        });
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
        return "PropertyPathInformation(path=" + this.path + ")";
    }
}

