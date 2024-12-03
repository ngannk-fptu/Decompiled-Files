/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.servlet.jsp.el.ELException
 *  javax.servlet.jsp.el.VariableResolver
 */
package org.apache.sling.scripting.jsp.jasper.el;

import javax.el.ELContext;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.VariableResolver;

public final class VariableResolverImpl
implements VariableResolver {
    private final ELContext ctx;

    public VariableResolverImpl(ELContext ctx) {
        this.ctx = ctx;
    }

    public Object resolveVariable(String pName) throws ELException {
        return this.ctx.getELResolver().getValue(this.ctx, null, (Object)pName);
    }
}

