/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Method;
import ognl.ASTConst;
import ognl.NodeType;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.enhance.ExpressionCompiler;
import ognl.enhance.UnsupportedCompilationException;

public class ASTStaticMethod
extends SimpleNode
implements NodeType {
    private String _className;
    private String _methodName;
    private Class _getterClass;

    public ASTStaticMethod(int id) {
        super(id);
    }

    public ASTStaticMethod(OgnlParser p, int id) {
        super(p, id);
    }

    void init(String className, String methodName) {
        this._className = className;
        this._methodName = methodName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object[] args = OgnlRuntime.getObjectArrayPool().create(this.jjtGetNumChildren());
        Object root = context.getRoot();
        try {
            int icount = args.length;
            for (int i = 0; i < icount; ++i) {
                args[i] = this._children[i].getValue(context, root);
            }
            Object object = OgnlRuntime.callStaticMethod(context, this._className, this._methodName, args);
            return object;
        }
        finally {
            OgnlRuntime.getObjectArrayPool().recycle(args);
        }
    }

    @Override
    public Class getGetterClass() {
        return this._getterClass;
    }

    @Override
    public Class getSetterClass() {
        return this._getterClass;
    }

    @Override
    public String toString() {
        String result = "@" + this._className + "@" + this._methodName;
        result = result + "(";
        if (this._children != null && this._children.length > 0) {
            for (int i = 0; i < this._children.length; ++i) {
                if (i > 0) {
                    result = result + ", ";
                }
                result = result + this._children[i];
            }
        }
        result = result + ")";
        return result;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String result = this._className + "#" + this._methodName + "(";
        try {
            Class clazz = OgnlRuntime.classForName(context, this._className);
            Method m = OgnlRuntime.getMethod(context, clazz, this._methodName, this._children, true);
            if (clazz == null || m == null) {
                throw new UnsupportedCompilationException("Unable to find class/method combo " + this._className + " / " + this._methodName);
            }
            if (!context.getMemberAccess().isAccessible(context, clazz, m, this._methodName)) {
                throw new UnsupportedCompilationException("Method is not accessible, check your jvm runtime security settings. For static class method " + this._className + " / " + this._methodName);
            }
            if (this._children != null && this._children.length > 0) {
                Class<?>[] parms = m.getParameterTypes();
                for (int i = 0; i < this._children.length; ++i) {
                    Class valueClass;
                    if (i > 0) {
                        result = result + ", ";
                    }
                    Class prevType = context.getCurrentType();
                    Object value = this._children[i].getValue(context, context.getRoot());
                    String parmString = this._children[i].toGetSourceString(context, context.getRoot());
                    if (parmString == null || parmString.trim().length() < 1) {
                        parmString = "null";
                    }
                    if (ASTConst.class.isInstance(this._children[i])) {
                        context.setCurrentType(prevType);
                    }
                    parmString = ExpressionCompiler.getRootExpression(this._children[i], context.getRoot(), context) + parmString;
                    String cast = "";
                    if (ExpressionCompiler.shouldCast(this._children[i])) {
                        cast = (String)context.remove("_preCast");
                    }
                    if (cast == null) {
                        cast = "";
                    }
                    if (!ASTConst.class.isInstance(this._children[i])) {
                        parmString = cast + parmString;
                    }
                    Class clazz2 = valueClass = value != null ? value.getClass() : null;
                    if (NodeType.class.isAssignableFrom(this._children[i].getClass())) {
                        valueClass = ((NodeType)((Object)this._children[i])).getGetterClass();
                    }
                    if (valueClass != parms[i]) {
                        if (parms[i].isArray()) {
                            parmString = OgnlRuntime.getCompiler().createLocalReference(context, "(" + ExpressionCompiler.getCastString(parms[i]) + ")ognl.OgnlOps.toArray(" + parmString + ", " + parms[i].getComponentType().getName() + ".class, true)", parms[i]);
                        } else if (parms[i].isPrimitive()) {
                            Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(parms[i]);
                            parmString = OgnlRuntime.getCompiler().createLocalReference(context, "((" + wrapClass.getName() + ")ognl.OgnlOps.convertValue(" + parmString + "," + wrapClass.getName() + ".class, true))." + OgnlRuntime.getNumericValueGetter(wrapClass), parms[i]);
                        } else if (parms[i] != Object.class) {
                            parmString = OgnlRuntime.getCompiler().createLocalReference(context, "(" + parms[i].getName() + ")ognl.OgnlOps.convertValue(" + parmString + "," + parms[i].getName() + ".class)", parms[i]);
                        } else if (NodeType.class.isInstance(this._children[i]) && ((NodeType)((Object)this._children[i])).getGetterClass() != null && Number.class.isAssignableFrom(((NodeType)((Object)this._children[i])).getGetterClass()) || valueClass.isPrimitive()) {
                            parmString = " ($w) " + parmString;
                        } else if (valueClass.isPrimitive()) {
                            parmString = "($w) " + parmString;
                        }
                    }
                    result = result + parmString;
                }
            }
            result = result + ")";
            try {
                Object contextObj = this.getValueBody(context, target);
                context.setCurrentObject(contextObj);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (m != null) {
                this._getterClass = m.getReturnType();
                context.setCurrentType(m.getReturnType());
                context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return this.toGetSourceString(context, target);
    }
}

