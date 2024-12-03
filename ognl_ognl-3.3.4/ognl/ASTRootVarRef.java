/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.ASTVarRef;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlParser;
import ognl.enhance.ExpressionCompiler;

public class ASTRootVarRef
extends ASTVarRef {
    public ASTRootVarRef(int id) {
        super(id);
    }

    public ASTRootVarRef(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return context.getRoot();
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        context.setRoot(value);
    }

    @Override
    public String toString() {
        return "#root";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (target != null) {
            this._getterClass = target.getClass();
        }
        if (this._getterClass != null) {
            context.setCurrentType(this._getterClass);
        }
        if (this._parent == null || this._getterClass != null && this._getterClass.isArray()) {
            return "";
        }
        return ExpressionCompiler.getRootExpression(this, target, context);
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        if (this._parent == null || this._getterClass != null && this._getterClass.isArray()) {
            return "";
        }
        return "$3";
    }
}

