/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ValueExpression
 *  javax.servlet.jsp.el.ELException
 *  javax.servlet.jsp.el.Expression
 *  javax.servlet.jsp.el.VariableResolver
 */
package org.apache.sling.scripting.jsp.jasper.el;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.VariableResolver;
import org.apache.sling.scripting.jsp.jasper.el.ELContextImpl;
import org.apache.sling.scripting.jsp.jasper.el.ELResolverImpl;

public final class ExpressionImpl
extends Expression {
    private final ValueExpression ve;

    public ExpressionImpl(ValueExpression ve) {
        this.ve = ve;
    }

    public Object evaluate(VariableResolver vResolver) throws ELException {
        ELContextImpl ctx = new ELContextImpl(new ELResolverImpl(vResolver));
        return this.ve.getValue((ELContext)ctx);
    }
}

