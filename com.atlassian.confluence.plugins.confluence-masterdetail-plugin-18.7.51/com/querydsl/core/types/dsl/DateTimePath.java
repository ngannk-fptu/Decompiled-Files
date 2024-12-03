/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.DateTimeExpression;
import java.lang.reflect.AnnotatedElement;

public class DateTimePath<T extends Comparable>
extends DateTimeExpression<T>
implements Path<T> {
    private static final long serialVersionUID = -2807916228198184631L;
    private final PathImpl<T> pathMixin;

    protected DateTimePath(PathImpl<T> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }

    protected DateTimePath(Class<? extends T> type, Path<?> parent, String property) {
        this(type, PathMetadataFactory.forProperty(parent, property));
    }

    protected DateTimePath(Class<? extends T> type, PathMetadata metadata) {
        super(ExpressionUtils.path(type, metadata));
        this.pathMixin = (PathImpl)this.mixin;
    }

    protected DateTimePath(Class<? extends T> type, String var) {
        this(type, PathMetadataFactory.forVariable(var));
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this.pathMixin, context);
    }

    @Override
    public PathMetadata getMetadata() {
        return this.pathMixin.getMetadata();
    }

    @Override
    public Path<?> getRoot() {
        return this.pathMixin.getRoot();
    }

    @Override
    public AnnotatedElement getAnnotatedElement() {
        return this.pathMixin.getAnnotatedElement();
    }
}

