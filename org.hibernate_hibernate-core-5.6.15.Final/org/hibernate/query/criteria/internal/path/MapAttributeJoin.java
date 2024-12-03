/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.JoinType
 *  javax.persistence.criteria.Path
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.metamodel.ManagedType
 *  javax.persistence.metamodel.MapAttribute
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import java.util.Map;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.FromImplementor;
import org.hibernate.query.criteria.internal.MapJoinImplementor;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.MapEntryExpression;
import org.hibernate.query.criteria.internal.path.MapKeyHelpers;
import org.hibernate.query.criteria.internal.path.PluralAttributeJoinSupport;

public class MapAttributeJoin<O, K, V>
extends PluralAttributeJoinSupport<O, Map<K, V>, V>
implements MapJoinImplementor<O, K, V>,
Serializable {
    public MapAttributeJoin(CriteriaBuilderImpl criteriaBuilder, Class<V> javaType, PathSource<O> pathSource, MapAttribute<? super O, K, V> joinAttribute, JoinType joinType) {
        super(criteriaBuilder, javaType, pathSource, joinAttribute, joinType);
    }

    @Override
    public MapAttribute<? super O, K, V> getAttribute() {
        return (MapAttribute)super.getAttribute();
    }

    @Override
    public MapAttribute<? super O, K, V> getModel() {
        return this.getAttribute();
    }

    @Override
    public final MapAttributeJoin<O, K, V> correlateTo(CriteriaSubqueryImpl subquery) {
        return (MapAttributeJoin)super.correlateTo(subquery);
    }

    @Override
    protected FromImplementor<O, V> createCorrelationDelegate() {
        return new MapAttributeJoin(this.criteriaBuilder(), this.getJavaType(), (PathImplementor)this.getParentPath(), this.getAttribute(), this.getJoinType());
    }

    public Path<V> value() {
        return this;
    }

    public Expression<Map.Entry<K, V>> entry() {
        return new MapEntryExpression(this.criteriaBuilder(), Map.Entry.class, this);
    }

    public Path<K> key() {
        MapKeyHelpers.MapKeySource<K, V> mapKeySource = new MapKeyHelpers.MapKeySource<K, V>(this.criteriaBuilder(), this.getAttribute().getJavaType(), this, this.getAttribute());
        MapKeyHelpers.MapKeyAttribute<K> mapKeyAttribute = new MapKeyHelpers.MapKeyAttribute<K>(this.criteriaBuilder(), this.getAttribute());
        return new MapKeyHelpers.MapKeyPath<K>(this.criteriaBuilder(), mapKeySource, mapKeyAttribute);
    }

    @Override
    public MapJoinImplementor<O, K, V> on(Predicate ... restrictions) {
        return (MapJoinImplementor)super.on(restrictions);
    }

    @Override
    public MapJoinImplementor<O, K, V> on(Expression<Boolean> restriction) {
        return (MapJoinImplementor)super.on((Expression)restriction);
    }

    @Override
    public <T extends V> MapAttributeJoin<O, K, T> treatAs(Class<T> treatAsType) {
        return new TreatedMapAttributeJoin(this, treatAsType);
    }

    public static class TreatedMapAttributeJoin<O, K, T>
    extends MapAttributeJoin<O, K, T> {
        private final MapAttributeJoin<O, K, ? super T> original;
        protected final Class<T> treatAsType;

        public TreatedMapAttributeJoin(MapAttributeJoin<O, K, ? super T> original, Class<T> treatAsType) {
            super(original.criteriaBuilder(), treatAsType, original.getPathSource(), original.getAttribute(), original.getJoinType());
            this.original = original;
            this.treatAsType = treatAsType;
        }

        @Override
        public String getAlias() {
            return this.isCorrelated() ? this.getCorrelationParent().getAlias() : super.getAlias();
        }

        @Override
        public void prepareAlias(RenderingContext renderingContext) {
            if (this.getAlias() == null) {
                if (this.isCorrelated()) {
                    this.setAlias(this.getCorrelationParent().getAlias());
                } else {
                    this.setAlias(renderingContext.generateAlias());
                }
            }
        }

        @Override
        protected void setAlias(String alias) {
            super.setAlias(alias);
            ((MapAttributeJoin)this.original).setAlias(alias);
        }

        @Override
        public String render(RenderingContext renderingContext) {
            return "treat(" + this.original.render(renderingContext) + " as " + this.treatAsType.getName() + ")";
        }

        @Override
        protected ManagedType<T> locateManagedType() {
            return this.criteriaBuilder().getEntityManagerFactory().getMetamodel().managedType(this.treatAsType);
        }

        @Override
        public String getPathIdentifier() {
            return "treat(" + this.getAlias() + " as " + this.treatAsType.getName() + ")";
        }

        @Override
        protected PathSource getPathSourceForSubPaths() {
            return this;
        }
    }
}

