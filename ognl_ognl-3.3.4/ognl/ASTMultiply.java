/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.NumericExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;

public class ASTMultiply
extends NumericExpression {
    public ASTMultiply(int id) {
        super(id);
    }

    public ASTMultiply(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public void jjtClose() {
        this.flattenTree();
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = this._children[0].getValue(context, source);
        for (int i = 1; i < this._children.length; ++i) {
            result = OgnlOps.multiply(result, this._children[i].getValue(context, source));
        }
        return result;
    }

    @Override
    public String getExpressionOperator(int index) {
        return "*";
    }
}

