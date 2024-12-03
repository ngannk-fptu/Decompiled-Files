/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.ArrayList;
import java.util.List;
import ognl.ASTConst;
import ognl.ASTCtor;
import ognl.NodeType;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.enhance.ExpressionCompiler;
import ognl.enhance.UnsupportedCompilationException;

public class ASTList
extends SimpleNode
implements NodeType {
    public ASTList(int id) {
        super(id);
    }

    public ASTList(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        ArrayList<Object> answer = new ArrayList<Object>(this.jjtGetNumChildren());
        for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
            answer.add(this._children[i].getValue(context, source));
        }
        return answer;
    }

    @Override
    public Class getGetterClass() {
        return null;
    }

    @Override
    public Class getSetterClass() {
        return null;
    }

    @Override
    public String toString() {
        String result = "{ ";
        for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
            if (i > 0) {
                result = result + ", ";
            }
            result = result + this._children[i].toString();
        }
        return result + " }";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String result = "";
        boolean array = false;
        if (this._parent != null && ASTCtor.class.isInstance(this._parent) && ((ASTCtor)this._parent).isArray()) {
            array = true;
        }
        context.setCurrentType(List.class);
        context.setCurrentAccessor(List.class);
        if (!array) {
            if (this.jjtGetNumChildren() < 1) {
                return "java.util.Arrays.asList( new Object[0])";
            }
            result = result + "java.util.Arrays.asList( new Object[] ";
        }
        result = result + "{ ";
        try {
            for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
                if (i > 0) {
                    result = result + ", ";
                }
                Class prevType = context.getCurrentType();
                Object objValue = this._children[i].getValue(context, context.getRoot());
                String value = this._children[i].toGetSourceString(context, target);
                if (ASTConst.class.isInstance(this._children[i])) {
                    context.setCurrentType(prevType);
                }
                value = ExpressionCompiler.getRootExpression(this._children[i], target, context) + value;
                String cast = "";
                if (ExpressionCompiler.shouldCast(this._children[i])) {
                    cast = (String)context.remove("_preCast");
                }
                if (cast == null) {
                    cast = "";
                }
                if (!ASTConst.class.isInstance(this._children[i])) {
                    value = cast + value;
                }
                Class ctorClass = (Class)context.get("_ctorClass");
                if (array && ctorClass != null && !ctorClass.isPrimitive()) {
                    Class valueClass;
                    Class clazz = valueClass = value != null ? value.getClass() : null;
                    if (NodeType.class.isAssignableFrom(this._children[i].getClass())) {
                        valueClass = ((NodeType)((Object)this._children[i])).getGetterClass();
                    }
                    if (valueClass != null && ctorClass.isArray()) {
                        value = OgnlRuntime.getCompiler().createLocalReference(context, "(" + ExpressionCompiler.getCastString(ctorClass) + ")ognl.OgnlOps.toArray(" + value + ", " + ctorClass.getComponentType().getName() + ".class, true)", ctorClass);
                    } else if (ctorClass.isPrimitive()) {
                        Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(ctorClass);
                        value = OgnlRuntime.getCompiler().createLocalReference(context, "((" + wrapClass.getName() + ")ognl.OgnlOps.convertValue(" + value + "," + wrapClass.getName() + ".class, true))." + OgnlRuntime.getNumericValueGetter(wrapClass), ctorClass);
                    } else if (ctorClass != Object.class) {
                        value = OgnlRuntime.getCompiler().createLocalReference(context, "(" + ctorClass.getName() + ")ognl.OgnlOps.convertValue(" + value + "," + ctorClass.getName() + ".class)", ctorClass);
                    } else if (NodeType.class.isInstance(this._children[i]) && ((NodeType)((Object)this._children[i])).getGetterClass() != null && Number.class.isAssignableFrom(((NodeType)((Object)this._children[i])).getGetterClass()) || valueClass.isPrimitive()) {
                        value = " ($w) (" + value + ")";
                    } else if (valueClass.isPrimitive()) {
                        value = "($w) (" + value + ")";
                    }
                } else if (ctorClass == null || !ctorClass.isPrimitive()) {
                    value = " ($w) (" + value + ")";
                }
                if (objValue == null || value.length() <= 0) {
                    value = "null";
                }
                result = result + value;
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        context.setCurrentType(List.class);
        context.setCurrentAccessor(List.class);
        result = result + "}";
        if (!array) {
            result = result + ")";
        }
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Can't generate setter for ASTList.");
    }
}

