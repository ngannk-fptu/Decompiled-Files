/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionException;
import com.querydsl.core.types.ParameterizedPathImpl;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Constants;
import com.querydsl.core.types.dsl.MapExpressionBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javax.annotation.Nullable;

public class MapPath<K, V, E extends SimpleExpression<? super V>>
extends MapExpressionBase<K, V, E>
implements Path<Map<K, V>> {
    private static final long serialVersionUID = -9113333728412016832L;
    private final Class<K> keyType;
    private final PathImpl<Map<K, V>> pathMixin;
    private final Class<E> queryType;
    @Nullable
    private transient Constructor<E> constructor;
    private final Class<V> valueType;

    protected MapPath(Class<? super K> keyType, Class<? super V> valueType, Class<E> queryType, String variable) {
        this(keyType, valueType, queryType, PathMetadataFactory.forVariable(variable));
    }

    protected MapPath(Class<? super K> keyType, Class<? super V> valueType, Class<E> queryType, Path<?> parent, String property) {
        this(keyType, valueType, queryType, PathMetadataFactory.forProperty(parent, property));
    }

    protected MapPath(Class<? super K> keyType, Class<? super V> valueType, Class<E> queryType, PathMetadata metadata) {
        super(new ParameterizedPathImpl(Map.class, metadata, keyType, valueType));
        this.keyType = keyType;
        this.valueType = valueType;
        this.queryType = queryType;
        this.pathMixin = (PathImpl)this.mixin;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this.pathMixin, context);
    }

    protected PathMetadata forMapAccess(K key) {
        return PathMetadataFactory.forMapAccess(this, key);
    }

    protected PathMetadata forMapAccess(Expression<K> key) {
        return PathMetadataFactory.forMapAccess(this, key);
    }

    @Override
    public E get(Expression<K> key) {
        try {
            PathMetadata md = this.forMapAccess(key);
            return this.newInstance(md);
        }
        catch (NoSuchMethodException e) {
            throw new ExpressionException(e);
        }
        catch (InstantiationException e) {
            throw new ExpressionException(e);
        }
        catch (IllegalAccessException e) {
            throw new ExpressionException(e);
        }
        catch (InvocationTargetException e) {
            throw new ExpressionException(e);
        }
    }

    @Override
    public E get(K key) {
        try {
            PathMetadata md = this.forMapAccess(key);
            return this.newInstance(md);
        }
        catch (NoSuchMethodException e) {
            throw new ExpressionException(e);
        }
        catch (InstantiationException e) {
            throw new ExpressionException(e);
        }
        catch (IllegalAccessException e) {
            throw new ExpressionException(e);
        }
        catch (InvocationTargetException e) {
            throw new ExpressionException(e);
        }
    }

    public Class<K> getKeyType() {
        return this.keyType;
    }

    @Override
    public PathMetadata getMetadata() {
        return this.pathMixin.getMetadata();
    }

    @Override
    public Path<?> getRoot() {
        return this.pathMixin.getRoot();
    }

    public Class<V> getValueType() {
        return this.valueType;
    }

    @Override
    public AnnotatedElement getAnnotatedElement() {
        return this.pathMixin.getAnnotatedElement();
    }

    private E newInstance(PathMetadata pm) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (this.constructor == null) {
            this.constructor = Constants.isTyped(this.queryType) ? this.queryType.getDeclaredConstructor(Class.class, PathMetadata.class) : this.queryType.getDeclaredConstructor(PathMetadata.class);
            this.constructor.setAccessible(true);
        }
        if (Constants.isTyped(this.queryType)) {
            return (E)((SimpleExpression)this.constructor.newInstance(this.getValueType(), pm));
        }
        return (E)((SimpleExpression)this.constructor.newInstance(pm));
    }

    @Override
    public Class<?> getParameter(int index) {
        if (index == 0) {
            return this.keyType;
        }
        if (index == 1) {
            return this.valueType;
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }
}

