/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.impl;

import com.rometools.rome.feed.impl.BeanIntrospector;
import com.rometools.rome.feed.impl.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class EqualsBean {
    private static final Object[] NO_PARAMS = new Object[0];

    private EqualsBean() {
    }

    public static boolean beanEquals(Class<?> beanClass, Object obj1, Object obj2) {
        boolean eq;
        if (obj1 == null && obj2 == null) {
            eq = true;
        } else if (obj1 == null || obj2 == null) {
            eq = false;
        } else if (!beanClass.isInstance(obj2)) {
            eq = false;
        } else {
            eq = true;
            try {
                Object value2;
                PropertyDescriptor propertyDescriptor;
                Method getter;
                Object value1;
                List<PropertyDescriptor> propertyDescriptors = BeanIntrospector.getPropertyDescriptorsWithGetters(beanClass);
                Iterator<PropertyDescriptor> iterator = propertyDescriptors.iterator();
                while (iterator.hasNext() && (eq = EqualsBean.doEquals(value1 = (getter = (propertyDescriptor = iterator.next()).getReadMethod()).invoke(obj1, NO_PARAMS), value2 = getter.invoke(obj2, NO_PARAMS)))) {
                }
            }
            catch (Exception ex) {
                throw new RuntimeException("Could not execute equals()", ex);
            }
        }
        return eq;
    }

    public static int beanHashCode(Object obj) {
        return obj.toString().hashCode();
    }

    private static boolean doEquals(Object obj1, Object obj2) {
        boolean eq;
        boolean bl = eq = obj1 == obj2;
        if (!eq && obj1 != null && obj2 != null) {
            Class<?> classObj1 = obj1.getClass();
            Class<?> classObj2 = obj2.getClass();
            eq = classObj1.isArray() && classObj2.isArray() ? EqualsBean.equalsArray(obj1, obj2) : obj1.equals(obj2);
        }
        return eq;
    }

    private static boolean equalsArray(Object array1, Object array2) {
        boolean eq;
        int length2;
        int length1 = Array.getLength(array1);
        if (length1 == (length2 = Array.getLength(array2))) {
            eq = true;
            for (int i = 0; eq && i < length1; ++i) {
                Object e1 = Array.get(array1, i);
                Object e2 = Array.get(array2, i);
                eq = EqualsBean.doEquals(e1, e2);
            }
        } else {
            eq = false;
        }
        return eq;
    }
}

