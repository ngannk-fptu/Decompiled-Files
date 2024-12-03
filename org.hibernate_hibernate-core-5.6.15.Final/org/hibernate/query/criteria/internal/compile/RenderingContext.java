/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.ParameterExpression
 */
package org.hibernate.query.criteria.internal.compile;

import javax.persistence.criteria.ParameterExpression;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.collections.Stack;
import org.hibernate.query.criteria.LiteralHandlingMode;
import org.hibernate.query.criteria.internal.compile.ExplicitParameterInfo;
import org.hibernate.query.criteria.internal.expression.function.FunctionExpression;
import org.hibernate.sql.ast.Clause;

public interface RenderingContext {
    public String generateAlias();

    public ExplicitParameterInfo registerExplicitParameter(ParameterExpression<?> var1);

    public String registerLiteralParameterBinding(Object var1, Class var2);

    public String getCastType(Class var1);

    public Dialect getDialect();

    default public LiteralHandlingMode getCriteriaLiteralHandlingMode() {
        return LiteralHandlingMode.AUTO;
    }

    public Stack<Clause> getClauseStack();

    public Stack<FunctionExpression> getFunctionStack();
}

