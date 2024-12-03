/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaUpdate
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Path
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.metamodel.SingularAttribute
 */
package org.hibernate.query.criteria.internal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;
import org.hibernate.query.criteria.internal.AbstractManipulationCriteriaQuery;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ExpressionImplementor;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;
import org.hibernate.sql.ast.Clause;

public class CriteriaUpdateImpl<T>
extends AbstractManipulationCriteriaQuery<T>
implements CriteriaUpdate<T> {
    private List<Assignment> assignments = new ArrayList<Assignment>();

    public CriteriaUpdateImpl(CriteriaBuilderImpl criteriaBuilder) {
        super(criteriaBuilder);
    }

    public <Y, X extends Y> CriteriaUpdate<T> set(SingularAttribute<? super T, Y> singularAttribute, X value) {
        Path attributePath = this.getRoot().get(singularAttribute);
        Object valueExpression = value == null ? this.criteriaBuilder().nullLiteral(attributePath.getJavaType()) : this.criteriaBuilder().literal(value);
        this.addAssignment((Path<Y>)attributePath, (Expression<? extends Y>)valueExpression);
        return this;
    }

    public <Y> CriteriaUpdate<T> set(SingularAttribute<? super T, Y> singularAttribute, Expression<? extends Y> value) {
        this.addAssignment(this.getRoot().get(singularAttribute), value);
        return this;
    }

    public <Y, X extends Y> CriteriaUpdate<T> set(Path<Y> attributePath, X value) {
        Object valueExpression = value == null ? this.criteriaBuilder().nullLiteral(attributePath.getJavaType()) : this.criteriaBuilder().literal(value);
        this.addAssignment(attributePath, (Expression<? extends Y>)valueExpression);
        return this;
    }

    public <Y> CriteriaUpdate<T> set(Path<Y> attributePath, Expression<? extends Y> value) {
        this.addAssignment(attributePath, value);
        return this;
    }

    public CriteriaUpdate<T> set(String attributeName, Object value) {
        Path attributePath = this.getRoot().get(attributeName);
        Object valueExpression = value instanceof Expression ? (Expression<Object>)value : (value == null ? this.criteriaBuilder().nullLiteral(attributePath.getJavaType()) : this.criteriaBuilder().literal(value));
        this.addAssignment((Path)attributePath, (Expression)valueExpression);
        return this;
    }

    protected <Y> void addAssignment(Path<Y> attributePath, Expression<? extends Y> value) {
        if (!PathImplementor.class.isInstance(attributePath)) {
            throw new IllegalArgumentException("Unexpected path implementation type : " + attributePath.getClass().getName());
        }
        if (!SingularAttributePath.class.isInstance(attributePath)) {
            throw new IllegalArgumentException("Attribute path for assignment must represent a singular attribute [" + ((PathImplementor)attributePath).getPathIdentifier() + "]");
        }
        if (value == null) {
            throw new IllegalArgumentException("Assignment value expression cannot be null. Did you mean to pass null as a literal?");
        }
        this.assignments.add(new Assignment((SingularAttributePath)attributePath, value));
    }

    public CriteriaUpdate<T> where(Expression<Boolean> restriction) {
        this.setRestriction(restriction);
        return this;
    }

    public CriteriaUpdate<T> where(Predicate ... restrictions) {
        this.setRestriction(restrictions);
        return this;
    }

    @Override
    public void validate() {
        super.validate();
        if (this.assignments.isEmpty()) {
            throw new IllegalStateException("No assignments specified as part of UPDATE criteria");
        }
    }

    @Override
    protected String renderQuery(RenderingContext renderingContext) {
        StringBuilder jpaql = new StringBuilder("update ");
        this.renderRoot(jpaql, renderingContext);
        this.renderAssignments(jpaql, renderingContext);
        this.renderRestrictions(jpaql, renderingContext);
        return jpaql.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderAssignments(StringBuilder jpaql, RenderingContext renderingContext) {
        renderingContext.getClauseStack().push(Clause.UPDATE);
        try {
            jpaql.append(" set ");
            boolean first = true;
            for (Assignment assignment : this.assignments) {
                if (!first) {
                    jpaql.append(", ");
                }
                jpaql.append(assignment.attributePath.render(renderingContext)).append(" = ").append(assignment.value.render(renderingContext));
                first = false;
            }
        }
        finally {
            renderingContext.getClauseStack().pop();
        }
    }

    private static class Assignment<A> {
        private final SingularAttributePath<A> attributePath;
        private final ExpressionImplementor<? extends A> value;

        private Assignment(SingularAttributePath<A> attributePath, Expression<? extends A> value) {
            this.attributePath = attributePath;
            this.value = (ExpressionImplementor)value;
        }
    }
}

