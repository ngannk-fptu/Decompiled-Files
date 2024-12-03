/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Method;
import java.util.List;
import ognl.ASTChain;
import ognl.ASTConst;
import ognl.ASTCtor;
import ognl.ASTList;
import ognl.ASTProperty;
import ognl.ASTStaticMethod;
import ognl.ASTTest;
import ognl.Node;
import ognl.NodeType;
import ognl.NullHandler;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.enhance.ExpressionCompiler;
import ognl.enhance.OrderedReturn;
import ognl.enhance.UnsupportedCompilationException;

public class ASTMethod
extends SimpleNode
implements OrderedReturn,
NodeType {
    private String _methodName;
    private String _lastExpression;
    private String _coreExpression;
    private Class _getterClass;

    public ASTMethod(int id) {
        super(id);
    }

    public ASTMethod(OgnlParser p, int id) {
        super(p, id);
    }

    public void setMethodName(String methodName) {
        this._methodName = methodName;
    }

    public String getMethodName() {
        return this._methodName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object[] args = OgnlRuntime.getObjectArrayPool().create(this.jjtGetNumChildren());
        try {
            Object root = context.getRoot();
            int icount = args.length;
            for (int i = 0; i < icount; ++i) {
                args[i] = this._children[i].getValue(context, root);
            }
            Object result = OgnlRuntime.callMethod(context, source, this._methodName, args);
            if (result == null) {
                NullHandler nh = OgnlRuntime.getNullHandler(OgnlRuntime.getTargetClass(source));
                result = nh.nullMethodResult(context, source, this._methodName, args);
            }
            Object object = result;
            return object;
        }
        finally {
            OgnlRuntime.getObjectArrayPool().recycle(args);
        }
    }

    @Override
    public String getLastExpression() {
        return this._lastExpression;
    }

    @Override
    public String getCoreExpression() {
        return this._coreExpression;
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
        String result = this._methodName;
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
        if (target == null) {
            throw new UnsupportedCompilationException("Target object is null.");
        }
        String post = "";
        String result = null;
        Method m = null;
        try {
            m = OgnlRuntime.getMethod(context, context.getCurrentType() != null ? context.getCurrentType() : target.getClass(), this._methodName, this._children, false);
            Class[] argumentClasses = ASTMethod.getChildrenClasses(context, this._children);
            if (m == null) {
                m = OgnlRuntime.getReadMethod(target.getClass(), this._methodName, argumentClasses);
            }
            if (m == null) {
                m = OgnlRuntime.getWriteMethod(target.getClass(), this._methodName, argumentClasses);
                if (m != null) {
                    context.setCurrentType(m.getReturnType());
                    context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
                    this._coreExpression = this.toSetSourceString(context, target);
                    if (this._coreExpression == null || this._coreExpression.length() < 1) {
                        throw new UnsupportedCompilationException("can't find suitable getter method");
                    }
                    this._coreExpression = this._coreExpression + ";";
                    this._lastExpression = "null";
                    return this._coreExpression;
                }
                return "";
            }
            this._getterClass = m.getReturnType();
            boolean varArgs = m.isVarArgs();
            if (varArgs) {
                throw new UnsupportedCompilationException("Javassist does not currently support varargs method calls");
            }
            result = "." + m.getName() + "(";
            if (this._children != null && this._children.length > 0) {
                Class<?>[] parms = m.getParameterTypes();
                String prevCast = (String)context.remove("_preCast");
                for (int i = 0; i < this._children.length; ++i) {
                    Class valueClass;
                    if (i > 0) {
                        result = result + ", ";
                    }
                    Class prevType = context.getCurrentType();
                    context.setCurrentObject(context.getRoot());
                    context.setCurrentType(context.getRoot() != null ? context.getRoot().getClass() : null);
                    context.setCurrentAccessor(null);
                    context.setPreviousType(null);
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
                    Class clazz = valueClass = value != null ? value.getClass() : null;
                    if (NodeType.class.isAssignableFrom(this._children[i].getClass())) {
                        valueClass = ((NodeType)((Object)this._children[i])).getGetterClass();
                    }
                    if ((!varArgs || varArgs && i + 1 < parms.length) && valueClass != parms[i]) {
                        if (parms[i].isArray()) {
                            parmString = OgnlRuntime.getCompiler().createLocalReference(context, "(" + ExpressionCompiler.getCastString(parms[i]) + ")ognl.OgnlOps#toArray(" + parmString + ", " + parms[i].getComponentType().getName() + ".class, true)", parms[i]);
                        } else if (parms[i].isPrimitive()) {
                            Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(parms[i]);
                            parmString = OgnlRuntime.getCompiler().createLocalReference(context, "((" + wrapClass.getName() + ")ognl.OgnlOps#convertValue(" + parmString + "," + wrapClass.getName() + ".class, true))." + OgnlRuntime.getNumericValueGetter(wrapClass), parms[i]);
                        } else if (parms[i] != Object.class) {
                            parmString = OgnlRuntime.getCompiler().createLocalReference(context, "(" + parms[i].getName() + ")ognl.OgnlOps#convertValue(" + parmString + "," + parms[i].getName() + ".class)", parms[i]);
                        } else if (NodeType.class.isInstance(this._children[i]) && ((NodeType)((Object)this._children[i])).getGetterClass() != null && Number.class.isAssignableFrom(((NodeType)((Object)this._children[i])).getGetterClass()) || valueClass != null && valueClass.isPrimitive()) {
                            parmString = " ($w) " + parmString;
                        } else if (valueClass != null && valueClass.isPrimitive()) {
                            parmString = "($w) " + parmString;
                        }
                    }
                    result = result + parmString;
                }
                if (prevCast != null) {
                    context.put("_preCast", prevCast);
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        try {
            Object contextObj = this.getValueBody(context, target);
            context.setCurrentObject(contextObj);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        result = result + ")" + post;
        if (m.getReturnType() == Void.TYPE) {
            this._coreExpression = result + ";";
            this._lastExpression = "null";
        }
        context.setCurrentType(m.getReturnType());
        context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        boolean varArgs;
        Method m = OgnlRuntime.getWriteMethod(context.getCurrentType() != null ? context.getCurrentType() : target.getClass(), this._methodName, ASTMethod.getChildrenClasses(context, this._children));
        if (m == null) {
            throw new UnsupportedCompilationException("Unable to determine setter method generation for " + this._methodName);
        }
        String post = "";
        String result = "." + m.getName() + "(";
        if (m.getReturnType() != Void.TYPE && m.getReturnType().isPrimitive() && (this._parent == null || !ASTTest.class.isInstance(this._parent))) {
            Class wrapper = OgnlRuntime.getPrimitiveWrapperClass(m.getReturnType());
            ExpressionCompiler.addCastString(context, "new " + wrapper.getName() + "(");
            post = ")";
            this._getterClass = wrapper;
        }
        if (varArgs = m.isVarArgs()) {
            throw new UnsupportedCompilationException("Javassist does not currently support varargs method calls");
        }
        try {
            if (this._children != null && this._children.length > 0) {
                Class<?>[] parms = m.getParameterTypes();
                String prevCast = (String)context.remove("_preCast");
                for (int i = 0; i < this._children.length; ++i) {
                    Class valueClass;
                    if (i > 0) {
                        result = result + ", ";
                    }
                    Class prevType = context.getCurrentType();
                    context.setCurrentObject(context.getRoot());
                    context.setCurrentType(context.getRoot() != null ? context.getRoot().getClass() : null);
                    context.setCurrentAccessor(null);
                    context.setPreviousType(null);
                    Object value = this._children[i].getValue(context, context.getRoot());
                    String parmString = this._children[i].toSetSourceString(context, context.getRoot());
                    if (context.getCurrentType() == Void.TYPE || context.getCurrentType() == Void.TYPE) {
                        throw new UnsupportedCompilationException("Method argument can't be a void type.");
                    }
                    if (parmString == null || parmString.trim().length() < 1) {
                        if (ASTProperty.class.isInstance(this._children[i]) || ASTMethod.class.isInstance(this._children[i]) || ASTStaticMethod.class.isInstance(this._children[i]) || ASTChain.class.isInstance(this._children[i])) {
                            throw new UnsupportedCompilationException("ASTMethod setter child returned null from a sub property expression.");
                        }
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
                    parmString = cast + parmString;
                    Class clazz = valueClass = value != null ? value.getClass() : null;
                    if (NodeType.class.isAssignableFrom(this._children[i].getClass())) {
                        valueClass = ((NodeType)((Object)this._children[i])).getGetterClass();
                    }
                    if (valueClass != parms[i]) {
                        if (parms[i].isArray()) {
                            parmString = OgnlRuntime.getCompiler().createLocalReference(context, "(" + ExpressionCompiler.getCastString(parms[i]) + ")ognl.OgnlOps#toArray(" + parmString + ", " + parms[i].getComponentType().getName() + ".class)", parms[i]);
                        } else if (parms[i].isPrimitive()) {
                            Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(parms[i]);
                            parmString = OgnlRuntime.getCompiler().createLocalReference(context, "((" + wrapClass.getName() + ")ognl.OgnlOps#convertValue(" + parmString + "," + wrapClass.getName() + ".class, true))." + OgnlRuntime.getNumericValueGetter(wrapClass), parms[i]);
                        } else if (parms[i] != Object.class) {
                            parmString = OgnlRuntime.getCompiler().createLocalReference(context, "(" + parms[i].getName() + ")ognl.OgnlOps#convertValue(" + parmString + "," + parms[i].getName() + ".class)", parms[i]);
                        } else if (NodeType.class.isInstance(this._children[i]) && ((NodeType)((Object)this._children[i])).getGetterClass() != null && Number.class.isAssignableFrom(((NodeType)((Object)this._children[i])).getGetterClass()) || valueClass != null && valueClass.isPrimitive()) {
                            parmString = " ($w) " + parmString;
                        } else if (valueClass != null && valueClass.isPrimitive()) {
                            parmString = "($w) " + parmString;
                        }
                    }
                    result = result + parmString;
                }
                if (prevCast != null) {
                    context.put("_preCast", prevCast);
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        try {
            Object contextObj = this.getValueBody(context, target);
            context.setCurrentObject(contextObj);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        context.setCurrentType(m.getReturnType());
        context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
        return result + ")" + post;
    }

    private static Class getClassMatchingAllChildren(OgnlContext context, Node[] _children) {
        Class[] cc = ASTMethod.getChildrenClasses(context, _children);
        Class<Object> componentType = null;
        for (int j = 0; j < cc.length; ++j) {
            Class pc;
            Class ic = cc[j];
            if (ic == null) {
                componentType = Object.class;
                break;
            }
            if (componentType == null) {
                componentType = ic;
                continue;
            }
            if (componentType.isAssignableFrom(ic)) continue;
            if (ic.isAssignableFrom(componentType)) {
                componentType = ic;
                continue;
            }
            while ((pc = componentType.getSuperclass()) != null) {
                if (!pc.isAssignableFrom(ic)) continue;
                componentType = pc;
                break;
            }
            if (componentType.isAssignableFrom(ic)) continue;
            componentType = Object.class;
            break;
        }
        if (componentType == null) {
            componentType = Object.class;
        }
        return componentType;
    }

    private static Class[] getChildrenClasses(OgnlContext context, Node[] _children) {
        if (_children == null) {
            return null;
        }
        Class[] argumentClasses = new Class[_children.length];
        for (int i = 0; i < _children.length; ++i) {
            Node child = _children[i];
            if (child instanceof ASTList) {
                argumentClasses[i] = List.class;
                continue;
            }
            if (child instanceof NodeType) {
                argumentClasses[i] = ((NodeType)((Object)child)).getGetterClass();
                continue;
            }
            if (child instanceof ASTCtor) {
                try {
                    argumentClasses[i] = ((ASTCtor)child).getCreatedClass(context);
                    continue;
                }
                catch (ClassNotFoundException nfe) {
                    throw OgnlOps.castToRuntime(nfe);
                }
            }
            if (child instanceof ASTTest) {
                argumentClasses[i] = ASTMethod.getClassMatchingAllChildren(context, ((ASTTest)child)._children);
                continue;
            }
            throw new UnsupportedOperationException("Don't know how to handle child: " + child);
        }
        return argumentClasses;
    }

    @Override
    public boolean isSimpleMethod(OgnlContext context) {
        return true;
    }
}

