/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ognl.DynamicSubscript;
import ognl.NoSuchPropertyException;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

public class ListPropertyAccessor
extends ObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        List list = (List)target;
        if (name instanceof String) {
            Object result = null;
            result = name.equals("size") ? new Integer(list.size()) : (name.equals("iterator") ? list.iterator() : (name.equals("isEmpty") || name.equals("empty") ? (list.isEmpty() ? Boolean.TRUE : Boolean.FALSE) : super.getProperty(context, target, name)));
            return result;
        }
        if (name instanceof Number) {
            return list.get(((Number)name).intValue());
        }
        if (name instanceof DynamicSubscript) {
            int len = list.size();
            switch (((DynamicSubscript)name).getFlag()) {
                case 0: {
                    return len > 0 ? list.get(0) : null;
                }
                case 1: {
                    return len > 0 ? list.get(len / 2) : null;
                }
                case 2: {
                    return len > 0 ? list.get(len - 1) : null;
                }
                case 3: {
                    return new ArrayList(list);
                }
            }
        }
        throw new NoSuchPropertyException(target, name);
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        if (name instanceof String && ((String)name).indexOf("$") < 0) {
            super.setProperty(context, target, name, value);
            return;
        }
        List list = (List)target;
        if (name instanceof Number) {
            list.set(((Number)name).intValue(), value);
            return;
        }
        if (name instanceof DynamicSubscript) {
            int len = list.size();
            switch (((DynamicSubscript)name).getFlag()) {
                case 0: {
                    if (len > 0) {
                        list.set(0, value);
                    }
                    return;
                }
                case 1: {
                    if (len > 0) {
                        list.set(len / 2, value);
                    }
                    return;
                }
                case 2: {
                    if (len > 0) {
                        list.set(len - 1, value);
                    }
                    return;
                }
                case 3: {
                    if (!(value instanceof Collection)) {
                        throw new OgnlException("Value must be a collection");
                    }
                    list.clear();
                    list.addAll((Collection)value);
                    return;
                }
            }
        }
        throw new NoSuchPropertyException(target, name);
    }

    @Override
    public Class getPropertyClass(OgnlContext context, Object target, Object index) {
        if (index instanceof String) {
            String key;
            String indexStr = (String)index;
            String string = key = indexStr.indexOf(34) >= 0 ? indexStr.replaceAll("\"", "") : indexStr;
            if (key.equals("size")) {
                return Integer.TYPE;
            }
            if (key.equals("iterator")) {
                return Iterator.class;
            }
            if (key.equals("isEmpty") || key.equals("empty")) {
                return Boolean.TYPE;
            }
            return super.getPropertyClass(context, target, index);
        }
        if (index instanceof Number) {
            return Object.class;
        }
        return null;
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        String indexStr = index.toString();
        if (indexStr.indexOf(34) >= 0) {
            indexStr = indexStr.replaceAll("\"", "");
        }
        if (String.class.isInstance(index)) {
            if (indexStr.equals("size")) {
                context.setCurrentAccessor(List.class);
                context.setCurrentType(Integer.TYPE);
                return ".size()";
            }
            if (indexStr.equals("iterator")) {
                context.setCurrentAccessor(List.class);
                context.setCurrentType(Iterator.class);
                return ".iterator()";
            }
            if (indexStr.equals("isEmpty") || indexStr.equals("empty")) {
                context.setCurrentAccessor(List.class);
                context.setCurrentType(Boolean.TYPE);
                return ".isEmpty()";
            }
        }
        if (context.getCurrentObject() != null && !Number.class.isInstance(context.getCurrentObject())) {
            try {
                Method m = OgnlRuntime.getReadMethod(target.getClass(), indexStr);
                if (m != null) {
                    return super.getSourceAccessor(context, target, index);
                }
            }
            catch (Throwable t) {
                throw OgnlOps.castToRuntime(t);
            }
        }
        context.setCurrentAccessor(List.class);
        if (!context.getCurrentType().isPrimitive() && Number.class.isAssignableFrom(context.getCurrentType())) {
            indexStr = indexStr + "." + OgnlRuntime.getNumericValueGetter(context.getCurrentType());
        } else if (context.getCurrentObject() != null && Number.class.isAssignableFrom(context.getCurrentObject().getClass()) && !context.getCurrentType().isPrimitive()) {
            String toString = String.class.isInstance(index) && context.getCurrentType() != Object.class ? "" : ".toString()";
            indexStr = "ognl.OgnlOps#getIntValue(" + indexStr + toString + ")";
        }
        context.setCurrentType(Object.class);
        return ".get(" + indexStr + ")";
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        String indexStr = index.toString();
        if (indexStr.indexOf(34) >= 0) {
            indexStr = indexStr.replaceAll("\"", "");
        }
        if (context.getCurrentObject() != null && !Number.class.isInstance(context.getCurrentObject())) {
            try {
                Method m = OgnlRuntime.getWriteMethod(target.getClass(), indexStr);
                if (m != null || !context.getCurrentType().isPrimitive()) {
                    return super.getSourceSetter(context, target, index);
                }
            }
            catch (Throwable t) {
                throw OgnlOps.castToRuntime(t);
            }
        }
        context.setCurrentAccessor(List.class);
        if (!context.getCurrentType().isPrimitive() && Number.class.isAssignableFrom(context.getCurrentType())) {
            indexStr = indexStr + "." + OgnlRuntime.getNumericValueGetter(context.getCurrentType());
        } else if (context.getCurrentObject() != null && Number.class.isAssignableFrom(context.getCurrentObject().getClass()) && !context.getCurrentType().isPrimitive()) {
            String toString = String.class.isInstance(index) && context.getCurrentType() != Object.class ? "" : ".toString()";
            indexStr = "ognl.OgnlOps#getIntValue(" + indexStr + toString + ")";
        }
        context.setCurrentType(Object.class);
        return ".set(" + indexStr + ", $3)";
    }
}

