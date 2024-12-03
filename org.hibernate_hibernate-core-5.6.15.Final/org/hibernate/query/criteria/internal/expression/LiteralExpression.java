/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import org.hibernate.query.criteria.LiteralHandlingMode;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;

public class LiteralExpression<T>
extends ExpressionImpl<T>
implements Serializable {
    private Object literal;

    public LiteralExpression(CriteriaBuilderImpl criteriaBuilder, T literal) {
        this(criteriaBuilder, LiteralExpression.determineClass(literal), literal);
    }

    private static Class determineClass(Object literal) {
        return literal == null ? null : literal.getClass();
    }

    public LiteralExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> type, T literal) {
        super(criteriaBuilder, type);
        this.literal = literal;
    }

    public T getLiteral() {
        return (T)this.literal;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(RenderingContext renderingContext) {
        if (this.literal instanceof Enum) {
            return this.normalRender(renderingContext, LiteralHandlingMode.BIND);
        }
        switch (renderingContext.getClauseStack().getCurrent()) {
            case SELECT: {
                return this.renderProjection(renderingContext);
            }
            case GROUP: {
                return this.renderProjection(renderingContext);
            }
        }
        return this.normalRender(renderingContext, renderingContext.getCriteriaLiteralHandlingMode());
    }

    private String normalRender(RenderingContext renderingContext, LiteralHandlingMode literalHandlingMode) {
        switch (literalHandlingMode) {
            case AUTO: {
                if (ValueHandlerFactory.isNumeric(this.literal)) {
                    return ValueHandlerFactory.determineAppropriateHandler(this.literal.getClass()).render(this.literal);
                }
                return this.bindLiteral(renderingContext);
            }
            case BIND: {
                return this.bindLiteral(renderingContext);
            }
            case INLINE: {
                ValueHandlerFactory.ValueHandler<?> valueHandler;
                Object literalValue = this.literal;
                if (String.class.equals(this.literal.getClass())) {
                    literalValue = renderingContext.getDialect().inlineLiteral((String)this.literal);
                }
                if ((valueHandler = ValueHandlerFactory.determineAppropriateHandler(this.literal.getClass())) == null) {
                    return this.bindLiteral(renderingContext);
                }
                return ValueHandlerFactory.determineAppropriateHandler(this.literal.getClass()).render(literalValue);
            }
        }
        throw new IllegalArgumentException("Unexpected LiteralHandlingMode: " + (Object)((Object)literalHandlingMode));
    }

    private String renderProjection(RenderingContext renderingContext) {
        if (ValueHandlerFactory.isCharacter(this.literal)) {
            return renderingContext.getDialect().inlineLiteral(this.literal.toString());
        }
        ValueHandlerFactory.ValueHandler<?> handler = ValueHandlerFactory.determineAppropriateHandler(this.literal.getClass());
        if (handler == null) {
            return this.normalRender(renderingContext, LiteralHandlingMode.BIND);
        }
        return handler.render(this.literal);
    }

    private String bindLiteral(RenderingContext renderingContext) {
        String parameterName = renderingContext.registerLiteralParameterBinding(this.getLiteral(), this.getJavaType());
        return ':' + parameterName;
    }

    @Override
    protected void resetJavaType(Class targetType) {
        super.resetJavaType(targetType);
        ValueHandlerFactory.ValueHandler<Object> valueHandler = this.getValueHandler();
        if (valueHandler == null) {
            valueHandler = ValueHandlerFactory.determineAppropriateHandler(targetType);
            this.forceConversion(valueHandler);
        }
        if (valueHandler != null) {
            this.literal = valueHandler.convert(this.literal);
        }
    }
}

