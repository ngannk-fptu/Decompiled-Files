/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;
import ognl.ASTChain;
import ognl.ASTConst;
import ognl.DynamicSubscript;
import ognl.NoSuchPropertyException;
import ognl.NodeType;
import ognl.ObjectIndexedPropertyDescriptor;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import ognl.SimpleNode;
import ognl.enhance.ExpressionCompiler;
import ognl.enhance.UnsupportedCompilationException;

public class ASTProperty
extends SimpleNode
implements NodeType {
    private boolean _indexedAccess = false;
    private Class _getterClass;
    private Class _setterClass;

    public ASTProperty(int id) {
        super(id);
    }

    public void setIndexedAccess(boolean value) {
        this._indexedAccess = value;
    }

    public boolean isIndexedAccess() {
        return this._indexedAccess;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getIndexedPropertyType(OgnlContext context, Object source) throws OgnlException {
        Class type = context.getCurrentType();
        Class prevType = context.getPreviousType();
        try {
            Object property;
            if (!this.isIndexedAccess() && (property = this.getProperty(context, source)) instanceof String) {
                int n = OgnlRuntime.getIndexedPropertyType(context, source == null ? null : OgnlRuntime.getCompiler().getInterfaceClass(source.getClass()), (String)property);
                return n;
            }
            int n = OgnlRuntime.INDEXED_PROPERTY_NONE;
            return n;
        }
        finally {
            context.setCurrentObject(source);
            context.setCurrentType(type);
            context.setPreviousType(prevType);
        }
    }

    public Object getProperty(OgnlContext context, Object source) throws OgnlException {
        return this._children[0].getValue(context, context.getRoot());
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object property = this.getProperty(context, source);
        Object result = OgnlRuntime.getProperty(context, source, property);
        if (result == null) {
            result = OgnlRuntime.getNullHandler(OgnlRuntime.getTargetClass(source)).nullPropertyValue(context, source, property);
        }
        return result;
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        OgnlRuntime.setProperty(context, target, this.getProperty(context, target), value);
    }

    @Override
    public boolean isNodeSimpleProperty(OgnlContext context) throws OgnlException {
        return this._children != null && this._children.length == 1 && ((SimpleNode)this._children[0]).isConstant(context);
    }

    @Override
    public Class getGetterClass() {
        return this._getterClass;
    }

    @Override
    public Class getSetterClass() {
        return this._setterClass;
    }

    @Override
    public String toString() {
        String result = this.isIndexedAccess() ? "[" + this._children[0] + "]" : ((ASTConst)this._children[0]).getValue().toString();
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (context.getCurrentObject() == null) {
            throw new UnsupportedCompilationException("Current target is null.");
        }
        String result = "";
        Method m = null;
        try {
            PropertyDescriptor pd;
            if (this.isIndexedAccess()) {
                String cast;
                Object value = this._children[0].getValue(context, context.getRoot());
                if (value == null || DynamicSubscript.class.isAssignableFrom(value.getClass())) {
                    throw new UnsupportedCompilationException("Value passed as indexed property was null or not supported.");
                }
                String srcString = this._children[0].toGetSourceString(context, context.getRoot());
                srcString = ExpressionCompiler.getRootExpression(this._children[0], context.getRoot(), context) + srcString;
                if (ASTChain.class.isInstance(this._children[0]) && (cast = (String)context.remove("_preCast")) != null) {
                    srcString = cast + srcString;
                }
                if (ASTConst.class.isInstance(this._children[0]) && String.class.isInstance(context.getCurrentObject())) {
                    srcString = "\"" + srcString + "\"";
                }
                if (context.get("_indexedMethod") != null) {
                    m = (Method)context.remove("_indexedMethod");
                    this._getterClass = m.getReturnType();
                    Object indexedValue = OgnlRuntime.callMethod(context, target, m.getName(), new Object[]{value});
                    context.setCurrentType(this._getterClass);
                    context.setCurrentObject(indexedValue);
                    context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
                    return "." + m.getName() + "(" + srcString + ")";
                }
                PropertyAccessor p = OgnlRuntime.getPropertyAccessor(target.getClass());
                Object currObj2 = context.getCurrentObject();
                Class currType = context.getCurrentType();
                Class prevType = context.getPreviousType();
                Object indexVal = p.getProperty(context, target, value);
                context.setCurrentObject(currObj2);
                context.setCurrentType(currType);
                context.setPreviousType(prevType);
                if (ASTConst.class.isInstance(this._children[0]) && Number.class.isInstance(context.getCurrentObject())) {
                    context.setCurrentType(OgnlRuntime.getPrimitiveWrapperClass(context.getCurrentObject().getClass()));
                }
                result = p.getSourceAccessor(context, target, srcString);
                this._getterClass = context.getCurrentType();
                context.setCurrentObject(indexVal);
                return result;
            }
            String name = ((ASTConst)this._children[0]).getValue().toString();
            if (!Iterator.class.isAssignableFrom(context.getCurrentObject().getClass()) || Iterator.class.isAssignableFrom(context.getCurrentObject().getClass()) && name.indexOf("next") < 0) {
                Object currObj = target;
                try {
                    target = this.getValue(context, context.getCurrentObject());
                }
                catch (NoSuchPropertyException e) {
                    try {
                        target = this.getValue(context, context.getRoot());
                    }
                    catch (NoSuchPropertyException currObj2) {
                        // empty catch block
                    }
                }
                finally {
                    context.setCurrentObject(currObj);
                }
            }
            if ((pd = OgnlRuntime.getPropertyDescriptor(context.getCurrentObject().getClass(), name)) != null && pd.getReadMethod() != null && !context.getMemberAccess().isAccessible(context, context.getCurrentObject(), pd.getReadMethod(), name)) {
                throw new UnsupportedCompilationException("Member access forbidden for property " + name + " on class " + context.getCurrentObject().getClass());
            }
            if (this.getIndexedPropertyType(context, context.getCurrentObject()) > 0 && pd != null) {
                if (pd instanceof IndexedPropertyDescriptor) {
                    m = ((IndexedPropertyDescriptor)pd).getIndexedReadMethod();
                } else if (pd instanceof ObjectIndexedPropertyDescriptor) {
                    m = ((ObjectIndexedPropertyDescriptor)pd).getIndexedReadMethod();
                } else {
                    throw new OgnlException("property '" + name + "' is not an indexed property");
                }
                if (this._parent == null) {
                    m = OgnlRuntime.getReadMethod(context.getCurrentObject().getClass(), name);
                    result = m.getName() + "()";
                    this._getterClass = m.getReturnType();
                } else {
                    context.put("_indexedMethod", m);
                }
            } else {
                PropertyAccessor pa = OgnlRuntime.getPropertyAccessor(context.getCurrentObject().getClass());
                if (context.getCurrentObject().getClass().isArray()) {
                    if (pd == null) {
                        pd = OgnlRuntime.getProperty(context.getCurrentObject().getClass(), name);
                        if (pd != null && pd.getReadMethod() != null) {
                            m = pd.getReadMethod();
                            result = pd.getName();
                        } else {
                            this._getterClass = Integer.TYPE;
                            context.setCurrentAccessor(context.getCurrentObject().getClass());
                            context.setCurrentType(Integer.TYPE);
                            result = "." + name;
                        }
                    }
                } else if (pd != null && pd.getReadMethod() != null) {
                    m = pd.getReadMethod();
                    result = "." + m.getName() + "()";
                } else if (pa != null) {
                    Object currObj = context.getCurrentObject();
                    Class currType = context.getCurrentType();
                    Class prevType = context.getPreviousType();
                    String srcString = this._children[0].toGetSourceString(context, context.getRoot());
                    if (ASTConst.class.isInstance(this._children[0]) && String.class.isInstance(context.getCurrentObject())) {
                        srcString = "\"" + srcString + "\"";
                    }
                    context.setCurrentObject(currObj);
                    context.setCurrentType(currType);
                    context.setPreviousType(prevType);
                    result = pa.getSourceAccessor(context, context.getCurrentObject(), srcString);
                    this._getterClass = context.getCurrentType();
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        if (m != null) {
            this._getterClass = m.getReturnType();
            context.setCurrentType(m.getReturnType());
            context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
        }
        context.setCurrentObject(target);
        return result;
    }

    Method getIndexedWriteMethod(PropertyDescriptor pd) {
        if (IndexedPropertyDescriptor.class.isInstance(pd)) {
            return ((IndexedPropertyDescriptor)pd).getIndexedWriteMethod();
        }
        if (ObjectIndexedPropertyDescriptor.class.isInstance(pd)) {
            return ((ObjectIndexedPropertyDescriptor)pd).getIndexedWriteMethod();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        String result = "";
        Method m = null;
        if (context.getCurrentObject() == null) {
            throw new UnsupportedCompilationException("Current target is null.");
        }
        try {
            PropertyDescriptor pd;
            if (this.isIndexedAccess()) {
                String cast;
                Object value = this._children[0].getValue(context, context.getRoot());
                if (value == null) {
                    throw new UnsupportedCompilationException("Value passed as indexed property is null, can't enhance statement to bytecode.");
                }
                String srcString = this._children[0].toGetSourceString(context, context.getRoot());
                srcString = ExpressionCompiler.getRootExpression(this._children[0], context.getRoot(), context) + srcString;
                if (ASTChain.class.isInstance(this._children[0]) && (cast = (String)context.remove("_preCast")) != null) {
                    srcString = cast + srcString;
                }
                if (ASTConst.class.isInstance(this._children[0]) && String.class.isInstance(context.getCurrentObject())) {
                    srcString = "\"" + srcString + "\"";
                }
                if (context.get("_indexedMethod") != null) {
                    m = (Method)context.remove("_indexedMethod");
                    PropertyDescriptor pd2 = (PropertyDescriptor)context.remove("_indexedDescriptor");
                    boolean lastChild = this.lastChild(context);
                    if (lastChild && (m = this.getIndexedWriteMethod(pd2)) == null) {
                        throw new UnsupportedCompilationException("Indexed property has no corresponding write method.");
                    }
                    this._setterClass = m.getParameterTypes()[0];
                    Object indexedValue = null;
                    if (!lastChild) {
                        indexedValue = OgnlRuntime.callMethod(context, target, m.getName(), new Object[]{value});
                    }
                    context.setCurrentType(this._setterClass);
                    context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
                    if (!lastChild) {
                        context.setCurrentObject(indexedValue);
                        return "." + m.getName() + "(" + srcString + ")";
                    }
                    return "." + m.getName() + "(" + srcString + ", $3)";
                }
                PropertyAccessor p = OgnlRuntime.getPropertyAccessor(target.getClass());
                Object currObj2 = context.getCurrentObject();
                Class currType = context.getCurrentType();
                Class prevType = context.getPreviousType();
                Object indexVal = p.getProperty(context, target, value);
                context.setCurrentObject(currObj2);
                context.setCurrentType(currType);
                context.setPreviousType(prevType);
                if (ASTConst.class.isInstance(this._children[0]) && Number.class.isInstance(context.getCurrentObject())) {
                    context.setCurrentType(OgnlRuntime.getPrimitiveWrapperClass(context.getCurrentObject().getClass()));
                }
                result = this.lastChild(context) ? p.getSourceSetter(context, target, srcString) : p.getSourceAccessor(context, target, srcString);
                this._getterClass = context.getCurrentType();
                context.setCurrentObject(indexVal);
                return result;
            }
            String name = ((ASTConst)this._children[0]).getValue().toString();
            if (!Iterator.class.isAssignableFrom(context.getCurrentObject().getClass()) || Iterator.class.isAssignableFrom(context.getCurrentObject().getClass()) && name.indexOf("next") < 0) {
                Object currObj = target;
                try {
                    target = this.getValue(context, context.getCurrentObject());
                }
                catch (NoSuchPropertyException e) {
                    try {
                        target = this.getValue(context, context.getRoot());
                    }
                    catch (NoSuchPropertyException currObj2) {
                        // empty catch block
                    }
                }
                finally {
                    context.setCurrentObject(currObj);
                }
            }
            if ((pd = OgnlRuntime.getPropertyDescriptor(OgnlRuntime.getCompiler().getInterfaceClass(context.getCurrentObject().getClass()), name)) != null) {
                Method pdMethod;
                Method method = pdMethod = this.lastChild(context) ? pd.getWriteMethod() : pd.getReadMethod();
                if (pdMethod != null && !context.getMemberAccess().isAccessible(context, context.getCurrentObject(), pdMethod, name)) {
                    throw new UnsupportedCompilationException("Member access forbidden for property " + name + " on class " + context.getCurrentObject().getClass());
                }
            }
            if (pd != null && this.getIndexedPropertyType(context, context.getCurrentObject()) > 0) {
                if (pd instanceof IndexedPropertyDescriptor) {
                    IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)pd;
                    m = this.lastChild(context) ? ipd.getIndexedWriteMethod() : ipd.getIndexedReadMethod();
                } else if (pd instanceof ObjectIndexedPropertyDescriptor) {
                    ObjectIndexedPropertyDescriptor opd = (ObjectIndexedPropertyDescriptor)pd;
                    m = this.lastChild(context) ? opd.getIndexedWriteMethod() : opd.getIndexedReadMethod();
                } else {
                    throw new OgnlException("property '" + name + "' is not an indexed property");
                }
                if (this._parent == null) {
                    m = OgnlRuntime.getWriteMethod(context.getCurrentObject().getClass(), name);
                    Class<?> parm = m.getParameterTypes()[0];
                    String cast = parm.isArray() ? ExpressionCompiler.getCastString(parm) : parm.getName();
                    result = m.getName() + "((" + cast + ")$3)";
                    this._setterClass = parm;
                } else {
                    context.put("_indexedMethod", m);
                    context.put("_indexedDescriptor", pd);
                }
            } else {
                PropertyAccessor pa = OgnlRuntime.getPropertyAccessor(context.getCurrentObject().getClass());
                if (target != null) {
                    this._setterClass = target.getClass();
                }
                if (this._parent != null && pd != null && pa == null) {
                    m = pd.getReadMethod();
                    result = m.getName() + "()";
                } else if (context.getCurrentObject().getClass().isArray()) {
                    result = "";
                } else if (pa != null) {
                    Object currObj = context.getCurrentObject();
                    String srcString = this._children[0].toGetSourceString(context, context.getRoot());
                    if (ASTConst.class.isInstance(this._children[0]) && String.class.isInstance(context.getCurrentObject())) {
                        srcString = "\"" + srcString + "\"";
                    }
                    context.setCurrentObject(currObj);
                    result = !this.lastChild(context) ? pa.getSourceAccessor(context, context.getCurrentObject(), srcString) : pa.getSourceSetter(context, context.getCurrentObject(), srcString);
                    this._getterClass = context.getCurrentType();
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        context.setCurrentObject(target);
        if (m != null) {
            context.setCurrentType(m.getReturnType());
            context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
        }
        return result;
    }
}

