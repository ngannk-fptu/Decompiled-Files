/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.NumericExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;

public class ASTBitNegate
extends NumericExpression {
    public ASTBitNegate(int id) {
        super(id);
    }

    public ASTBitNegate(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return OgnlOps.bitNegate(this._children[0].getValue(context, source));
    }

    @Override
    public String toString() {
        return "~" + this._children[0];
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String source = this._children[0].toGetSourceString(context, target);
        if (!ASTBitNegate.class.isInstance(this._children[0])) {
            return "~(" + super.coerceToNumeric(source, context, this._children[0]) + ")";
        }
        return "~(" + source + ")";
    }
}

