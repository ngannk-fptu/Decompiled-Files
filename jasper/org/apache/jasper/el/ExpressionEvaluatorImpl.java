/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.ExpressionFactory
 *  javax.el.ValueExpression
 *  javax.servlet.jsp.el.ELException
 *  javax.servlet.jsp.el.ELParseException
 *  javax.servlet.jsp.el.Expression
 *  javax.servlet.jsp.el.ExpressionEvaluator
 *  javax.servlet.jsp.el.FunctionMapper
 *  javax.servlet.jsp.el.VariableResolver
 */
package org.apache.jasper.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ELParseException;
import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.VariableResolver;
import org.apache.jasper.el.ELContextImpl;
import org.apache.jasper.el.ExpressionImpl;
import org.apache.jasper.el.FunctionMapperImpl;

@Deprecated
public final class ExpressionEvaluatorImpl
extends ExpressionEvaluator {
    private final ExpressionFactory factory;

    public ExpressionEvaluatorImpl(ExpressionFactory factory) {
        this.factory = factory;
    }

    public Expression parseExpression(String expression, Class expectedType, FunctionMapper fMapper) throws ELException {
        try {
            ELContextImpl ctx = new ELContextImpl(ELContextImpl.getDefaultResolver(this.factory));
            if (fMapper != null) {
                ctx.setFunctionMapper(new FunctionMapperImpl(fMapper));
            }
            ValueExpression ve = this.factory.createValueExpression((ELContext)ctx, expression, expectedType);
            return new ExpressionImpl(ve, this.factory);
        }
        catch (javax.el.ELException e) {
            throw new ELParseException(e.getMessage());
        }
    }

    public Object evaluate(String expression, Class expectedType, VariableResolver vResolver, FunctionMapper fMapper) throws ELException {
        return this.parseExpression(expression, expectedType, fMapper).evaluate(vResolver);
    }
}

