/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Order
 *  javax.persistence.criteria.ParameterExpression
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Selection
 *  javax.persistence.criteria.Subquery
 *  javax.persistence.metamodel.EntityType
 *  org.jboss.logging.Logger
 */
package org.hibernate.query.criteria.internal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;
import org.hibernate.query.criteria.internal.AbstractNode;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.hibernate.query.criteria.internal.QueryStructure;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.SelectionImplementor;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.query.criteria.internal.compile.CompilableCriteria;
import org.hibernate.query.criteria.internal.compile.CriteriaInterpretation;
import org.hibernate.query.criteria.internal.compile.CriteriaQueryTypeQueryAdapter;
import org.hibernate.query.criteria.internal.compile.ImplicitParameterBinding;
import org.hibernate.query.criteria.internal.compile.InterpretedParameterMetadata;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.sql.ast.Clause;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class CriteriaQueryImpl<T>
extends AbstractNode
implements CriteriaQuery<T>,
CompilableCriteria,
Serializable {
    private static final Logger log = Logger.getLogger(CriteriaQueryImpl.class);
    private final Class<T> returnType;
    private final QueryStructure<T> queryStructure;
    private List<Order> orderSpecs = Collections.emptyList();

    public CriteriaQueryImpl(CriteriaBuilderImpl criteriaBuilder, Class<T> returnType) {
        super(criteriaBuilder);
        this.returnType = returnType;
        this.queryStructure = new QueryStructure(this, criteriaBuilder);
    }

    protected QueryStructure<T> getQueryStructure() {
        return this.queryStructure;
    }

    public Class<T> getResultType() {
        return this.returnType;
    }

    public CriteriaQuery<T> distinct(boolean applyDistinction) {
        this.queryStructure.setDistinct(applyDistinction);
        return this;
    }

    public boolean isDistinct() {
        return this.queryStructure.isDistinct();
    }

    public Selection<T> getSelection() {
        return this.queryStructure.getSelection();
    }

    public void applySelection(Selection<? extends T> selection) {
        this.queryStructure.setSelection(selection);
    }

    public CriteriaQuery<T> select(Selection<? extends T> selection) {
        this.applySelection(selection);
        return this;
    }

    public CriteriaQuery<T> multiselect(Selection<?> ... selections) {
        return this.multiselect(Arrays.asList(selections));
    }

    public CriteriaQuery<T> multiselect(List<Selection<?>> selections) {
        Object selection;
        if (Tuple.class.isAssignableFrom(this.getResultType())) {
            selection = this.criteriaBuilder().tuple(selections);
        } else if (this.getResultType().isArray()) {
            selection = this.criteriaBuilder().array(this.getResultType(), selections);
        } else if (Object.class.equals(this.getResultType())) {
            switch (selections.size()) {
                case 0: {
                    throw new IllegalArgumentException("empty selections passed to criteria query typed as Object");
                }
                case 1: {
                    selection = selections.get(0);
                    break;
                }
                default: {
                    selection = this.criteriaBuilder().array(selections);
                    break;
                }
            }
        } else {
            selection = this.criteriaBuilder().construct(this.getResultType(), selections);
        }
        this.applySelection((Selection<? extends T>)selection);
        return this;
    }

    public Set<Root<?>> getRoots() {
        return this.queryStructure.getRoots();
    }

    public <X> Root<X> from(EntityType<X> entityType) {
        return this.queryStructure.from(entityType);
    }

    public <X> Root<X> from(Class<X> entityClass) {
        return this.queryStructure.from(entityClass);
    }

    public Predicate getRestriction() {
        return this.queryStructure.getRestriction();
    }

    public CriteriaQuery<T> where(Expression<Boolean> expression) {
        this.queryStructure.setRestriction(this.criteriaBuilder().wrap(expression));
        return this;
    }

    public CriteriaQuery<T> where(Predicate ... predicates) {
        this.queryStructure.setRestriction(this.criteriaBuilder().and(predicates));
        return this;
    }

    public List<Expression<?>> getGroupList() {
        return this.queryStructure.getGroupings();
    }

    public CriteriaQuery<T> groupBy(Expression<?> ... groupings) {
        this.queryStructure.setGroupings(groupings);
        return this;
    }

    public CriteriaQuery<T> groupBy(List<Expression<?>> groupings) {
        this.queryStructure.setGroupings(groupings);
        return this;
    }

    public Predicate getGroupRestriction() {
        return this.queryStructure.getHaving();
    }

    public CriteriaQuery<T> having(Expression<Boolean> expression) {
        this.queryStructure.setHaving(this.criteriaBuilder().wrap(expression));
        return this;
    }

    public CriteriaQuery<T> having(Predicate ... predicates) {
        this.queryStructure.setHaving(this.criteriaBuilder().and(predicates));
        return this;
    }

    public List<Order> getOrderList() {
        return this.orderSpecs;
    }

    public CriteriaQuery<T> orderBy(Order ... orders) {
        this.orderSpecs = orders != null && orders.length > 0 ? Arrays.asList(orders) : Collections.emptyList();
        return this;
    }

    public CriteriaQuery<T> orderBy(List<Order> orders) {
        this.orderSpecs = orders;
        return this;
    }

    public Set<ParameterExpression<?>> getParameters() {
        return this.queryStructure.getParameters();
    }

    public <U> Subquery<U> subquery(Class<U> subqueryType) {
        return this.queryStructure.subquery(subqueryType);
    }

    @Override
    public void validate() {
        if (this.getRoots().isEmpty()) {
            throw new IllegalStateException("No criteria query roots were specified");
        }
        if (this.getSelection() == null && !this.hasImplicitSelection()) {
            throw new IllegalStateException("No explicit selection and an implicit one could not be determined");
        }
    }

    private boolean hasImplicitSelection() {
        if (this.getRoots().size() != 1) {
            return false;
        }
        Root<?> root = this.getRoots().iterator().next();
        Class javaType = root.getModel().getJavaType();
        return javaType == null || javaType == this.returnType;
    }

    @Override
    public CriteriaInterpretation interpret(RenderingContext renderingContext) {
        StringBuilder jpaqlBuffer = new StringBuilder();
        this.queryStructure.render(jpaqlBuffer, renderingContext);
        this.renderOrderByClause(renderingContext, jpaqlBuffer);
        final String jpaqlString = jpaqlBuffer.toString();
        log.debugf("Rendered criteria query -> %s", (Object)jpaqlString);
        return new CriteriaInterpretation(){

            @Override
            public QueryImplementor buildCompiledQuery(SharedSessionContractImplementor entityManager, InterpretedParameterMetadata parameterMetadata) {
                final Map<String, Class> implicitParameterTypes = this.extractTypeMap(parameterMetadata.implicitParameterBindings());
                QueryImplementor jpaqlQuery = entityManager.createQuery(jpaqlString, CriteriaQueryImpl.this.getResultType(), CriteriaQueryImpl.this.getSelection(), new HibernateEntityManagerImplementor.QueryOptions(){

                    @Override
                    public List<ValueHandlerFactory.ValueHandler> getValueHandlers() {
                        SelectionImplementor selection = (SelectionImplementor)CriteriaQueryImpl.this.queryStructure.getSelection();
                        return selection == null ? null : selection.getValueHandlers();
                    }

                    @Override
                    public Map<String, Class> getNamedParameterExplicitTypes() {
                        return implicitParameterTypes;
                    }

                    @Override
                    public HibernateEntityManagerImplementor.QueryOptions.ResultMetadataValidator getResultMetadataValidator() {
                        return new HibernateEntityManagerImplementor.QueryOptions.ResultMetadataValidator(){

                            @Override
                            public void validate(Type[] returnTypes) {
                                SelectionImplementor selection = (SelectionImplementor)CriteriaQueryImpl.this.queryStructure.getSelection();
                                if (selection != null) {
                                    if (selection.isCompoundSelection()) {
                                        if (returnTypes.length != selection.getCompoundSelectionItems().size()) {
                                            throw new IllegalStateException("Number of return values [" + returnTypes.length + "] did not match expected [" + selection.getCompoundSelectionItems().size() + "]");
                                        }
                                    } else if (returnTypes.length > 1) {
                                        throw new IllegalStateException("Number of return values [" + returnTypes.length + "] did not match expected [1]");
                                    }
                                }
                            }
                        };
                    }
                });
                for (ImplicitParameterBinding implicitParameterBinding : parameterMetadata.implicitParameterBindings()) {
                    implicitParameterBinding.bind(jpaqlQuery);
                }
                return new CriteriaQueryTypeQueryAdapter(entityManager, jpaqlQuery, parameterMetadata.explicitParameterInfoMap());
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void renderOrderByClause(RenderingContext renderingContext, StringBuilder jpaqlBuffer) {
        if (this.getOrderList().isEmpty()) {
            return;
        }
        renderingContext.getClauseStack().push(Clause.ORDER);
        try {
            jpaqlBuffer.append(" order by ");
            String sep = "";
            for (Order orderSpec : this.getOrderList()) {
                Boolean nullsFirst;
                jpaqlBuffer.append(sep).append(((Renderable)orderSpec.getExpression()).render(renderingContext)).append(orderSpec.isAscending() ? " asc" : " desc");
                if (orderSpec instanceof OrderImpl && (nullsFirst = ((OrderImpl)orderSpec).getNullsFirst()) != null) {
                    if (nullsFirst.booleanValue()) {
                        jpaqlBuffer.append(" nulls first");
                    } else {
                        jpaqlBuffer.append(" nulls last");
                    }
                }
                sep = ", ";
            }
        }
        finally {
            renderingContext.getClauseStack().pop();
        }
    }
}

