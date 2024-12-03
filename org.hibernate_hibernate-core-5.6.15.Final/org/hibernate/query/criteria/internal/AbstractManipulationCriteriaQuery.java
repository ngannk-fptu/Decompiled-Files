/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CommonAbstractCriteria
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Subquery
 *  javax.persistence.metamodel.EntityType
 */
package org.hibernate.query.criteria.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.query.criteria.internal.compile.CompilableCriteria;
import org.hibernate.query.criteria.internal.compile.CriteriaInterpretation;
import org.hibernate.query.criteria.internal.compile.ImplicitParameterBinding;
import org.hibernate.query.criteria.internal.compile.InterpretedParameterMetadata;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.RootImpl;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.sql.ast.Clause;

public abstract class AbstractManipulationCriteriaQuery<T>
implements CompilableCriteria,
CommonAbstractCriteria {
    private final CriteriaBuilderImpl criteriaBuilder;
    private RootImpl<T> root;
    private Predicate restriction;

    protected AbstractManipulationCriteriaQuery(CriteriaBuilderImpl criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    protected CriteriaBuilderImpl criteriaBuilder() {
        return this.criteriaBuilder;
    }

    public Root from(Class<T> entityClass) {
        EntityType entityType = this.criteriaBuilder.getEntityManagerFactory().getMetamodel().entity(entityClass);
        if (entityType == null) {
            throw new IllegalArgumentException(entityClass + " is not an entity");
        }
        return this.from(entityType);
    }

    public Root<T> from(EntityType<T> entityType) {
        this.root = new RootImpl<T>(this.criteriaBuilder, entityType, false);
        return this.root;
    }

    public Root<T> getRoot() {
        return this.root;
    }

    protected void setRestriction(Expression<Boolean> restriction) {
        this.restriction = this.criteriaBuilder.wrap(restriction);
    }

    public void setRestriction(Predicate ... restrictions) {
        this.restriction = this.criteriaBuilder.and(restrictions);
    }

    public Predicate getRestriction() {
        return this.restriction;
    }

    public <U> Subquery<U> subquery(Class<U> type) {
        return new CriteriaSubqueryImpl<U>(this.criteriaBuilder(), type, this);
    }

    @Override
    public void validate() {
        if (this.root == null) {
            throw new IllegalStateException("UPDATE/DELETE criteria must name root entity");
        }
    }

    @Override
    public CriteriaInterpretation interpret(RenderingContext renderingContext) {
        final String jpaqlString = this.renderQuery(renderingContext);
        return new CriteriaInterpretation(){

            @Override
            public QueryImplementor buildCompiledQuery(SharedSessionContractImplementor entityManager, InterpretedParameterMetadata interpretedParameterMetadata) {
                final Map<String, Class> implicitParameterTypes = this.extractTypeMap(interpretedParameterMetadata.implicitParameterBindings());
                QueryImplementor query = entityManager.createQuery(jpaqlString, null, null, new HibernateEntityManagerImplementor.QueryOptions(){

                    @Override
                    public List<ValueHandlerFactory.ValueHandler> getValueHandlers() {
                        return null;
                    }

                    @Override
                    public Map<String, Class> getNamedParameterExplicitTypes() {
                        return implicitParameterTypes;
                    }

                    @Override
                    public HibernateEntityManagerImplementor.QueryOptions.ResultMetadataValidator getResultMetadataValidator() {
                        return null;
                    }
                });
                for (ImplicitParameterBinding implicitParameterBinding : interpretedParameterMetadata.implicitParameterBindings()) {
                    implicitParameterBinding.bind(query);
                }
                return query;
            }

            private Map<String, Class> extractTypeMap(List<ImplicitParameterBinding> implicitParameterBindings) {
                HashMap<String, Class> map = new HashMap<String, Class>();
                for (ImplicitParameterBinding implicitParameter : implicitParameterBindings) {
                    map.put(implicitParameter.getParameterName(), implicitParameter.getJavaType());
                }
                return map;
            }
        };
    }

    protected abstract String renderQuery(RenderingContext var1);

    protected void renderRoot(StringBuilder jpaql, RenderingContext renderingContext) {
        jpaql.append(this.root.renderTableExpression(renderingContext));
    }

    protected void renderRestrictions(StringBuilder jpaql, RenderingContext renderingContext) {
        if (this.getRestriction() == null) {
            return;
        }
        renderingContext.getClauseStack().push(Clause.WHERE);
        try {
            jpaql.append(" where ").append(((Renderable)this.getRestriction()).render(renderingContext));
        }
        finally {
            renderingContext.getClauseStack().pop();
        }
    }
}

