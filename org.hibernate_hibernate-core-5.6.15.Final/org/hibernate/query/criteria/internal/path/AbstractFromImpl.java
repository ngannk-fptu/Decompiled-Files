/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CollectionJoin
 *  javax.persistence.criteria.Fetch
 *  javax.persistence.criteria.From
 *  javax.persistence.criteria.Join
 *  javax.persistence.criteria.JoinType
 *  javax.persistence.criteria.ListJoin
 *  javax.persistence.criteria.MapJoin
 *  javax.persistence.criteria.SetJoin
 *  javax.persistence.metamodel.Attribute
 *  javax.persistence.metamodel.CollectionAttribute
 *  javax.persistence.metamodel.ListAttribute
 *  javax.persistence.metamodel.ManagedType
 *  javax.persistence.metamodel.MapAttribute
 *  javax.persistence.metamodel.PluralAttribute
 *  javax.persistence.metamodel.PluralAttribute$CollectionType
 *  javax.persistence.metamodel.SetAttribute
 *  javax.persistence.metamodel.SingularAttribute
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.hibernate.query.criteria.internal.BasicPathUsageException;
import org.hibernate.query.criteria.internal.CollectionJoinImplementor;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.FromImplementor;
import org.hibernate.query.criteria.internal.JoinImplementor;
import org.hibernate.query.criteria.internal.ListJoinImplementor;
import org.hibernate.query.criteria.internal.MapJoinImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.SetJoinImplementor;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.AbstractPathImpl;
import org.hibernate.query.criteria.internal.path.CollectionAttributeJoin;
import org.hibernate.query.criteria.internal.path.ListAttributeJoin;
import org.hibernate.query.criteria.internal.path.MapAttributeJoin;
import org.hibernate.query.criteria.internal.path.SetAttributeJoin;
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin;

public abstract class AbstractFromImpl<Z, X>
extends AbstractPathImpl<X>
implements From<Z, X>,
FromImplementor<Z, X>,
Serializable {
    public static final JoinType DEFAULT_JOIN_TYPE = JoinType.INNER;
    private Set<Join<X, ?>> joins;
    private Set<Fetch<X, ?>> fetches;
    private FromImplementor<Z, X> correlationParent;
    private JoinScope<X> joinScope = new BasicJoinScope();

    public AbstractFromImpl(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType) {
        this(criteriaBuilder, javaType, null);
    }

    public AbstractFromImpl(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, PathSource pathSource) {
        super(criteriaBuilder, javaType, pathSource);
    }

    @Override
    public PathSource<Z> getPathSource() {
        return super.getPathSource();
    }

    @Override
    public String getPathIdentifier() {
        return this.getAlias();
    }

    @Override
    protected boolean canBeDereferenced() {
        return true;
    }

    @Override
    public void prepareAlias(RenderingContext renderingContext) {
        if (this.getAlias() == null) {
            if (this.canBeReplacedByCorrelatedParentInSubQuery()) {
                this.setAlias(this.getCorrelationParent().getAlias());
            } else {
                this.setAlias(renderingContext.generateAlias());
            }
        }
    }

    @Override
    public String render(RenderingContext renderingContext) {
        this.prepareAlias(renderingContext);
        return this.getAlias();
    }

    @Override
    public Attribute<?, ?> getAttribute() {
        return null;
    }

    public From<?, Z> getParent() {
        return null;
    }

    @Override
    protected Attribute<X, ?> locateAttributeInternal(String name) {
        return this.locateManagedType().getAttribute(name);
    }

    protected ManagedType<? super X> locateManagedType() {
        return (ManagedType)this.getModel();
    }

    public boolean isCorrelated() {
        return this.correlationParent != null;
    }

    @Override
    public FromImplementor<Z, X> getCorrelationParent() {
        if (this.correlationParent == null) {
            throw new IllegalStateException(String.format("Criteria query From node [%s] is not part of a subquery correlation", this.getPathIdentifier()));
        }
        return this.correlationParent;
    }

    @Override
    public FromImplementor<Z, X> correlateTo(CriteriaSubqueryImpl subquery) {
        FromImplementor<Z, X> correlationDelegate = this.createCorrelationDelegate();
        correlationDelegate.prepareCorrelationDelegate(this);
        return correlationDelegate;
    }

    protected abstract FromImplementor<Z, X> createCorrelationDelegate();

    @Override
    public void prepareCorrelationDelegate(FromImplementor<Z, X> parent) {
        this.joinScope = new CorrelationJoinScope();
        this.correlationParent = parent;
    }

    @Override
    public String getAlias() {
        return this.canBeReplacedByCorrelatedParentInSubQuery() ? this.getCorrelationParent().getAlias() : super.getAlias();
    }

    protected abstract boolean canBeJoinSource();

    protected RuntimeException illegalJoin() {
        return new IllegalArgumentException("Collection of values [" + this.getPathIdentifier() + "] cannot be source of a join");
    }

    public Set<Join<X, ?>> getJoins() {
        return this.joins == null ? Collections.EMPTY_SET : this.joins;
    }

    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> singularAttribute) {
        return this.join(singularAttribute, DEFAULT_JOIN_TYPE);
    }

    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute, JoinType jt) {
        if (!this.canBeJoinSource()) {
            throw this.illegalJoin();
        }
        JoinImplementor<? super X, Y> join = this.constructJoin(attribute, jt);
        this.joinScope.addJoin(join);
        return join;
    }

    private <Y> JoinImplementor<X, Y> constructJoin(SingularAttribute<? super X, Y> attribute, JoinType jt) {
        if (Type.PersistenceType.BASIC.equals((Object)attribute.getType().getPersistenceType())) {
            throw new BasicPathUsageException("Cannot join to attribute of basic type", (Attribute<?, ?>)attribute);
        }
        if (jt.equals((Object)JoinType.RIGHT)) {
            throw new UnsupportedOperationException("RIGHT JOIN not supported");
        }
        Class attributeType = attribute.getBindableJavaType();
        return new SingularAttributeJoin(this.criteriaBuilder(), attributeType, this, attribute, jt);
    }

    public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection) {
        return this.join(collection, DEFAULT_JOIN_TYPE);
    }

    public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection, JoinType jt) {
        if (!this.canBeJoinSource()) {
            throw this.illegalJoin();
        }
        CollectionJoinImplementor<? super X, Y> join = this.constructJoin(collection, jt);
        this.joinScope.addJoin(join);
        return join;
    }

    private <Y> CollectionJoinImplementor<X, Y> constructJoin(CollectionAttribute<? super X, Y> collection, JoinType jt) {
        if (jt.equals((Object)JoinType.RIGHT)) {
            throw new UnsupportedOperationException("RIGHT JOIN not supported");
        }
        Class attributeType = collection.getBindableJavaType();
        return new CollectionAttributeJoin<X, Y>(this.criteriaBuilder(), attributeType, this, collection, jt);
    }

    public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> set) {
        return this.join(set, DEFAULT_JOIN_TYPE);
    }

    public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> set, JoinType jt) {
        if (!this.canBeJoinSource()) {
            throw this.illegalJoin();
        }
        SetJoinImplementor<? super X, Y> join = this.constructJoin(set, jt);
        this.joinScope.addJoin(join);
        return join;
    }

    private <Y> SetJoinImplementor<X, Y> constructJoin(SetAttribute<? super X, Y> set, JoinType jt) {
        if (jt.equals((Object)JoinType.RIGHT)) {
            throw new UnsupportedOperationException("RIGHT JOIN not supported");
        }
        Class attributeType = set.getBindableJavaType();
        return new SetAttributeJoin<X, Y>(this.criteriaBuilder(), attributeType, this, set, jt);
    }

    public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list) {
        return this.join(list, DEFAULT_JOIN_TYPE);
    }

    public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list, JoinType jt) {
        if (!this.canBeJoinSource()) {
            throw this.illegalJoin();
        }
        ListJoinImplementor<? super X, Y> join = this.constructJoin(list, jt);
        this.joinScope.addJoin(join);
        return join;
    }

    private <Y> ListJoinImplementor<X, Y> constructJoin(ListAttribute<? super X, Y> list, JoinType jt) {
        if (jt.equals((Object)JoinType.RIGHT)) {
            throw new UnsupportedOperationException("RIGHT JOIN not supported");
        }
        Class attributeType = list.getBindableJavaType();
        return new ListAttributeJoin<X, Y>(this.criteriaBuilder(), attributeType, this, list, jt);
    }

    public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map) {
        return this.join(map, DEFAULT_JOIN_TYPE);
    }

    public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map, JoinType jt) {
        if (!this.canBeJoinSource()) {
            throw this.illegalJoin();
        }
        MapJoinImplementor<? super X, K, V> join = this.constructJoin(map, jt);
        this.joinScope.addJoin(join);
        return join;
    }

    private <K, V> MapJoinImplementor<X, K, V> constructJoin(MapAttribute<? super X, K, V> map, JoinType jt) {
        if (jt.equals((Object)JoinType.RIGHT)) {
            throw new UnsupportedOperationException("RIGHT JOIN not supported");
        }
        Class attributeType = map.getBindableJavaType();
        return new MapAttributeJoin<X, K, V>(this.criteriaBuilder(), attributeType, this, map, jt);
    }

    public <X, Y> Join<X, Y> join(String attributeName) {
        return this.join(attributeName, DEFAULT_JOIN_TYPE);
    }

    public <X, Y> Join<X, Y> join(String attributeName, JoinType jt) {
        if (!this.canBeJoinSource()) {
            throw this.illegalJoin();
        }
        if (jt.equals((Object)JoinType.RIGHT)) {
            throw new UnsupportedOperationException("RIGHT JOIN not supported");
        }
        Attribute attribute = this.locateAttribute(attributeName);
        if (attribute.isCollection()) {
            PluralAttribute pluralAttribute = (PluralAttribute)attribute;
            if (PluralAttribute.CollectionType.COLLECTION.equals((Object)pluralAttribute.getCollectionType())) {
                return this.join((CollectionAttribute)attribute, jt);
            }
            if (PluralAttribute.CollectionType.LIST.equals((Object)pluralAttribute.getCollectionType())) {
                return this.join((ListAttribute)attribute, jt);
            }
            if (PluralAttribute.CollectionType.SET.equals((Object)pluralAttribute.getCollectionType())) {
                return this.join((SetAttribute)attribute, jt);
            }
            return this.join((MapAttribute)attribute, jt);
        }
        return this.join((SingularAttribute)attribute, jt);
    }

    public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName) {
        return this.joinCollection(attributeName, DEFAULT_JOIN_TYPE);
    }

    public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName, JoinType jt) {
        Attribute attribute = this.locateAttribute(attributeName);
        if (!attribute.isCollection()) {
            throw new IllegalArgumentException("Requested attribute was not a collection");
        }
        PluralAttribute pluralAttribute = (PluralAttribute)attribute;
        if (!PluralAttribute.CollectionType.COLLECTION.equals((Object)pluralAttribute.getCollectionType())) {
            throw new IllegalArgumentException("Requested attribute was not a collection");
        }
        return this.join((CollectionAttribute)attribute, jt);
    }

    public <X, Y> SetJoin<X, Y> joinSet(String attributeName) {
        return this.joinSet(attributeName, DEFAULT_JOIN_TYPE);
    }

    public <X, Y> SetJoin<X, Y> joinSet(String attributeName, JoinType jt) {
        Attribute attribute = this.locateAttribute(attributeName);
        if (!attribute.isCollection()) {
            throw new IllegalArgumentException("Requested attribute was not a set");
        }
        PluralAttribute pluralAttribute = (PluralAttribute)attribute;
        if (!PluralAttribute.CollectionType.SET.equals((Object)pluralAttribute.getCollectionType())) {
            throw new IllegalArgumentException("Requested attribute was not a set");
        }
        return this.join((SetAttribute)attribute, jt);
    }

    public <X, Y> ListJoin<X, Y> joinList(String attributeName) {
        return this.joinList(attributeName, DEFAULT_JOIN_TYPE);
    }

    public <X, Y> ListJoin<X, Y> joinList(String attributeName, JoinType jt) {
        Attribute attribute = this.locateAttribute(attributeName);
        if (!attribute.isCollection()) {
            throw new IllegalArgumentException("Requested attribute was not a list");
        }
        PluralAttribute pluralAttribute = (PluralAttribute)attribute;
        if (!PluralAttribute.CollectionType.LIST.equals((Object)pluralAttribute.getCollectionType())) {
            throw new IllegalArgumentException("Requested attribute was not a list");
        }
        return this.join((ListAttribute)attribute, jt);
    }

    public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName) {
        return this.joinMap(attributeName, DEFAULT_JOIN_TYPE);
    }

    public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName, JoinType jt) {
        Attribute attribute = this.locateAttribute(attributeName);
        if (!attribute.isCollection()) {
            throw new IllegalArgumentException("Requested attribute was not a map");
        }
        PluralAttribute pluralAttribute = (PluralAttribute)attribute;
        if (!PluralAttribute.CollectionType.MAP.equals((Object)pluralAttribute.getCollectionType())) {
            throw new IllegalArgumentException("Requested attribute was not a map");
        }
        return this.join((MapAttribute)attribute, jt);
    }

    protected boolean canBeFetchSource() {
        return this.canBeJoinSource();
    }

    protected RuntimeException illegalFetch() {
        return new IllegalArgumentException("Collection of values [" + this.getPathIdentifier() + "] cannot be source of a fetch");
    }

    public Set<Fetch<X, ?>> getFetches() {
        return this.fetches == null ? Collections.EMPTY_SET : this.fetches;
    }

    public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> singularAttribute) {
        return this.fetch(singularAttribute, DEFAULT_JOIN_TYPE);
    }

    public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute, JoinType jt) {
        if (!this.canBeFetchSource()) {
            throw this.illegalFetch();
        }
        JoinImplementor<? super X, Y> fetch = this.constructJoin(attribute, jt);
        this.joinScope.addFetch(fetch);
        return fetch;
    }

    public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> pluralAttribute) {
        return this.fetch(pluralAttribute, DEFAULT_JOIN_TYPE);
    }

    public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> pluralAttribute, JoinType jt) {
        if (!this.canBeFetchSource()) {
            throw this.illegalFetch();
        }
        JoinImplementor<Object, Object> fetch = PluralAttribute.CollectionType.COLLECTION.equals((Object)pluralAttribute.getCollectionType()) ? this.constructJoin((CollectionAttribute)pluralAttribute, jt) : (PluralAttribute.CollectionType.LIST.equals((Object)pluralAttribute.getCollectionType()) ? this.constructJoin((ListAttribute)pluralAttribute, jt) : (PluralAttribute.CollectionType.SET.equals((Object)pluralAttribute.getCollectionType()) ? this.constructJoin((SetAttribute)pluralAttribute, jt) : this.constructJoin((MapAttribute)pluralAttribute, jt)));
        this.joinScope.addFetch(fetch);
        return fetch;
    }

    public <X, Y> Fetch<X, Y> fetch(String attributeName) {
        return this.fetch(attributeName, DEFAULT_JOIN_TYPE);
    }

    public <X, Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
        if (!this.canBeFetchSource()) {
            throw this.illegalFetch();
        }
        Attribute attribute = this.locateAttribute(attributeName);
        if (attribute.isCollection()) {
            return this.fetch((PluralAttribute)attribute, jt);
        }
        return this.fetch((SingularAttribute)attribute, jt);
    }

    @Override
    public boolean canBeReplacedByCorrelatedParentInSubQuery() {
        if (this.correlationParent == null) {
            return false;
        }
        if (this.joins == null) {
            return true;
        }
        for (Join<X, ?> join : this.joins) {
            if (join.getJoinType() == JoinType.LEFT) {
                return false;
            }
            assert (join.getJoinType() == JoinType.INNER);
        }
        return true;
    }

    protected class CorrelationJoinScope
    implements JoinScope<X> {
        protected CorrelationJoinScope() {
        }

        @Override
        public void addJoin(Join<X, ?> join) {
            if (AbstractFromImpl.this.joins == null) {
                AbstractFromImpl.this.joins = new LinkedHashSet();
            }
            AbstractFromImpl.this.joins.add(join);
        }

        @Override
        public void addFetch(Fetch<X, ?> fetch) {
            throw new UnsupportedOperationException("Cannot define fetch from a subquery correlation");
        }
    }

    protected class BasicJoinScope
    implements JoinScope<X> {
        protected BasicJoinScope() {
        }

        @Override
        public void addJoin(Join<X, ?> join) {
            if (AbstractFromImpl.this.joins == null) {
                AbstractFromImpl.this.joins = new LinkedHashSet();
            }
            AbstractFromImpl.this.joins.add(join);
        }

        @Override
        public void addFetch(Fetch<X, ?> fetch) {
            if (AbstractFromImpl.this.fetches == null) {
                AbstractFromImpl.this.fetches = new LinkedHashSet();
            }
            AbstractFromImpl.this.fetches.add(fetch);
        }
    }

    public static interface JoinScope<X>
    extends Serializable {
        public void addJoin(Join<X, ?> var1);

        public void addFetch(Fetch<X, ?> var1);
    }
}

