/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.el;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.VariableResolver;

public abstract class Expression {
    public abstract Object evaluate(VariableResolver var1) throws ELException;
}

