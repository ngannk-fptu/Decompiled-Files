/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.ExpressionNode;
import ognl.NodeType;
import ognl.OgnlContext;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.enhance.UnsupportedCompilationException;

public abstract class BooleanExpression
extends ExpressionNode
implements NodeType {
    protected Class _getterClass;

    public BooleanExpression(int id) {
        super(id);
    }

    public BooleanExpression(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public Class getGetterClass() {
        return this._getterClass;
    }

    @Override
    public Class getSetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        try {
            Object value = this.getValueBody(context, target);
            this._getterClass = value != null && Boolean.class.isAssignableFrom(value.getClass()) ? Boolean.TYPE : (value != null ? value.getClass() : Boolean.TYPE);
            String ret = super.toGetSourceString(context, target);
            if ("(false)".equals(ret)) {
                return "false";
            }
            if ("(true)".equals(ret)) {
                return "true";
            }
            return ret;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }
}

