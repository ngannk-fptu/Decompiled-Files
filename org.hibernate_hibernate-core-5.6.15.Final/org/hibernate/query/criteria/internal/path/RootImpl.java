/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Root
 *  javax.persistence.metamodel.EntityType
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.FromImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.AbstractFromImpl;

public class RootImpl<X>
extends AbstractFromImpl<X, X>
implements Root<X>,
Serializable {
    private final EntityType<X> entityType;
    private final boolean allowJoins;
    private final Set<TreatedRoot<? extends X>> treats = new LinkedHashSet<TreatedRoot<? extends X>>();

    public RootImpl(CriteriaBuilderImpl criteriaBuilder, EntityType<X> entityType) {
        this(criteriaBuilder, entityType, true);
    }

    public RootImpl(CriteriaBuilderImpl criteriaBuilder, EntityType<X> entityType, boolean allowJoins) {
        super(criteriaBuilder, entityType.getJavaType());
        this.entityType = entityType;
        this.allowJoins = allowJoins;
    }

    public EntityType<X> getEntityType() {
        return this.entityType;
    }

    public EntityType<X> getModel() {
        return this.getEntityType();
    }

    @Override
    protected FromImplementor<X, X> createCorrelationDelegate() {
        return new RootImpl<X>(this.criteriaBuilder(), this.getEntityType());
    }

    public RootImpl<X> correlateTo(CriteriaSubqueryImpl subquery) {
        return (RootImpl)super.correlateTo(subquery);
    }

    @Override
    protected boolean canBeJoinSource() {
        return this.allowJoins;
    }

    @Override
    protected RuntimeException illegalJoin() {
        return this.allowJoins ? super.illegalJoin() : new IllegalArgumentException("UPDATE/DELETE criteria queries cannot define joins");
    }

    @Override
    protected RuntimeException illegalFetch() {
        return this.allowJoins ? super.illegalFetch() : new IllegalArgumentException("UPDATE/DELETE criteria queries cannot define fetches");
    }

    @Override
    public String renderTableExpression(RenderingContext renderingContext) {
        this.prepareAlias(renderingContext);
        return this.getModel().getName() + " as " + this.getAlias();
    }

    @Override
    public String getPathIdentifier() {
        return this.getAlias();
    }

    @Override
    public String render(RenderingContext renderingContext) {
        this.prepareAlias(renderingContext);
        return this.getAlias();
    }

    public Set<TreatedRoot<? extends X>> getTreats() {
        return this.treats;
    }

    @Override
    public <T extends X> RootImpl<T> treatAs(Class<T> treatAsType) {
        TreatedRoot<T> treatedRoot = new TreatedRoot<T>(this, treatAsType);
        this.treats.add(treatedRoot);
        return treatedRoot;
    }

    public static class TreatedRoot<T>
    extends RootImpl<T> {
        private final RootImpl<? super T> original;
        private final Class<T> treatAsType;

        public TreatedRoot(RootImpl<? super T> original, Class<T> treatAsType) {
            super(original.criteriaBuilder(), original.criteriaBuilder().getEntityManagerFactory().getMetamodel().entity(treatAsType));
            this.original = original;
            this.treatAsType = treatAsType;
        }

        @Override
        public String getAlias() {
            return this.original.getAlias();
        }

        @Override
        public void prepareAlias(RenderingContext renderingContext) {
            this.original.prepareAlias(renderingContext);
        }

        @Override
        public String render(RenderingContext renderingContext) {
            this.original.prepareAlias(renderingContext);
            return this.getTreatFragment();
        }

        protected String getTreatFragment() {
            return "treat(" + this.original.getAlias() + " as " + this.treatAsType.getName() + ")";
        }

        @Override
        public String getPathIdentifier() {
            return this.getTreatFragment();
        }

        @Override
        protected PathSource getPathSourceForSubPaths() {
            return this;
        }
    }
}

