/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.BooleanExpression;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.enhance.ExpressionCompiler;
import ognl.enhance.UnsupportedCompilationException;

public class ASTOr
extends BooleanExpression {
    public ASTOr(int id) {
        super(id);
    }

    public ASTOr(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public void jjtClose() {
        this.flattenTree();
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = null;
        int last = this._children.length - 1;
        for (int i = 0; i <= last; ++i) {
            result = this._children[i].getValue(context, source);
            if (i != last && OgnlOps.booleanValue(result)) break;
        }
        return result;
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        int last = this._children.length - 1;
        for (int i = 0; i < last; ++i) {
            Object v = this._children[i].getValue(context, target);
            if (!OgnlOps.booleanValue(v)) continue;
            return;
        }
        this._children[last].setValue(context, target, value);
    }

    @Override
    public String getExpressionOperator(int index) {
        return "||";
    }

    @Override
    public Class getGetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (this._children.length != 2) {
            throw new UnsupportedCompilationException("Can only compile boolean expressions with two children.");
        }
        String result = "(";
        try {
            String first = OgnlRuntime.getChildSource(context, target, this._children[0]);
            if (!OgnlRuntime.isBoolean(first)) {
                first = OgnlRuntime.getCompiler().createLocalReference(context, first, context.getCurrentType());
            }
            Class firstType = context.getCurrentType();
            String second = OgnlRuntime.getChildSource(context, target, this._children[1]);
            if (!OgnlRuntime.isBoolean(second)) {
                second = OgnlRuntime.getCompiler().createLocalReference(context, second, context.getCurrentType());
            }
            Class secondType = context.getCurrentType();
            boolean mismatched = firstType.isPrimitive() && !secondType.isPrimitive() || !firstType.isPrimitive() && secondType.isPrimitive();
            result = result + "ognl.OgnlOps.booleanValue(" + first + ")";
            result = result + " ? ";
            result = result + (mismatched ? " ($w) " : "") + first;
            result = result + " : ";
            result = result + (mismatched ? " ($w) " : "") + second;
            result = result + ")";
            context.setCurrentObject(target);
            context.setCurrentType(Boolean.TYPE);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        if (this._children.length != 2) {
            throw new UnsupportedCompilationException("Can only compile boolean expressions with two children.");
        }
        String pre = (String)context.get("_currentChain");
        if (pre == null) {
            pre = "";
        }
        String result = "";
        try {
            this._children[0].getValue(context, target);
            String first = ExpressionCompiler.getRootExpression(this._children[0], context.getRoot(), context) + pre + this._children[0].toGetSourceString(context, target);
            if (!OgnlRuntime.isBoolean(first)) {
                first = OgnlRuntime.getCompiler().createLocalReference(context, first, Object.class);
            }
            this._children[1].getValue(context, target);
            String second = ExpressionCompiler.getRootExpression(this._children[1], context.getRoot(), context) + pre + this._children[1].toSetSourceString(context, target);
            if (!OgnlRuntime.isBoolean(second)) {
                second = OgnlRuntime.getCompiler().createLocalReference(context, second, context.getCurrentType());
            }
            result = result + "ognl.OgnlOps.booleanValue(" + first + ")";
            result = result + " ? ";
            result = result + first;
            result = result + " : ";
            result = result + second;
            context.setCurrentObject(target);
            context.setCurrentType(Boolean.TYPE);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }
}

