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

public class SubstringFunction
extends BasicFunctionExpression<String>
implements Serializable {
    public static final String NAME = "substring";
    private final Expression<String> value;
    private final Expression<Integer> start;
    private final Expression<Integer> length;

    public SubstringFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> value, Expression<Integer> start, Expression<Integer> length) {
        super(criteriaBuilder, String.class, NAME);
        this.value = value;
        this.start = start;
        this.length = length;
    }

    public SubstringFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> value, Expression<Integer> start) {
        this(criteriaBuilder, value, start, (Expression<Integer>)((Expression)null));
    }

    public SubstringFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> value, int start) {
        this(criteriaBuilder, value, new LiteralExpression<Integer>(criteriaBuilder, start));
    }

    public SubstringFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> value, int start, int length) {
        this(criteriaBuilder, value, new LiteralExpression<Integer>(criteriaBuilder, start), new LiteralExpression<Integer>(criteriaBuilder, length));
    }

    public Expression<Integer> getLength() {
        return this.length;
    }

    public Expression<Integer> getStart() {
        return this.start;
    }

    public Expression<String> getValue() {
        return this.value;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getLength(), registry);
        ParameterContainer.Helper.possibleParameter(this.getStart(), registry);
        ParameterContainer.Helper.possibleParameter(this.getValue(), registry);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String render(RenderingContext renderingContext) {
        renderingContext.getFunctionStack().push(this);
        try {
            StringBuilder buffer = new StringBuilder();
            buffer.append("substring(").append(((Renderable)this.getValue()).render(renderingContext)).append(',').append(((Renderable)this.getStart()).render(renderingContext));
            if (this.getLength() != null) {
                buffer.append(',').append(((Renderable)this.getLength()).render(renderingContext));
            }
            String string = buffer.append(')').toString();
            return string;
        }
        finally {
            renderingContext.getFunctionStack().pop();
        }
    }
}

