/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.ASTVarRef;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlParser;
import ognl.enhance.UnsupportedCompilationException;

public class ASTThisVarRef
extends ASTVarRef {
    public ASTThisVarRef(int id) {
        super(id);
    }

    public ASTThisVarRef(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return context.getCurrentObject();
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        context.setCurrentObject(value);
    }

    @Override
    public String toString() {
        return "#this";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Unable to compile this references.");
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Unable to compile this references.");
    }
}

