/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core.types;

import com.querydsl.core.types.ExpressionBase;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.PathType;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.util.ReflectionUtils;
import java.lang.reflect.AnnotatedElement;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PathImpl<T>
extends ExpressionBase<T>
implements Path<T> {
    private static final long serialVersionUID = -2498447742798348162L;
    private final PathMetadata metadata;
    private final Path<?> root;
    @Nullable
    private transient AnnotatedElement annotatedElement;

    protected PathImpl(Class<? extends T> type, String variable) {
        this(type, PathMetadataFactory.forVariable(variable));
    }

    protected PathImpl(Class<? extends T> type, PathMetadata metadata) {
        super(type);
        this.metadata = metadata;
        this.root = metadata.getRootPath() != null ? metadata.getRootPath() : this;
    }

    protected PathImpl(Class<? extends T> type, Path<?> parent, String property) {
        this(type, PathMetadataFactory.forProperty(parent, property));
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Path) {
            return ((Path)o).getMetadata().equals(this.metadata);
        }
        return false;
    }

    @Override
    public final PathMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public final Path<?> getRoot() {
        return this.root;
    }

    @Override
    public final AnnotatedElement getAnnotatedElement() {
        if (this.annotatedElement == null) {
            if (this.metadata.getPathType() == PathType.PROPERTY) {
                Class beanClass = this.metadata.getParent().getType();
                String propertyName = this.metadata.getName();
                this.annotatedElement = ReflectionUtils.getAnnotatedElement(beanClass, propertyName, this.getType());
            } else {
                this.annotatedElement = this.getType();
            }
        }
        return this.annotatedElement;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }
}

