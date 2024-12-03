/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Primitives
 */
package com.querydsl.core.types.dsl;

import com.google.common.primitives.Primitives;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.ArrayExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimplePath;
import java.lang.reflect.AnnotatedElement;
import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

public class ArrayPath<A, E>
extends SimpleExpression<A>
implements Path<A>,
ArrayExpression<A, E> {
    private static final long serialVersionUID = 7795049264874048226L;
    private final Class<E> componentType;
    private final PathImpl<A> pathMixin;
    @Nullable
    private volatile transient NumberExpression<Integer> size;

    protected ArrayPath(Class<? super A> type, String variable) {
        this(type, PathMetadataFactory.forVariable(variable));
    }

    protected ArrayPath(Class<? super A> type, Path<?> parent, String property) {
        this(type, PathMetadataFactory.forProperty(parent, property));
    }

    protected ArrayPath(Class<? super A> type, PathMetadata metadata) {
        super(ExpressionUtils.path(type, metadata));
        this.pathMixin = (PathImpl)this.mixin;
        this.componentType = Primitives.wrap(type.getComponentType());
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this.pathMixin, context);
    }

    @Override
    public SimplePath<E> get(Expression<Integer> index) {
        PathMetadata md = PathMetadataFactory.forArrayAccess(this.pathMixin, index);
        return Expressions.path(this.componentType, md);
    }

    @Override
    public SimplePath<E> get(@Nonnegative int index) {
        PathMetadata md = PathMetadataFactory.forArrayAccess(this.pathMixin, index);
        return Expressions.path(this.componentType, md);
    }

    public Class<E> getElementType() {
        return this.componentType;
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

    @Override
    public NumberExpression<Integer> size() {
        if (this.size == null) {
            this.size = Expressions.numberOperation(Integer.class, Ops.ARRAY_SIZE, this.pathMixin);
        }
        return this.size;
    }
}

