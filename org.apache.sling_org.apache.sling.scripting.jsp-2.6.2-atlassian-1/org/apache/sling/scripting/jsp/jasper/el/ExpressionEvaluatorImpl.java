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
package org.apache.sling.scripting.jsp.jasper.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ELParseException;
import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.VariableResolver;
import org.apache.sling.scripting.jsp.jasper.el.ELContextImpl;
import org.apache.sling.scripting.jsp.jasper.el.ELResolverImpl;
import org.apache.sling.scripting.jsp.jasper.el.ExpressionImpl;
import org.apache.sling.scripting.jsp.jasper.el.FunctionMapperImpl;

public final class ExpressionEvaluatorImpl
extends ExpressionEvaluator {
    private final ExpressionFactory factory;

    public ExpressionEvaluatorImpl(ExpressionFactory factory) {
        this.factory = factory;
    }

    public Expression parseExpression(String expression, Class expectedType, FunctionMapper fMapper) throws ELException {
        try {
            ELContextImpl ctx = new ELContextImpl(ELResolverImpl.DefaultResolver);
            if (fMapper != null) {
                ctx.setFunctionMapper(new FunctionMapperImpl(fMapper));
            }
            ValueExpression ve = this.factory.createValueExpression((ELContext)ctx, expression, expectedType);
            return new ExpressionImpl(ve);
        }
        catch (javax.el.ELException e) {
            throw new ELParseException(e.getMessage());
        }
    }

    public Object evaluate(String expression, Class expectedType, VariableResolver vResolver, FunctionMapper fMapper) throws ELException {
        return this.parseExpression(expression, expectedType, fMapper).evaluate(vResolver);
    }
}

