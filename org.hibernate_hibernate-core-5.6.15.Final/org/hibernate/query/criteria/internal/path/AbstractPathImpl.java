/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Path
 *  javax.persistence.metamodel.Attribute
 *  javax.persistence.metamodel.MapAttribute
 *  javax.persistence.metamodel.PluralAttribute
 *  javax.persistence.metamodel.PluralAttribute$CollectionType
 *  javax.persistence.metamodel.SingularAttribute
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.expression.PathTypeExpression;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;

public abstract class AbstractPathImpl<X>
extends ExpressionImpl<X>
implements Path<X>,
PathImplementor<X>,
Serializable {
    private final PathSource pathSource;
    private final Expression<Class<? extends X>> typeExpression;
    private Map<String, Path> attributePathRegistry;

    public AbstractPathImpl(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, PathSource pathSource) {
        super(criteriaBuilder, javaType);
        this.pathSource = pathSource;
        this.typeExpression = new PathTypeExpression(this.criteriaBuilder(), this.getJavaType(), this);
    }

    public PathSource getPathSource() {
        return this.pathSource;
    }

    public PathSource<?> getParentPath() {
        return this.getPathSource();
    }

    public Expression<Class<? extends X>> type() {
        return this.typeExpression;
    }

    @Override
    public String getPathIdentifier() {
        return this.getPathSource().getPathIdentifier() + "." + this.getAttribute().getName();
    }

    protected abstract boolean canBeDereferenced();

    protected final RuntimeException illegalDereference() {
        return new IllegalStateException(String.format("Illegal attempt to dereference path source [%s] of basic type", this.getPathIdentifier()));
    }

    protected final RuntimeException unknownAttribute(String attributeName) {
        String message = "Unable to resolve attribute [" + attributeName + "] against path";
        PathSource source = this.getPathSource();
        if (source != null) {
            message = message + " [" + source.getPathIdentifier() + "]";
        }
        return new IllegalArgumentException(message);
    }

    protected final Path resolveCachedAttributePath(String attributeName) {
        return this.attributePathRegistry == null ? null : this.attributePathRegistry.get(attributeName);
    }

    protected final void registerAttributePath(String attributeName, Path path) {
        if (this.attributePathRegistry == null) {
            this.attributePathRegistry = new HashMap<String, Path>();
        }
        this.attributePathRegistry.put(attributeName, path);
    }

    public <Y> Path<Y> get(SingularAttribute<? super X, Y> attribute) {
        if (!this.canBeDereferenced()) {
            throw this.illegalDereference();
        }
        SingularAttributePath<Y> path = (SingularAttributePath<Y>)this.resolveCachedAttributePath(attribute.getName());
        if (path == null) {
            path = new SingularAttributePath<Y>(this.criteriaBuilder(), attribute.getJavaType(), this.getPathSourceForSubPaths(), attribute);
            this.registerAttributePath(attribute.getName(), path);
        }
        return path;
    }

    protected PathSource getPathSourceForSubPaths() {
        return this;
    }

    public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<X, C, E> attribute) {
        if (!this.canBeDereferenced()) {
            throw this.illegalDereference();
        }
        PluralAttributePath<C> path = (PluralAttributePath<C>)this.resolveCachedAttributePath(attribute.getName());
        if (path == null) {
            path = new PluralAttributePath<C>(this.criteriaBuilder(), this, attribute);
            this.registerAttributePath(attribute.getName(), path);
        }
        return path;
    }

    public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<X, K, V> attribute) {
        if (!this.canBeDereferenced()) {
            throw this.illegalDereference();
        }
        PluralAttributePath<K> path = (PluralAttributePath<K>)this.resolveCachedAttributePath(attribute.getName());
        if (path == null) {
            path = new PluralAttributePath<K>(this.criteriaBuilder(), this, attribute);
            this.registerAttributePath(attribute.getName(), path);
        }
        return path;
    }

    public <Y> Path<Y> get(String attributeName) {
        if (!this.canBeDereferenced()) {
            throw this.illegalDereference();
        }
        Attribute attribute = this.locateAttribute(attributeName);
        if (attribute.isCollection()) {
            PluralAttribute pluralAttribute = (PluralAttribute)attribute;
            if (PluralAttribute.CollectionType.MAP.equals((Object)pluralAttribute.getCollectionType())) {
                return (PluralAttributePath)this.get((MapAttribute)pluralAttribute);
            }
            return (PluralAttributePath)this.get(pluralAttribute);
        }
        return this.get((SingularAttribute)attribute);
    }

    protected final Attribute locateAttribute(String attributeName) {
        Attribute attribute = this.locateAttributeInternal(attributeName);
        if (attribute == null) {
            throw this.unknownAttribute(attributeName);
        }
        return attribute;
    }

    protected abstract Attribute locateAttributeInternal(String var1);

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public void prepareAlias(RenderingContext renderingContext) {
        PathSource source = this.getPathSource();
        if (source != null) {
            source.prepareAlias(renderingContext);
        }
    }

    @Override
    public String render(RenderingContext renderingContext) {
        PathSource source = this.getPathSource();
        if (source != null) {
            source.prepareAlias(renderingContext);
            return source.getPathIdentifier() + "." + this.getAttribute().getName();
        }
        return this.getAttribute().getName();
    }
}

