/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.ASTConst;
import ognl.NodeType;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;

public class ASTInstanceof
extends SimpleNode
implements NodeType {
    private String targetType;

    public ASTInstanceof(int id) {
        super(id);
    }

    public ASTInstanceof(OgnlParser p, int id) {
        super(p, id);
    }

    void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object value = this._children[0].getValue(context, source);
        return OgnlRuntime.isInstance(context, value, this.targetType) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public String toString() {
        return this._children[0] + " instanceof " + this.targetType;
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
            String ret = "";
            ret = ASTConst.class.isInstance(this._children[0]) ? ((Boolean)this.getValueBody(context, target)).toString() : this._children[0].toGetSourceString(context, target) + " instanceof " + this.targetType;
            context.setCurrentType(Boolean.TYPE);
            return ret;
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return this.toGetSourceString(context, target);
    }
}

