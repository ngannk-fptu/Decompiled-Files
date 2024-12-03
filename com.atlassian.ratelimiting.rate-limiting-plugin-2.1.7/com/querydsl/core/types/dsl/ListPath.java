/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ParameterizedPathImpl;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.CollectionPathBase;
import com.querydsl.core.types.dsl.ListExpression;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class ListPath<E, Q extends SimpleExpression<? super E>>
extends CollectionPathBase<List<E>, E, Q>
implements ListExpression<E, Q> {
    private static final long serialVersionUID = 3302301599074388860L;
    private final Map<Integer, Q> cache = new HashMap<Integer, Q>();
    private final Class<E> elementType;
    private final PathImpl<List<E>> pathMixin;
    private final Class<Q> queryType;
    @Nullable
    private transient Q any;

    protected ListPath(Class<? super E> elementType, Class<Q> queryType, String variable) {
        this(elementType, queryType, PathMetadataFactory.forVariable(variable));
    }

    protected ListPath(Class<? super E> elementType, Class<Q> queryType, Path<?> parent, String property) {
        this(elementType, queryType, PathMetadataFactory.forProperty(parent, property));
    }

    protected ListPath(Class<? super E> elementType, Class<Q> queryType, PathMetadata metadata) {
        this(elementType, queryType, metadata, PathInits.DIRECT);
    }

    protected ListPath(Class<? super E> elementType, Class<Q> queryType, PathMetadata metadata, PathInits inits) {
        super(new ParameterizedPathImpl<List>(List.class, metadata, elementType), inits);
        this.elementType = elementType;
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
            this.any = this.newInstance(this.queryType, PathMetadataFactory.forCollectionAny(this));
        }
        return this.any;
    }

    protected PathMetadata forListAccess(int index) {
        return PathMetadataFactory.forListAccess(this, index);
    }

    protected PathMetadata forListAccess(Expression<Integer> index) {
        return PathMetadataFactory.forListAccess(this, index);
    }

    private Q create(int index) {
        PathMetadata md = this.forListAccess(index);
        return this.newInstance(this.queryType, md);
    }

    @Override
    public Q get(Expression<Integer> index) {
        PathMetadata md = this.forListAccess(index);
        return this.newInstance(this.queryType, md);
    }

    @Override
    public Q get(int index) {
        if (this.cache.containsKey(index)) {
            return (Q)((SimpleExpression)this.cache.get(index));
        }
        Q rv = this.create(index);
        this.cache.put(index, rv);
        return rv;
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

