/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ParameterizedPathImpl;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.CollectionPathBase;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import javax.annotation.Nullable;

public class CollectionPath<E, Q extends SimpleExpression<? super E>>
extends CollectionPathBase<Collection<E>, E, Q> {
    private static final long serialVersionUID = -4982311799113762600L;
    private final Class<E> elementType;
    private final PathImpl<Collection<E>> pathMixin;
    @Nullable
    private transient Q any;
    private final Class<Q> queryType;

    protected CollectionPath(Class<? super E> type, Class<Q> queryType, String variable) {
        this(type, queryType, PathMetadataFactory.forVariable(variable));
    }

    protected CollectionPath(Class<? super E> type, Class<Q> queryType, Path<?> parent, String property) {
        this(type, queryType, PathMetadataFactory.forProperty(parent, property));
    }

    protected CollectionPath(Class<? super E> type, Class<Q> queryType, PathMetadata metadata) {
        this(type, queryType, metadata, PathInits.DIRECT);
    }

    protected CollectionPath(Class<? super E> type, Class<Q> queryType, PathMetadata metadata, PathInits inits) {
        super(new ParameterizedPathImpl<Collection>(Collection.class, metadata, type), inits);
        this.elementType = type;
        this.queryType = queryType;
        this.pathMixin = (PathImpl)this.mixin;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this.pathMixin, context);
    }

    @Override
    public Q any() {
        if (this.any == null) {
            this.any = this.newInstance(this.queryType, PathMetadataFactory.forCollectionAny(this.pathMixin));
        }
        return this.any;
    }

    @Override
    public Class<E> getElementType() {
        return this.elementType;
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
    public Class<?> getParameter(int index) {
        if (index == 0) {
            return this.elementType;
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }
}

