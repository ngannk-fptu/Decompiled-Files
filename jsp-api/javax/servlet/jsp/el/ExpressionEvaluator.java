/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.el;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.VariableResolver;

public abstract class ExpressionEvaluator {
    public abstract Expression parseExpression(String var1, Class var2, FunctionMapper var3) throws ELException;

    public abstract Object evaluate(String var1, Class var2, VariableResolver var3, FunctionMapper var4) throws ELException;
}

