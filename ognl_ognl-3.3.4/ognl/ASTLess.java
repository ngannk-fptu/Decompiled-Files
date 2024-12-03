/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.ComparisonExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;

class ASTLess
extends ComparisonExpression {
    public ASTLess(int id) {
        super(id);
    }

    public ASTLess(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object v2;
        Object v1 = this._children[0].getValue(context, source);
        return OgnlOps.less(v1, v2 = this._children[1].getValue(context, source)) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public String getExpressionOperator(int index) {
        return "<";
    }

    @Override
    public String getComparisonFunction() {
        return "ognl.OgnlOps.less";
    }
}

