/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContextListener
 *  javax.el.ELResolver
 *  javax.el.ExpressionFactory
 */
package javax.servlet.jsp;

import javax.el.ELContextListener;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;

public interface JspApplicationContext {
    public void addELContextListener(ELContextListener var1);

    public void addELResolver(ELResolver var1);

    public ExpressionFactory getExpressionFactory();
}

