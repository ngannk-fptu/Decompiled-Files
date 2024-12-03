/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.ArrayList;
import java.util.Enumeration;
import ognl.ElementsAccessor;
import ognl.Node;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.enhance.UnsupportedCompilationException;

public class ASTSelectFirst
extends SimpleNode {
    public ASTSelectFirst(int id) {
        super(id);
    }

    public ASTSelectFirst(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Node expr = this._children[0];
        ArrayList answer = new ArrayList();
        ElementsAccessor elementsAccessor = OgnlRuntime.getElementsAccessor(OgnlRuntime.getTargetClass(source));
        Enumeration e = elementsAccessor.getElements(source);
        while (e.hasMoreElements()) {
            Object next = e.nextElement();
            if (!OgnlOps.booleanValue(expr.getValue(context, next))) continue;
            answer.add(next);
            break;
        }
        return answer;
    }

    @Override
    public String toString() {
        return "{^ " + this._children[0] + " }";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Eval expressions not supported as native java yet.");
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Eval expressions not supported as native java yet.");
    }
}

