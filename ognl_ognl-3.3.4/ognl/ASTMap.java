/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.HashMap;
import java.util.Map;
import ognl.ASTKeyValue;
import ognl.Node;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.enhance.UnsupportedCompilationException;

public class ASTMap
extends SimpleNode {
    private static Class DEFAULT_MAP_CLASS;
    private String className;

    public ASTMap(int id) {
        super(id);
    }

    public ASTMap(OgnlParser p, int id) {
        super(p, id);
    }

    protected void setClassName(String value) {
        this.className = value;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Map answer;
        if (this.className == null) {
            try {
                answer = (Map)DEFAULT_MAP_CLASS.newInstance();
            }
            catch (Exception ex) {
                throw new OgnlException("Default Map class '" + DEFAULT_MAP_CLASS.getName() + "' instantiation error", ex);
            }
        }
        try {
            answer = (Map)OgnlRuntime.classForName(context, this.className).newInstance();
        }
        catch (Exception ex) {
            throw new OgnlException("Map implementor '" + this.className + "' not found", ex);
        }
        for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
            ASTKeyValue kv = (ASTKeyValue)this._children[i];
            Node k = kv.getKey();
            Node v = kv.getValue();
            answer.put(k.getValue(context, source), v == null ? null : v.getValue(context, source));
        }
        return answer;
    }

    @Override
    public String toString() {
        String result = "#";
        if (this.className != null) {
            result = result + "@" + this.className + "@";
        }
        result = result + "{ ";
        for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
            ASTKeyValue kv = (ASTKeyValue)this._children[i];
            if (i > 0) {
                result = result + ", ";
            }
            result = result + kv.getKey() + " : " + kv.getValue();
        }
        return result + " }";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Map expressions not supported as native java yet.");
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Map expressions not supported as native java yet.");
    }

    static {
        try {
            DEFAULT_MAP_CLASS = Class.forName("java.util.LinkedHashMap");
        }
        catch (ClassNotFoundException ex) {
            DEFAULT_MAP_CLASS = HashMap.class;
        }
    }
}

