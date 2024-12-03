/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 */
package javax.servlet.jsp;

import java.io.Writer;
import java.util.Enumeration;
import javax.el.ELContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

public abstract class JspContext {
    public abstract void setAttribute(String var1, Object var2);

    public abstract void setAttribute(String var1, Object var2, int var3);

    public abstract Object getAttribute(String var1);

    public abstract Object getAttribute(String var1, int var2);

    public abstract Object findAttribute(String var1);

    public abstract void removeAttribute(String var1);

    public abstract void removeAttribute(String var1, int var2);

    public abstract int getAttributesScope(String var1);

    public abstract Enumeration<String> getAttributeNamesInScope(int var1);

    public abstract JspWriter getOut();

    public abstract ExpressionEvaluator getExpressionEvaluator();

    public abstract ELContext getELContext();

    public abstract VariableResolver getVariableResolver();

    public JspWriter pushBody(Writer writer) {
        return null;
    }

    public JspWriter popBody() {
        return null;
    }
}

