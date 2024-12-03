/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.BooleanExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;

public class ASTNot
extends BooleanExpression {
    public ASTNot(int id) {
        super(id);
    }

    public ASTNot(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return OgnlOps.booleanValue(this._children[0].getValue(context, source)) ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public String getExpressionOperator(int index) {
        return "!";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        try {
            String srcString = super.toGetSourceString(context, target);
            if (srcString == null || srcString.trim().length() < 1) {
                srcString = "null";
            }
            context.setCurrentType(Boolean.TYPE);
            return "(! ognl.OgnlOps.booleanValue(" + srcString + ") )";
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }
}

