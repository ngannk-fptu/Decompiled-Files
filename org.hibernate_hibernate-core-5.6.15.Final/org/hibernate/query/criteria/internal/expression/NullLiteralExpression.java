/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.sql.ast.Clause;

public class NullLiteralExpression<T>
extends ExpressionImpl<T>
implements Serializable {
    public NullLiteralExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> type) {
        super(criteriaBuilder, type);
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(RenderingContext renderingContext) {
        if (renderingContext.getClauseStack().getCurrent() == Clause.SELECT) {
            return "cast( \tnull  as " + renderingContext.getCastType(this.getJavaType()) + ')';
        }
        return "null";
    }
}

