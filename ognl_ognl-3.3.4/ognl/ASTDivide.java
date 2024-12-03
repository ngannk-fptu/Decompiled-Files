/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.NumericExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;

public class ASTDivide
extends NumericExpression {
    public ASTDivide(int id) {
        super(id);
    }

    public ASTDivide(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object v1 = this._children[0].getValue(context, source);
        Object v2 = this._children[1].getValue(context, source);
        return OgnlOps.divide(v1, v2);
    }

    @Override
    public String getExpressionOperator(int index) {
        return "/";
    }
}

