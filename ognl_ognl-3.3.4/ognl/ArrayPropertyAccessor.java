/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Array;
import java.util.Map;
import ognl.DynamicSubscript;
import ognl.NoSuchPropertyException;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import ognl.TypeConverter;

public class ArrayPropertyAccessor
extends ObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        Object result = null;
        if (name instanceof String) {
            result = name.equals("length") ? new Integer(Array.getLength(target)) : super.getProperty(context, target, name);
        } else {
            Object index = name;
            if (index instanceof DynamicSubscript) {
                int len = Array.getLength(target);
                switch (((DynamicSubscript)index).getFlag()) {
                    case 3: {
                        result = Array.newInstance(target.getClass().getComponentType(), len);
                        System.arraycopy(target, 0, result, 0, len);
                        break;
                    }
                    case 0: {
                        index = new Integer(len > 0 ? 0 : -1);
                        break;
                    }
                    case 1: {
                        index = new Integer(len > 0 ? len / 2 : -1);
                        break;
                    }
                    case 2: {
                        index = new Integer(len > 0 ? len - 1 : -1);
                    }
                }
            }
            if (result == null) {
                if (index instanceof Number) {
                    int i = ((Number)index).intValue();
                    result = i >= 0 ? Array.get(target, i) : null;
                } else {
                    throw new NoSuchPropertyException(target, index);
                }
            }
        }
        return result;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        Object index = name;
        boolean isNumber = index instanceof Number;
        if (isNumber || index instanceof DynamicSubscript) {
            TypeConverter converter = ((OgnlContext)context).getTypeConverter();
            Object convertedValue = converter.convertValue(context, target, null, name.toString(), value, target.getClass().getComponentType());
            if (isNumber) {
                int i = ((Number)index).intValue();
                if (i >= 0) {
                    Array.set(target, i, convertedValue);
                }
            } else {
                int len = Array.getLength(target);
                switch (((DynamicSubscript)index).getFlag()) {
                    case 3: {
                        System.arraycopy(target, 0, convertedValue, 0, len);
                        return;
                    }
                    case 0: {
                        index = new Integer(len > 0 ? 0 : -1);
                        break;
                    }
                    case 1: {
                        index = new Integer(len > 0 ? len / 2 : -1);
                        break;
                    }
                    case 2: {
                        index = new Integer(len > 0 ? len - 1 : -1);
                    }
                }
            }
        } else if (name instanceof String) {
            super.setProperty(context, target, name, value);
        } else {
            throw new NoSuchPropertyException(target, index);
        }
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        String indexStr = index.toString();
        if (context.getCurrentType() != null && !context.getCurrentType().isPrimitive() && Number.class.isAssignableFrom(context.getCurrentType())) {
            indexStr = indexStr + "." + OgnlRuntime.getNumericValueGetter(context.getCurrentType());
        } else if (context.getCurrentObject() != null && Number.class.isAssignableFrom(context.getCurrentObject().getClass()) && !context.getCurrentType().isPrimitive()) {
            String toString = String.class.isInstance(index) && context.getCurrentType() != Object.class ? "" : ".toString()";
            indexStr = "ognl.OgnlOps#getIntValue(" + indexStr + toString + ")";
        }
        context.setCurrentAccessor(target.getClass());
        context.setCurrentType(target.getClass().getComponentType());
        return "[" + indexStr + "]";
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        String indexStr = index.toString();
        if (context.getCurrentType() != null && !context.getCurrentType().isPrimitive() && Number.class.isAssignableFrom(context.getCurrentType())) {
            indexStr = indexStr + "." + OgnlRuntime.getNumericValueGetter(context.getCurrentType());
        } else if (context.getCurrentObject() != null && Number.class.isAssignableFrom(context.getCurrentObject().getClass()) && !context.getCurrentType().isPrimitive()) {
            String toString = String.class.isInstance(index) && context.getCurrentType() != Object.class ? "" : ".toString()";
            indexStr = "ognl.OgnlOps#getIntValue(" + indexStr + toString + ")";
        }
        Class<?> type = target.getClass().isArray() ? target.getClass().getComponentType() : target.getClass();
        context.setCurrentAccessor(target.getClass());
        context.setCurrentType(target.getClass().getComponentType());
        if (type.isPrimitive()) {
            Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(type);
            return "[" + indexStr + "]=((" + wrapClass.getName() + ")ognl.OgnlOps.convertValue($3," + wrapClass.getName() + ".class, true))." + OgnlRuntime.getNumericValueGetter(wrapClass);
        }
        return "[" + indexStr + "]=ognl.OgnlOps.convertValue($3," + type.getName() + ".class)";
    }
}

