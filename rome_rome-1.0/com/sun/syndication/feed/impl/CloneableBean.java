/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.impl;

import com.sun.syndication.feed.impl.BeanIntrospector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CloneableBean
implements Serializable,
Cloneable {
    private static final Class[] NO_PARAMS_DEF = new Class[0];
    private static final Object[] NO_PARAMS = new Object[0];
    private Object _obj;
    private Set _ignoreProperties;
    private static final Set BASIC_TYPES = new HashSet();
    private static final Map CONSTRUCTOR_BASIC_TYPES;
    static /* synthetic */ Class class$java$lang$Object;

    protected CloneableBean() {
        this._obj = this;
    }

    public CloneableBean(Object obj) {
        this(obj, null);
    }

    public CloneableBean(Object obj, Set ignoreProperties) {
        this._obj = obj;
        this._ignoreProperties = ignoreProperties != null ? ignoreProperties : Collections.EMPTY_SET;
    }

    public Object clone() throws CloneNotSupportedException {
        return this.beanClone();
    }

    public Object beanClone() throws CloneNotSupportedException {
        Object clonedBean;
        try {
            clonedBean = this._obj.getClass().newInstance();
            PropertyDescriptor[] pds = BeanIntrospector.getPropertyDescriptors(this._obj.getClass());
            if (pds != null) {
                for (int i = 0; i < pds.length; ++i) {
                    Object value;
                    Method pReadMethod = pds[i].getReadMethod();
                    Method pWriteMethod = pds[i].getWriteMethod();
                    if (pReadMethod == null || pWriteMethod == null || this._ignoreProperties.contains(pds[i].getName()) || pReadMethod.getDeclaringClass() == (class$java$lang$Object == null ? CloneableBean.class$("java.lang.Object") : class$java$lang$Object) || pReadMethod.getParameterTypes().length != 0 || (value = pReadMethod.invoke(this._obj, NO_PARAMS)) == null) continue;
                    value = this.doClone(value);
                    pWriteMethod.invoke(clonedBean, value);
                }
            }
        }
        catch (CloneNotSupportedException cnsEx) {
            throw cnsEx;
        }
        catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace(System.out);
            throw new CloneNotSupportedException("Cannot clone a " + this._obj.getClass() + " object");
        }
        return clonedBean;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Object doClone(Object value) throws Exception {
        if (value == null) return value;
        Class<?> vClass = value.getClass();
        if (vClass.isArray()) {
            return this.cloneArray(value);
        }
        if (value instanceof Collection) {
            return this.cloneCollection((Collection)value);
        }
        if (value instanceof Map) {
            return this.cloneMap((Map)value);
        }
        if (this.isBasicType(vClass)) return value;
        if (!(value instanceof Cloneable)) throw new CloneNotSupportedException("Cannot clone a " + vClass.getName() + " object");
        Method cloneMethod = vClass.getMethod("clone", NO_PARAMS_DEF);
        if (!Modifier.isPublic(cloneMethod.getModifiers())) throw new CloneNotSupportedException("Cannot clone a " + value.getClass() + " object, clone() is not public");
        return cloneMethod.invoke(value, NO_PARAMS);
    }

    private Object cloneArray(Object array) throws Exception {
        Class<?> elementClass = array.getClass().getComponentType();
        int length = Array.getLength(array);
        Object newArray = Array.newInstance(elementClass, length);
        for (int i = 0; i < length; ++i) {
            Object element = this.doClone(Array.get(array, i));
            Array.set(newArray, i, element);
        }
        return newArray;
    }

    private Object cloneCollection(Collection collection) throws Exception {
        Class<?> mClass = collection.getClass();
        Collection newColl = (Collection)mClass.newInstance();
        Iterator i = collection.iterator();
        while (i.hasNext()) {
            Object element = this.doClone(i.next());
            newColl.add(element);
        }
        return newColl;
    }

    private Object cloneMap(Map map) throws Exception {
        Class<?> mClass = map.getClass();
        Map newMap = (Map)mClass.newInstance();
        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = entries.next();
            Object key = this.doClone(entry.getKey());
            Object value = this.doClone(entry.getValue());
            newMap.put(key, value);
        }
        return newMap;
    }

    private boolean isBasicType(Class vClass) {
        return BASIC_TYPES.contains(vClass);
    }

    static {
        BASIC_TYPES.add(Boolean.class);
        BASIC_TYPES.add(Byte.class);
        BASIC_TYPES.add(Character.class);
        BASIC_TYPES.add(Double.class);
        BASIC_TYPES.add(Float.class);
        BASIC_TYPES.add(Integer.class);
        BASIC_TYPES.add(Long.class);
        BASIC_TYPES.add(Short.class);
        BASIC_TYPES.add(String.class);
        CONSTRUCTOR_BASIC_TYPES = new HashMap();
        CONSTRUCTOR_BASIC_TYPES.put(Boolean.class, new Class[]{Boolean.TYPE});
        CONSTRUCTOR_BASIC_TYPES.put(Byte.class, new Class[]{Byte.TYPE});
        CONSTRUCTOR_BASIC_TYPES.put(Character.class, new Class[]{Character.TYPE});
        CONSTRUCTOR_BASIC_TYPES.put(Double.class, new Class[]{Double.TYPE});
        CONSTRUCTOR_BASIC_TYPES.put(Float.class, new Class[]{Float.TYPE});
        CONSTRUCTOR_BASIC_TYPES.put(Integer.class, new Class[]{Integer.TYPE});
        CONSTRUCTOR_BASIC_TYPES.put(Long.class, new Class[]{Long.TYPE});
        CONSTRUCTOR_BASIC_TYPES.put(Short.class, new Class[]{Short.TYPE});
        CONSTRUCTOR_BASIC_TYPES.put(String.class, new Class[]{String.class});
    }
}

