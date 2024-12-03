/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.rometools.rome.feed.impl;

import com.rometools.rome.feed.impl.BeanIntrospector;
import com.rometools.rome.feed.impl.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToStringBean {
    private static final Logger LOG = LoggerFactory.getLogger(ToStringBean.class);
    private static final ThreadLocal<Stack<String[]>> PREFIX_TL = new ThreadLocal();
    private static final Object[] NO_PARAMS = new Object[0];

    private ToStringBean() {
    }

    public static String toString(Class<?> beanClass, Object obj) {
        String prefix;
        String[] tsInfo;
        Stack<Object> stack = PREFIX_TL.get();
        boolean needStackCleanup = false;
        if (stack == null) {
            stack = new Stack();
            PREFIX_TL.set(stack);
            needStackCleanup = true;
        }
        if ((tsInfo = stack.isEmpty() ? null : (String[])stack.peek()) == null) {
            String className = obj.getClass().getName();
            prefix = className.substring(className.lastIndexOf(".") + 1);
        } else {
            tsInfo[1] = prefix = tsInfo[0];
        }
        String result = ToStringBean.toString(beanClass, obj, prefix);
        if (needStackCleanup) {
            PREFIX_TL.remove();
        }
        return result;
    }

    private static String toString(Class<?> beanClass, Object obj, String prefix) {
        StringBuffer sb = new StringBuffer(128);
        try {
            List<PropertyDescriptor> propertyDescriptors = BeanIntrospector.getPropertyDescriptorsWithGetters(beanClass);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String propertyName = propertyDescriptor.getName();
                Method getter = propertyDescriptor.getReadMethod();
                Object value = getter.invoke(obj, NO_PARAMS);
                ToStringBean.printProperty(sb, prefix + "." + propertyName, value);
            }
        }
        catch (Exception e) {
            LOG.error("Error while generating toString", (Throwable)e);
            Class<?> clazz = obj.getClass();
            String errorMessage = e.getMessage();
            sb.append(String.format("\n\nEXCEPTION: Could not complete %s.toString(): %s\n", clazz, errorMessage));
        }
        return sb.toString();
    }

    private static void printProperty(StringBuffer sb, String prefix, Object value) {
        if (value == null) {
            sb.append(prefix).append("=null\n");
        } else if (value.getClass().isArray()) {
            ToStringBean.printArrayProperty(sb, prefix, value);
        } else if (value instanceof Map) {
            Map map = (Map)value;
            Set entries = map.entrySet();
            if (entries.isEmpty()) {
                sb.append(prefix).append("=[]\n");
            } else {
                for (Map.Entry entry : entries) {
                    Object eKey = entry.getKey();
                    Object eValue = entry.getValue();
                    String ePrefix = String.format("%s[%s]", prefix, eKey);
                    String[] tsInfo = new String[2];
                    tsInfo[0] = ePrefix;
                    Stack<String[]> stack = PREFIX_TL.get();
                    stack.push(tsInfo);
                    String s = eValue == null ? "null" : eValue.toString();
                    stack.pop();
                    if (tsInfo[1] == null) {
                        sb.append(ePrefix).append("=").append(s).append("\n");
                        continue;
                    }
                    sb.append(s);
                }
            }
        } else if (value instanceof Collection) {
            Collection collection = (Collection)value;
            if (collection.isEmpty()) {
                sb.append(prefix).append("=[]\n");
            } else {
                int c = 0;
                for (Object cValue : collection) {
                    String cPrefix = String.format("%s[%s]", prefix, c++);
                    String[] tsInfo = new String[2];
                    tsInfo[0] = cPrefix;
                    Stack<String[]> stack = PREFIX_TL.get();
                    stack.push(tsInfo);
                    String s = cValue == null ? "null" : cValue.toString();
                    stack.pop();
                    if (tsInfo[1] == null) {
                        sb.append(cPrefix).append("=").append(s).append("\n");
                        continue;
                    }
                    sb.append(s);
                }
            }
        } else {
            String[] tsInfo = new String[2];
            tsInfo[0] = prefix;
            Stack<String[]> stack = PREFIX_TL.get();
            stack.push(tsInfo);
            String s = value.toString();
            stack.pop();
            if (tsInfo[1] == null) {
                sb.append(prefix).append("=").append(s).append("\n");
            } else {
                sb.append(s);
            }
        }
    }

    private static void printArrayProperty(StringBuffer sb, String prefix, Object array) {
        int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            Object obj = Array.get(array, i);
            ToStringBean.printProperty(sb, String.format("%s[%s]", prefix, i), obj);
        }
    }
}

