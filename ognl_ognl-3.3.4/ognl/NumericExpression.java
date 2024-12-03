/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.ASTConst;
import ognl.ExpressionNode;
import ognl.Node;
import ognl.NodeType;
import ognl.OgnlContext;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.enhance.ExpressionCompiler;

public abstract class NumericExpression
extends ExpressionNode
implements NodeType {
    protected Class _getterClass;

    public NumericExpression(int id) {
        super(id);
    }

    public NumericExpression(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public Class getGetterClass() {
        if (this._getterClass != null) {
            return this._getterClass;
        }
        return Double.TYPE;
    }

    @Override
    public Class getSetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        Object value = null;
        String result = "";
        try {
            value = this.getValueBody(context, target);
            if (value != null) {
                this._getterClass = value.getClass();
            }
            for (int i = 0; i < this._children.length; ++i) {
                if (i > 0) {
                    result = result + " " + this.getExpressionOperator(i) + " ";
                }
                String str = OgnlRuntime.getChildSource(context, target, this._children[i]);
                result = result + this.coerceToNumeric(str, context, this._children[i]);
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }

    public String coerceToNumeric(String source, OgnlContext context, Node child) {
        String ret = source;
        Object value = context.getCurrentObject();
        if (ASTConst.class.isInstance(child) && value != null) {
            return value.toString();
        }
        if (context.getCurrentType() != null && !context.getCurrentType().isPrimitive() && context.getCurrentObject() != null && Number.class.isInstance(context.getCurrentObject())) {
            ret = "((" + ExpressionCompiler.getCastString(context.getCurrentObject().getClass()) + ")" + ret + ")";
            ret = ret + "." + OgnlRuntime.getNumericValueGetter(context.getCurrentObject().getClass());
        } else if (context.getCurrentType() != null && context.getCurrentType().isPrimitive() && (ASTConst.class.isInstance(child) || NumericExpression.class.isInstance(child))) {
            ret = ret + OgnlRuntime.getNumericLiteral(context.getCurrentType());
        } else if (context.getCurrentType() != null && String.class.isAssignableFrom(context.getCurrentType())) {
            ret = "Double.parseDouble(" + ret + ")";
            context.setCurrentType(Double.TYPE);
        }
        if (NumericExpression.class.isInstance(child)) {
            ret = "(" + ret + ")";
        }
        return ret;
    }
}

