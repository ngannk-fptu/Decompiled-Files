/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.Node;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlParser;
import ognl.SimpleNode;
import ognl.enhance.UnsupportedCompilationException;

public class ASTEval
extends SimpleNode {
    public ASTEval(int id) {
        super(id);
    }

    public ASTEval(OgnlParser p, int id) {
        super(p, id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result;
        Object expr = this._children[0].getValue(context, source);
        Object previousRoot = context.getRoot();
        source = this._children[1].getValue(context, source);
        Node node = expr instanceof Node ? (Node)expr : (Node)Ognl.parseExpression(expr.toString());
        try {
            context.setRoot(source);
            result = node.getValue(context, source);
        }
        finally {
            context.setRoot(previousRoot);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        Object expr = this._children[0].getValue(context, target);
        Object previousRoot = context.getRoot();
        target = this._children[1].getValue(context, target);
        Node node = expr instanceof Node ? (Node)expr : (Node)Ognl.parseExpression(expr.toString());
        try {
            context.setRoot(target);
            node.setValue(context, target, value);
        }
        finally {
            context.setRoot(previousRoot);
        }
    }

    @Override
    public boolean isEvalChain(OgnlContext context) throws OgnlException {
        return true;
    }

    @Override
    public String toString() {
        return "(" + this._children[0] + ")(" + this._children[1] + ")";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Eval expressions not supported as native java yet.");
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Map expressions not supported as native java yet.");
    }
}

