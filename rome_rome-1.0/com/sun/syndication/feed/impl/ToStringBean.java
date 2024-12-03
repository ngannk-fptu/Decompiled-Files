/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.impl;

import com.sun.syndication.feed.impl.BeanIntrospector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class ToStringBean
implements Serializable {
    private static final ThreadLocal PREFIX_TL = new ThreadLocal(){

        public Object get() {
            Object o = super.get();
            if (o == null) {
                o = new Stack();
                this.set(o);
            }
            return o;
        }
    };
    private static final Object[] NO_PARAMS = new Object[0];
    private Class _beanClass;
    private Object _obj;
    static /* synthetic */ Class class$java$lang$Object;

    protected ToStringBean(Class beanClass) {
        this._beanClass = beanClass;
        this._obj = this;
    }

    public ToStringBean(Class beanClass, Object obj) {
        this._beanClass = beanClass;
        this._obj = obj;
    }

    public String toString() {
        String prefix;
        Stack stack = (Stack)PREFIX_TL.get();
        String[] tsInfo = stack.isEmpty() ? null : stack.peek();
        if (tsInfo == null) {
            String className = this._obj.getClass().getName();
            prefix = className.substring(className.lastIndexOf(".") + 1);
        } else {
            tsInfo[1] = prefix = tsInfo[0];
        }
        return this.toString(prefix);
    }

    private String toString(String prefix) {
        StringBuffer sb = new StringBuffer(128);
        try {
            PropertyDescriptor[] pds = BeanIntrospector.getPropertyDescriptors(this._beanClass);
            if (pds != null) {
                for (int i = 0; i < pds.length; ++i) {
                    String pName = pds[i].getName();
                    Method pReadMethod = pds[i].getReadMethod();
                    if (pReadMethod == null || pReadMethod.getDeclaringClass() == (class$java$lang$Object == null ? ToStringBean.class$("java.lang.Object") : class$java$lang$Object) || pReadMethod.getParameterTypes().length != 0) continue;
                    Object value = pReadMethod.invoke(this._obj, NO_PARAMS);
                    this.printProperty(sb, prefix + "." + pName, value);
                }
            }
        }
        catch (Exception ex) {
            sb.append("\n\nEXCEPTION: Could not complete " + this._obj.getClass() + ".toString(): " + ex.getMessage() + "\n");
        }
        return sb.toString();
    }

    private void printProperty(StringBuffer sb, String prefix, Object value) {
        if (value == null) {
            sb.append(prefix).append("=null\n");
        } else if (value.getClass().isArray()) {
            this.printArrayProperty(sb, prefix, value);
        } else if (value instanceof Map) {
            Map map = (Map)value;
            Iterator i = map.entrySet().iterator();
            if (i.hasNext()) {
                while (i.hasNext()) {
                    Map.Entry me = i.next();
                    String ePrefix = prefix + "[" + me.getKey() + "]";
                    Object eValue = me.getValue();
                    String[] tsInfo = new String[2];
                    tsInfo[0] = ePrefix;
                    Stack stack = (Stack)PREFIX_TL.get();
                    stack.push(tsInfo);
                    String s = eValue != null ? eValue.toString() : "null";
                    stack.pop();
                    if (tsInfo[1] == null) {
                        sb.append(ePrefix).append("=").append(s).append("\n");
                        continue;
                    }
                    sb.append(s);
                }
            } else {
                sb.append(prefix).append("=[]\n");
            }
        } else if (value instanceof Collection) {
            Collection collection = (Collection)value;
            Iterator i = collection.iterator();
            if (i.hasNext()) {
                int c = 0;
                while (i.hasNext()) {
                    String cPrefix = prefix + "[" + c++ + "]";
                    Object cValue = i.next();
                    String[] tsInfo = new String[2];
                    tsInfo[0] = cPrefix;
                    Stack stack = (Stack)PREFIX_TL.get();
                    stack.push(tsInfo);
                    String s = cValue != null ? cValue.toString() : "null";
                    stack.pop();
                    if (tsInfo[1] == null) {
                        sb.append(cPrefix).append("=").append(s).append("\n");
                        continue;
                    }
                    sb.append(s);
                }
            } else {
                sb.append(prefix).append("=[]\n");
            }
        } else {
            String[] tsInfo = new String[2];
            tsInfo[0] = prefix;
            Stack stack = (Stack)PREFIX_TL.get();
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

    private void printArrayProperty(StringBuffer sb, String prefix, Object array) {
        int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            Object obj = Array.get(array, i);
            this.printProperty(sb, prefix + "[" + i + "]", obj);
        }
    }
}

