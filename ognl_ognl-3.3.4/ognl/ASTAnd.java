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

public class ASTAnd
extends BooleanExpression {
    public ASTAnd(int id) {
        super(id);
    }

    public ASTAnd(OgnlParser p, int id) {
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
            if (i != last && !OgnlOps.booleanValue(result)) break;
        }
        return result;
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        int last = this._children.length - 1;
        for (int i = 0; i < last; ++i) {
            Object v = this._children[i].getValue(context, target);
            if (OgnlOps.booleanValue(v)) continue;
            return;
        }
        this._children[last].setValue(context, target, value);
    }

    @Override
    public String getExpressionOperator(int index) {
        return "&&";
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
        String result = "";
        try {
            String second;
            String first = OgnlRuntime.getChildSource(context, target, this._children[0]);
            if (!OgnlOps.booleanValue(context.getCurrentObject())) {
                throw new UnsupportedCompilationException("And expression can't be compiled until all conditions are true.");
            }
            if (!OgnlRuntime.isBoolean(first) && !context.getCurrentType().isPrimitive()) {
                first = OgnlRuntime.getCompiler().createLocalReference(context, first, context.getCurrentType());
            }
            if (!OgnlRuntime.isBoolean(second = OgnlRuntime.getChildSource(context, target, this._children[1])) && !context.getCurrentType().isPrimitive()) {
                second = OgnlRuntime.getCompiler().createLocalReference(context, second, context.getCurrentType());
            }
            result = result + "(ognl.OgnlOps.booleanValue(" + first + ")";
            result = result + " ? ";
            result = result + " ($w) (" + second + ")";
            result = result + " : ";
            result = result + " ($w) (" + first + ")";
            result = result + ")";
            context.setCurrentObject(target);
            context.setCurrentType(Object.class);
        }
        catch (NullPointerException e) {
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
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
            if (!OgnlOps.booleanValue(this._children[0].getValue(context, target))) {
                throw new UnsupportedCompilationException("And expression can't be compiled until all conditions are true.");
            }
            String first = ExpressionCompiler.getRootExpression(this._children[0], context.getRoot(), context) + pre + this._children[0].toGetSourceString(context, target);
            this._children[1].getValue(context, target);
            String second = ExpressionCompiler.getRootExpression(this._children[1], context.getRoot(), context) + pre + this._children[1].toSetSourceString(context, target);
            result = !OgnlRuntime.isBoolean(first) ? result + "if(ognl.OgnlOps.booleanValue(" + first + ")){" : result + "if(" + first + "){";
            result = result + second;
            result = result + "; } ";
            context.setCurrentObject(target);
            context.setCurrentType(Object.class);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }
}

