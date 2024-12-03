/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression.function;

import java.io.Serializable;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;

public class LocateFunction
extends BasicFunctionExpression<Integer>
implements Serializable {
    public static final String NAME = "locate";
    private final Expression<String> pattern;
    private final Expression<String> string;
    private final Expression<Integer> start;

    public LocateFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> pattern, Expression<String> string, Expression<Integer> start) {
        super(criteriaBuilder, Integer.class, NAME);
        this.pattern = pattern;
        this.string = string;
        this.start = start;
    }

    public LocateFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> pattern, Expression<String> string) {
        this(criteriaBuilder, pattern, string, null);
    }

    public LocateFunction(CriteriaBuilderImpl criteriaBuilder, String pattern, Expression<String> string) {
        this(criteriaBuilder, new LiteralExpression<String>(criteriaBuilder, pattern), string, null);
    }

    public LocateFunction(CriteriaBuilderImpl criteriaBuilder, String pattern, Expression<String> string, int start) {
        this(criteriaBuilder, new LiteralExpression<String>(criteriaBuilder, pattern), string, new LiteralExpression<Integer>(criteriaBuilder, start));
    }

    public Expression<String> getPattern() {
        return this.pattern;
    }

    public Expression<Integer> getStart() {
        return this.start;
    }

    public Expression<String> getString() {
        return this.string;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getPattern(), registry);
        ParameterContainer.Helper.possibleParameter(this.getStart(), registry);
        ParameterContainer.Helper.possibleParameter(this.getString(), registry);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String render(RenderingContext renderingContext) {
        renderingContext.getFunctionStack().push(this);
        try {
            StringBuilder buffer = new StringBuilder();
            buffer.append("locate(").append(((Renderable)this.getPattern()).render(renderingContext)).append(',').append(((Renderable)this.getString()).render(renderingContext));
            if (this.getStart() != null) {
                buffer.append(',').append(((Renderable)this.getStart()).render(renderingContext));
            }
            buffer.append(')');
            String string = buffer.toString();
            return string;
        }
        finally {
            renderingContext.getFunctionStack().pop();
        }
    }
}

