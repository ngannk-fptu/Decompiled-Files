/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.NumericExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;

public class ASTUnsignedShiftRight
extends NumericExpression {
    public ASTUnsignedShiftRight(int id) {
        super(id);
    }

    public ASTUnsignedShiftRight(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object v1 = this._children[0].getValue(context, source);
        Object v2 = this._children[1].getValue(context, source);
        return OgnlOps.unsignedShiftRight(v1, v2);
    }

    @Override
    public String getExpressionOperator(int index) {
        return ">>>";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String result = "";
        try {
            String child1 = OgnlRuntime.getChildSource(context, target, this._children[0]);
            child1 = this.coerceToNumeric(child1, context, this._children[0]);
            String child2 = OgnlRuntime.getChildSource(context, target, this._children[1]);
            child2 = this.coerceToNumeric(child2, context, this._children[1]);
            Object v1 = this._children[0].getValue(context, target);
            int type = OgnlOps.getNumericType(v1);
            if (type <= 4) {
                child1 = "(int)" + child1;
                child2 = "(int)" + child2;
            }
            result = child1 + " >>> " + child2;
            context.setCurrentType(Integer.TYPE);
            context.setCurrentObject(this.getValueBody(context, target));
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }
}

