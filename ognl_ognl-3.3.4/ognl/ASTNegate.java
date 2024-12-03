/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.NumericExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.SimpleNode;

public class ASTNegate
extends NumericExpression {
    public ASTNegate(int id) {
        super(id);
    }

    public ASTNegate(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return OgnlOps.negate(this._children[0].getValue(context, source));
    }

    @Override
    public String toString() {
        return "-" + this._children[0];
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String source = this._children[0].toGetSourceString(context, target);
        if (!ASTNegate.class.isInstance(this._children[0])) {
            return "-" + source;
        }
        return "-(" + source + ")";
    }

    @Override
    public boolean isOperation(OgnlContext context) throws OgnlException {
        if (this._children.length == 1) {
            SimpleNode child = (SimpleNode)this._children[0];
            return child.isOperation(context) || !child.isConstant(context);
        }
        return super.isOperation(context);
    }
}

