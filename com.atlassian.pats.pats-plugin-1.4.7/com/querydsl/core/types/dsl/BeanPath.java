/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionException;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.PathType;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.CollectionPath;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.SetPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.TimePath;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public class BeanPath<T>
extends SimpleExpression<T>
implements Path<T> {
    private static final long serialVersionUID = -1845524024957822731L;
    private final Map<Class<?>, Object> casts = new ConcurrentHashMap();
    @Nullable
    private final PathInits inits;
    private final PathImpl<T> pathMixin = (PathImpl)this.mixin;

    public BeanPath(Class<? extends T> type, String variable) {
        this(type, PathMetadataFactory.forVariable(variable), null);
    }

    public BeanPath(Class<? extends T> type, Path<?> parent, String property) {
        this(type, PathMetadataFactory.forProperty(parent, property), null);
    }

    public BeanPath(Class<? extends T> type, PathMetadata metadata) {
        this(type, metadata, null);
    }

    public BeanPath(Class<? extends T> type, PathMetadata metadata, @Nullable PathInits inits) {
        super(ExpressionUtils.path(type, metadata));
        this.inits = inits;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    public <U extends BeanPath<? extends T>> U as(Class<U> clazz) {
        try {
            if (!this.casts.containsKey(clazz)) {
                PathMetadata metadata = this.pathMixin.getMetadata().getPathType() != PathType.COLLECTION_ANY ? PathMetadataFactory.forDelegate(this.pathMixin) : this.pathMixin.getMetadata();
                BeanPath rv = this.inits != null && this.pathMixin.getMetadata().getPathType() != PathType.VARIABLE ? (BeanPath)clazz.getConstructor(PathMetadata.class, PathInits.class).newInstance(metadata, this.inits) : (BeanPath)clazz.getConstructor(PathMetadata.class).newInstance(metadata);
                this.casts.put(clazz, rv);
                return (U)rv;
            }
            return (U)((BeanPath)this.casts.get(clazz));
        }
        catch (InstantiationException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        catch (IllegalAccessException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        catch (InvocationTargetException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        catch (NoSuchMethodException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
    }

    protected <P extends Path<?>> P add(P path) {
        return path;
    }

    protected <A, E> ArrayPath<A, E> createArray(String property, Class<? super A> type) {
        return this.add(new ArrayPath(type, this.forProperty(property)));
    }

    protected BooleanPath createBoolean(String property) {
        return this.add(new BooleanPath(this.forProperty(property)));
    }

    protected <A, Q extends SimpleExpression<? super A>> CollectionPath<A, Q> createCollection(String property, Class<? super A> type, Class<? super Q> queryType, PathInits inits) {
        return this.add(new CollectionPath<A, Q>(type, queryType, this.forProperty(property), inits));
    }

    protected <A extends Comparable> ComparablePath<A> createComparable(String property, Class<? super A> type) {
        return this.add(new ComparablePath<A>(type, this.forProperty(property)));
    }

    protected <A extends Enum<A>> EnumPath<A> createEnum(String property, Class<A> type) {
        return this.add(new EnumPath<A>(type, this.forProperty(property)));
    }

    protected <A extends Comparable> DatePath<A> createDate(String property, Class<? super A> type) {
        return this.add(new DatePath<A>(type, this.forProperty(property)));
    }

    protected <A extends Comparable> DateTimePath<A> createDateTime(String property, Class<? super A> type) {
        return this.add(new DateTimePath<A>(type, this.forProperty(property)));
    }

    protected <A, E extends SimpleExpression<? super A>> ListPath<A, E> createList(String property, Class<? super A> type, Class<? super E> queryType, PathInits inits) {
        return this.add(new ListPath<A, E>(type, queryType, this.forProperty(property), inits));
    }

    protected <K, V, E extends SimpleExpression<? super V>> MapPath<K, V, E> createMap(String property, Class<? super K> key, Class<? super V> value, Class<? super E> queryType) {
        return this.add(new MapPath<K, V, E>(key, value, queryType, this.forProperty(property)));
    }

    protected <A extends Number> NumberPath<A> createNumber(String property, Class<? super A> type) {
        return this.add(new NumberPath<A>(type, this.forProperty(property)));
    }

    protected <A, E extends SimpleExpression<? super A>> SetPath<A, E> createSet(String property, Class<? super A> type, Class<? super E> queryType, PathInits inits) {
        return this.add(new SetPath<A, E>(type, queryType, this.forProperty(property), inits));
    }

    protected <A> SimplePath<A> createSimple(String property, Class<? super A> type) {
        return this.add(new SimplePath<A>(type, this.forProperty(property)));
    }

    protected StringPath createString(String property) {
        return this.add(new StringPath(this.forProperty(property)));
    }

    protected <A extends Comparable> TimePath<A> createTime(String property, Class<? super A> type) {
        return this.add(new TimePath<A>(type, this.forProperty(property)));
    }

    protected PathMetadata forProperty(String property) {
        return PathMetadataFactory.forProperty(this, property);
    }

    @Override
    public PathMetadata getMetadata() {
        return this.pathMixin.getMetadata();
    }

    @Override
    public Path<?> getRoot() {
        return this.pathMixin.getRoot();
    }

    public <B extends T> BooleanExpression instanceOf(Class<B> type) {
        return Expressions.booleanOperation(Ops.INSTANCE_OF, this.pathMixin, ConstantImpl.create(type));
    }

    public BooleanExpression instanceOfAny(Class ... types) {
        BooleanExpression[] exprs = new BooleanExpression[types.length];
        for (int i = 0; i < types.length; ++i) {
            exprs[i] = this.instanceOf(types[i]);
        }
        return Expressions.anyOf(exprs);
    }

    @Override
    public AnnotatedElement getAnnotatedElement() {
        return this.pathMixin.getAnnotatedElement();
    }
}

