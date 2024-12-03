/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.ASTAssign;
import ognl.NodeType;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.enhance.OrderedReturn;
import ognl.enhance.UnsupportedCompilationException;

public class ASTVarRef
extends SimpleNode
implements NodeType,
OrderedReturn {
    private String _name;
    protected Class _getterClass;
    protected String _core;
    protected String _last;

    public ASTVarRef(int id) {
        super(id);
    }

    public ASTVarRef(OgnlParser p, int id) {
        super(p, id);
    }

    void setName(String name) {
        this._name = name;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return context.get(this._name);
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        context.put(this._name, value);
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
    public String getCoreExpression() {
        return this._core;
    }

    @Override
    public String getLastExpression() {
        return this._last;
    }

    @Override
    public String toString() {
        return "#" + this._name;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        Object value = context.get(this._name);
        if (value != null) {
            this._getterClass = value.getClass();
        }
        context.setCurrentType(this._getterClass);
        context.setCurrentAccessor(context.getClass());
        context.setCurrentObject(value);
        if (context.getCurrentObject() == null) {
            throw new UnsupportedCompilationException("Current context object is null, can't compile var reference.");
        }
        String pre = "";
        String post = "";
        if (context.getCurrentType() != null) {
            pre = "((" + OgnlRuntime.getCompiler().getInterfaceClass(context.getCurrentType()).getName() + ")";
            post = ")";
        }
        if (this._parent != null && ASTAssign.class.isInstance(this._parent)) {
            this._core = "$1.put(\"" + this._name + "\",";
            this._last = pre + "$1.get(\"" + this._name + "\")" + post;
            return this._core;
        }
        return pre + "$1.get(\"" + this._name + "\")" + post;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return this.toGetSourceString(context, target);
    }
}

