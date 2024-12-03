/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import ognl.NoSuchPropertyException;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import ognl.enhance.ExpressionCompiler;
import ognl.enhance.UnsupportedCompilationException;

public class ObjectPropertyAccessor
implements PropertyAccessor {
    public Object getPossibleProperty(Map context, Object target, String name) throws OgnlException {
        Object result;
        OgnlContext ognlContext = (OgnlContext)context;
        try {
            result = OgnlRuntime.getMethodValue(ognlContext, target, name, true);
            if (result == OgnlRuntime.NotFound) {
                result = OgnlRuntime.getFieldValue(ognlContext, target, name, true);
            }
        }
        catch (IntrospectionException ex) {
            throw new OgnlException(name, ex);
        }
        catch (OgnlException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new OgnlException(name, ex);
        }
        return result;
    }

    public Object setPossibleProperty(Map context, Object target, String name, Object value) throws OgnlException {
        Object result = null;
        OgnlContext ognlContext = (OgnlContext)context;
        try {
            Method m;
            if (!OgnlRuntime.setMethodValue(ognlContext, target, name, value, true)) {
                Object object = result = OgnlRuntime.setFieldValue(ognlContext, target, name, value) ? null : OgnlRuntime.NotFound;
            }
            if (result == OgnlRuntime.NotFound && (m = OgnlRuntime.getWriteMethod(target.getClass(), name)) != null) {
                result = m.invoke(target, value);
            }
        }
        catch (IntrospectionException ex) {
            throw new OgnlException(name, ex);
        }
        catch (OgnlException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new OgnlException(name, ex);
        }
        return result;
    }

    public boolean hasGetProperty(OgnlContext context, Object target, Object oname) throws OgnlException {
        try {
            return OgnlRuntime.hasGetProperty(context, target, oname);
        }
        catch (IntrospectionException ex) {
            throw new OgnlException("checking if " + target + " has gettable property " + oname, ex);
        }
    }

    public boolean hasGetProperty(Map context, Object target, Object oname) throws OgnlException {
        return this.hasGetProperty((OgnlContext)context, target, oname);
    }

    public boolean hasSetProperty(OgnlContext context, Object target, Object oname) throws OgnlException {
        try {
            return OgnlRuntime.hasSetProperty(context, target, oname);
        }
        catch (IntrospectionException ex) {
            throw new OgnlException("checking if " + target + " has settable property " + oname, ex);
        }
    }

    public boolean hasSetProperty(Map context, Object target, Object oname) throws OgnlException {
        return this.hasSetProperty((OgnlContext)context, target, oname);
    }

    @Override
    public Object getProperty(Map context, Object target, Object oname) throws OgnlException {
        Object result = null;
        String name = oname.toString();
        result = this.getPossibleProperty(context, target, name);
        if (result == OgnlRuntime.NotFound) {
            throw new NoSuchPropertyException(target, name);
        }
        return result;
    }

    @Override
    public void setProperty(Map context, Object target, Object oname, Object value) throws OgnlException {
        String name = oname.toString();
        Object result = this.setPossibleProperty(context, target, name, value);
        if (result == OgnlRuntime.NotFound) {
            throw new NoSuchPropertyException(target, name);
        }
    }

    public Class getPropertyClass(OgnlContext context, Object target, Object index) {
        try {
            Method m = OgnlRuntime.getReadMethod(target.getClass(), index.toString());
            if (m == null) {
                if (String.class.isAssignableFrom(index.getClass()) && !target.getClass().isArray()) {
                    String indexStr = (String)index;
                    String key = indexStr.indexOf(34) >= 0 ? indexStr.replaceAll("\"", "") : indexStr;
                    try {
                        Field f = target.getClass().getField(key);
                        if (f != null) {
                            return f.getType();
                        }
                    }
                    catch (NoSuchFieldException e) {
                        return null;
                    }
                }
                return null;
            }
            return m.getReturnType();
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        try {
            String indexStr = index.toString();
            String methodName = indexStr.indexOf(34) >= 0 ? indexStr.replaceAll("\"", "") : indexStr;
            Method m = OgnlRuntime.getReadMethod(target.getClass(), methodName);
            if (m == null && context.getCurrentObject() != null) {
                String currentObjectStr = context.getCurrentObject().toString();
                m = OgnlRuntime.getReadMethod(target.getClass(), currentObjectStr.indexOf(34) >= 0 ? currentObjectStr.replaceAll("\"", "") : currentObjectStr);
            }
            if (m == null) {
                try {
                    Field f;
                    if (String.class.isAssignableFrom(index.getClass()) && !target.getClass().isArray() && (f = target.getClass().getField(methodName)) != null) {
                        context.setCurrentType(f.getType());
                        context.setCurrentAccessor(f.getDeclaringClass());
                        return "." + f.getName();
                    }
                }
                catch (NoSuchFieldException noSuchFieldException) {
                    // empty catch block
                }
                return "";
            }
            context.setCurrentType(m.getReturnType());
            context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
            return "." + m.getName() + "()";
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        try {
            String conversion;
            String indexStr = index.toString();
            String methodName = indexStr.indexOf(34) >= 0 ? indexStr.replaceAll("\"", "") : indexStr;
            Method m = OgnlRuntime.getWriteMethod(target.getClass(), methodName);
            if (m == null && context.getCurrentObject() != null && context.getCurrentObject().toString() != null) {
                String currentObjectStr = context.getCurrentObject().toString();
                m = OgnlRuntime.getWriteMethod(target.getClass(), currentObjectStr.indexOf(34) >= 0 ? currentObjectStr.replaceAll("\"", "") : currentObjectStr);
            }
            if (m == null || m.getParameterTypes() == null || m.getParameterTypes().length <= 0) {
                throw new UnsupportedCompilationException("Unable to determine setting expression on " + context.getCurrentObject() + " with index of " + index);
            }
            Class<?> parm = m.getParameterTypes()[0];
            if (m.getParameterTypes().length > 1) {
                throw new UnsupportedCompilationException("Object property accessors can only support single parameter setters.");
            }
            if (parm.isPrimitive()) {
                Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(parm);
                conversion = OgnlRuntime.getCompiler().createLocalReference(context, "((" + wrapClass.getName() + ")ognl.OgnlOps#convertValue($3," + wrapClass.getName() + ".class, true))." + OgnlRuntime.getNumericValueGetter(wrapClass), parm);
            } else {
                conversion = parm.isArray() ? OgnlRuntime.getCompiler().createLocalReference(context, "(" + ExpressionCompiler.getCastString(parm) + ")ognl.OgnlOps#toArray($3," + parm.getComponentType().getName() + ".class)", parm) : OgnlRuntime.getCompiler().createLocalReference(context, "(" + parm.getName() + ")ognl.OgnlOps#convertValue($3," + parm.getName() + ".class)", parm);
            }
            context.setCurrentType(m.getReturnType());
            context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
            return "." + m.getName() + "(" + conversion + ")";
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }
}

