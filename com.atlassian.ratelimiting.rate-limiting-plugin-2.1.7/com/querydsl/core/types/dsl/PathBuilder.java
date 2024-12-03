/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.CollectionPath;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilderValidator;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.SetPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.TimePath;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PathBuilder<T>
extends EntityPathBase<T> {
    private static final long serialVersionUID = -1666357914232685088L;
    private final ConcurrentHashMap<AbstractMap.SimpleEntry<String, Class<?>>, PathBuilder<?>> properties = new ConcurrentHashMap();
    private final ConcurrentHashMap<Path<?>, Object> propertyMetadata = new ConcurrentHashMap();
    private final PathBuilderValidator validator;

    public PathBuilder(Class<? extends T> type, PathMetadata pathMetadata, PathBuilderValidator validator) {
        super(type, pathMetadata);
        this.validator = validator;
    }

    public PathBuilder(Class<? extends T> type, PathMetadata pathMetadata) {
        this(type, pathMetadata, PathBuilderValidator.DEFAULT);
    }

    public PathBuilder(Class<? extends T> type, String variable, PathBuilderValidator validator) {
        this(type, PathMetadataFactory.forVariable(variable), validator);
    }

    public PathBuilder(Class<? extends T> type, String variable) {
        this(type, PathMetadataFactory.forVariable(variable), PathBuilderValidator.DEFAULT);
    }

    private <P extends Path<?>> P addMetadataOf(P newPath, Path<?> path) {
        EntityPath parent;
        if (path.getMetadata().getParent() instanceof EntityPath && (parent = (EntityPath)path.getMetadata().getParent()).getMetadata(path) != null) {
            this.propertyMetadata.putIfAbsent(newPath, parent.getMetadata(path));
        }
        return newPath;
    }

    protected <A> Class<? extends A> validate(String property, Class<A> propertyType) {
        Class<?> validatedType = this.validator.validate(this.getType(), property, propertyType);
        if (validatedType != null) {
            return validatedType;
        }
        throw new IllegalArgumentException("Illegal property " + property);
    }

    @Override
    public Object getMetadata(Path<?> property) {
        return this.propertyMetadata.get(property);
    }

    public PathBuilder<Object> get(String property) {
        AbstractMap.SimpleEntry<String, Class<Object>> entry = new AbstractMap.SimpleEntry<String, Class<Object>>(property, Object.class);
        PathBuilder<Object> path = this.properties.get(entry);
        PathBuilder<Object> existingPath = null;
        if (path == null) {
            Class<Object> vtype = this.validate(property, Object.class);
            path = new PathBuilder<Object>(vtype, this.forProperty(property), this.validator);
            existingPath = this.properties.putIfAbsent(entry, path);
        }
        return existingPath == null ? path : existingPath;
    }

    public <A> PathBuilder<A> get(String property, Class<A> type) {
        AbstractMap.SimpleEntry<String, Class<A>> entry = new AbstractMap.SimpleEntry<String, Class<A>>(property, type);
        PathBuilder<Object> path = this.properties.get(entry);
        PathBuilder<?> existingPath = null;
        if (path == null) {
            Class<A> vtype = this.validate(property, type);
            path = new PathBuilder<A>(vtype, this.forProperty(property), this.validator);
            existingPath = this.properties.putIfAbsent(entry, path);
        }
        return existingPath == null ? path : existingPath;
    }

    public <A, E> ArrayPath<A, E> getArray(String property, Class<A> type) {
        this.validate(property, Array.newInstance(type, 0).getClass());
        return super.createArray(property, type);
    }

    public BooleanPath get(BooleanPath path) {
        BooleanPath newPath = this.getBoolean(this.toString(path));
        return this.addMetadataOf(newPath, path);
    }

    public BooleanPath getBoolean(String propertyName) {
        this.validate(propertyName, Boolean.class);
        return super.createBoolean(propertyName);
    }

    public <A> CollectionPath<A, PathBuilder<A>> getCollection(String property, Class<A> type) {
        return this.getCollection(property, type, PathBuilder.class);
    }

    public <A, E extends SimpleExpression<A>> CollectionPath<A, E> getCollection(String property, Class<A> type, Class<? super E> queryType) {
        this.validate(property, Collection.class);
        return super.createCollection(property, type, queryType, PathInits.DIRECT);
    }

    public <A extends Comparable<?>> ComparablePath<A> get(ComparablePath<A> path) {
        ComparablePath newPath = this.getComparable(this.toString(path), path.getType());
        return this.addMetadataOf(newPath, path);
    }

    public <A extends Comparable<?>> ComparablePath<A> getComparable(String property, Class<A> type) {
        Class<A> vtype = this.validate(property, type);
        return super.createComparable(property, vtype);
    }

    public <A extends Comparable<?>> DatePath<A> get(DatePath<A> path) {
        DatePath newPath = this.getDate(this.toString(path), path.getType());
        return this.addMetadataOf(newPath, path);
    }

    public <A extends Comparable<?>> DatePath<A> getDate(String property, Class<A> type) {
        Class<A> vtype = this.validate(property, type);
        return super.createDate(property, vtype);
    }

    public <A extends Comparable<?>> DateTimePath<A> get(DateTimePath<A> path) {
        DateTimePath newPath = this.getDateTime(this.toString(path), path.getType());
        return this.addMetadataOf(newPath, path);
    }

    public <A extends Comparable<?>> DateTimePath<A> getDateTime(String property, Class<A> type) {
        Class<A> vtype = this.validate(property, type);
        return super.createDateTime(property, vtype);
    }

    public <A extends Enum<A>> EnumPath<A> getEnum(String property, Class<A> type) {
        this.validate(property, type);
        return super.createEnum(property, type);
    }

    public <A extends Enum<A>> EnumPath<A> get(EnumPath<A> path) {
        EnumPath newPath = this.getEnum(this.toString(path), path.getType());
        return this.addMetadataOf(newPath, path);
    }

    public <A> ListPath<A, PathBuilder<A>> getList(String property, Class<A> type) {
        return this.getList(property, type, PathBuilder.class);
    }

    public <A, E extends SimpleExpression<A>> ListPath<A, E> getList(String property, Class<A> type, Class<? super E> queryType) {
        this.validate(property, List.class);
        return super.createList(property, type, queryType, PathInits.DIRECT);
    }

    public <K, V> MapPath<K, V, PathBuilder<V>> getMap(String property, Class<K> key, Class<V> value) {
        return this.getMap(property, key, value, PathBuilder.class);
    }

    public <K, V, E extends SimpleExpression<V>> MapPath<K, V, E> getMap(String property, Class<K> key, Class<V> value, Class<? super E> queryType) {
        this.validate(property, Map.class);
        return super.createMap(property, key, value, queryType);
    }

    public <A extends Number> NumberPath<A> get(NumberPath<A> path) {
        NumberPath newPath = this.getNumber(this.toString(path), path.getType());
        return this.addMetadataOf(newPath, path);
    }

    public <A extends Number> NumberPath<A> getNumber(String property, Class<A> type) {
        Class<A> vtype = this.validate(property, type);
        return super.createNumber(property, vtype);
    }

    public <A> SetPath<A, PathBuilder<A>> getSet(String property, Class<A> type) {
        return this.getSet(property, type, PathBuilder.class);
    }

    public <A, E extends SimpleExpression<A>> SetPath<A, E> getSet(String property, Class<A> type, Class<? super E> queryType) {
        this.validate(property, Set.class);
        return super.createSet(property, type, queryType, PathInits.DIRECT);
    }

    public <A> SimplePath<A> get(Path<A> path) {
        SimplePath newPath = this.getSimple(this.toString(path), path.getType());
        return this.addMetadataOf(newPath, path);
    }

    public <A> SimplePath<A> getSimple(String property, Class<A> type) {
        Class<A> vtype = this.validate(property, type);
        return super.createSimple(property, vtype);
    }

    public StringPath get(StringPath path) {
        StringPath newPath = this.getString(this.toString(path));
        return this.addMetadataOf(newPath, path);
    }

    public StringPath getString(String property) {
        this.validate(property, String.class);
        return super.createString(property);
    }

    public <A extends Comparable<?>> TimePath<A> get(TimePath<A> path) {
        TimePath newPath = this.getTime(this.toString(path), path.getType());
        return this.addMetadataOf(newPath, path);
    }

    public <A extends Comparable<?>> TimePath<A> getTime(String property, Class<A> type) {
        Class<A> vtype = this.validate(property, type);
        return super.createTime(property, vtype);
    }

    private String toString(Path<?> path) {
        return path.getMetadata().getElement().toString();
    }
}

