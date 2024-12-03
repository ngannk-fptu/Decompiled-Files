/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.impl;

import com.sun.syndication.feed.impl.BeanIntrospector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class EqualsBean
implements Serializable {
    private static final Object[] NO_PARAMS = new Object[0];
    private Class _beanClass;
    private Object _obj;
    static /* synthetic */ Class class$java$lang$Object;

    protected EqualsBean(Class beanClass) {
        this._beanClass = beanClass;
        this._obj = this;
    }

    public EqualsBean(Class beanClass, Object obj) {
        if (!beanClass.isInstance(obj)) {
            throw new IllegalArgumentException(obj.getClass() + " is not instance of " + beanClass);
        }
        this._beanClass = beanClass;
        this._obj = obj;
    }

    public boolean equals(Object obj) {
        return this.beanEquals(obj);
    }

    public boolean beanEquals(Object obj) {
        boolean eq;
        Object bean1 = this._obj;
        Object bean2 = obj;
        if (bean2 == null) {
            eq = false;
        } else if (bean1 == null && bean2 == null) {
            eq = true;
        } else if (bean1 == null || bean2 == null) {
            eq = false;
        } else if (!this._beanClass.isInstance(bean2)) {
            eq = false;
        } else {
            eq = true;
            try {
                PropertyDescriptor[] pds = BeanIntrospector.getPropertyDescriptors(this._beanClass);
                if (pds != null) {
                    for (int i = 0; eq && i < pds.length; ++i) {
                        Method pReadMethod = pds[i].getReadMethod();
                        if (pReadMethod == null || pReadMethod.getDeclaringClass() == (class$java$lang$Object == null ? EqualsBean.class$("java.lang.Object") : class$java$lang$Object) || pReadMethod.getParameterTypes().length != 0) continue;
                        Object value1 = pReadMethod.invoke(bean1, NO_PARAMS);
                        Object value2 = pReadMethod.invoke(bean2, NO_PARAMS);
                        eq = this.doEquals(value1, value2);
                    }
                }
            }
            catch (Exception ex) {
                throw new RuntimeException("Could not execute equals()", ex);
            }
        }
        return eq;
    }

    public int hashCode() {
        return this.beanHashCode();
    }

    public int beanHashCode() {
        return this._obj.toString().hashCode();
    }

    private boolean doEquals(Object obj1, Object obj2) {
        boolean eq;
        boolean bl = eq = obj1 == obj2;
        if (!eq && obj1 != null && obj2 != null) {
            Class<?> classObj1 = obj1.getClass();
            Class<?> classObj2 = obj2.getClass();
            eq = classObj1.isArray() && classObj2.isArray() ? this.equalsArray(obj1, obj2) : obj1.equals(obj2);
        }
        return eq;
    }

    private boolean equalsArray(Object array1, Object array2) {
        boolean eq;
        int length2;
        int length1 = Array.getLength(array1);
        if (length1 == (length2 = Array.getLength(array2))) {
            eq = true;
            for (int i = 0; eq && i < length1; ++i) {
                Object e1 = Array.get(array1, i);
                Object e2 = Array.get(array2, i);
                eq = this.doEquals(e1, e2);
            }
        } else {
            eq = false;
        }
        return eq;
    }
}

