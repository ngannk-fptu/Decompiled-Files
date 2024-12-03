/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ExpressionFactory
 *  javax.el.ValueExpression
 *  javax.servlet.jsp.el.ELException
 *  javax.servlet.jsp.el.Expression
 *  javax.servlet.jsp.el.VariableResolver
 */
package org.apache.jasper.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.VariableResolver;
import org.apache.jasper.el.ELContextImpl;
import org.apache.jasper.el.ELResolverImpl;

@Deprecated
public final class ExpressionImpl
extends Expression {
    private final ValueExpression ve;
    private final ExpressionFactory factory;

    public ExpressionImpl(ValueExpression ve, ExpressionFactory factory) {
        this.ve = ve;
        this.factory = factory;
    }

    public Object evaluate(VariableResolver vResolver) throws ELException {
        ELContextImpl ctx = new ELContextImpl(new ELResolverImpl(vResolver, this.factory));
        return this.ve.getValue((ELContext)ctx);
    }
}

