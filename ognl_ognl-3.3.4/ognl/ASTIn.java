/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.NodeType;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.enhance.UnsupportedCompilationException;

public class ASTIn
extends SimpleNode
implements NodeType {
    public ASTIn(int id) {
        super(id);
    }

    public ASTIn(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object v2;
        Object v1 = this._children[0].getValue(context, source);
        return OgnlOps.in(v1, v2 = this._children[1].getValue(context, source)) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public String toString() {
        return this._children[0] + " in " + this._children[1];
    }

    @Override
    public Class getGetterClass() {
        return Boolean.TYPE;
    }

    @Override
    public Class getSetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        try {
            String result = "ognl.OgnlOps.in( ($w) ";
            result = result + OgnlRuntime.getChildSource(context, target, this._children[0]) + ", ($w) " + OgnlRuntime.getChildSource(context, target, this._children[1]);
            result = result + ")";
            context.setCurrentType(Boolean.TYPE);
            return result;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Map expressions not supported as native java yet.");
    }
}

