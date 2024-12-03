/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.ComparisonExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;

public class ASTGreaterEq
extends ComparisonExpression {
    public ASTGreaterEq(int id) {
        super(id);
    }

    public ASTGreaterEq(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object v2;
        Object v1 = this._children[0].getValue(context, source);
        return OgnlOps.less(v1, v2 = this._children[1].getValue(context, source)) ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public String getExpressionOperator(int index) {
        return ">=";
    }

    @Override
    public String getComparisonFunction() {
        return "!ognl.OgnlOps.less";
    }
}

