/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.javabeans;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.InvalidPropertyTypeException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertyImplementationException;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BeanIntrospectorPropertySet
extends AbstractPropertySet {
    private Map descriptors = new HashMap();
    private Object bean = null;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class array$B;
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$java$util$Properties;

    public void setBean(Object bean) throws PropertyImplementationException {
        this.bean = bean;
        try {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] beanDescriptors = info.getPropertyDescriptors();
            for (int i = 0; i < beanDescriptors.length; ++i) {
                PropertyDescriptor beanDescriptor = beanDescriptors[i];
                this.descriptors.put(beanDescriptor.getName(), beanDescriptor);
            }
        }
        catch (IntrospectionException e) {
            throw new PropertyImplementationException("Object is not a bean", e);
        }
    }

    public Collection getKeys(String prefix, int type) throws PropertyException {
        ArrayList<String> keys = new ArrayList<String>();
        Iterator iter = this.descriptors.values().iterator();
        while (iter.hasNext()) {
            PropertyDescriptor descriptor = (PropertyDescriptor)iter.next();
            if (prefix != null && !descriptor.getName().startsWith(prefix) || type != 0 && this.getType(descriptor.getName()) != type) continue;
            keys.add(descriptor.getName());
        }
        return keys;
    }

    public boolean isSettable(String property) {
        PropertyDescriptor descriptor = (PropertyDescriptor)this.descriptors.get(property);
        return descriptor != null && descriptor.getWriteMethod() != null;
    }

    public int getType(String key) throws PropertyException {
        PropertyDescriptor descriptor = (PropertyDescriptor)this.descriptors.get(key);
        if (descriptor == null) {
            throw new PropertyException("No key " + key + " found");
        }
        Class<?> c = descriptor.getPropertyType();
        if (c == Integer.TYPE || c == (class$java$lang$Integer == null ? (class$java$lang$Integer = BeanIntrospectorPropertySet.class$("java.lang.Integer")) : class$java$lang$Integer)) {
            return 2;
        }
        if (c == Long.TYPE || c == (class$java$lang$Long == null ? (class$java$lang$Long = BeanIntrospectorPropertySet.class$("java.lang.Long")) : class$java$lang$Long)) {
            return 3;
        }
        if (c == Double.TYPE || c == (class$java$lang$Double == null ? (class$java$lang$Double = BeanIntrospectorPropertySet.class$("java.lang.Double")) : class$java$lang$Double)) {
            return 4;
        }
        if (c == (class$java$lang$String == null ? (class$java$lang$String = BeanIntrospectorPropertySet.class$("java.lang.String")) : class$java$lang$String)) {
            return 5;
        }
        if (c == Boolean.TYPE || c == (class$java$lang$Boolean == null ? (class$java$lang$Boolean = BeanIntrospectorPropertySet.class$("java.lang.Boolean")) : class$java$lang$Boolean)) {
            return 1;
        }
        if (c == (array$B == null ? (array$B = BeanIntrospectorPropertySet.class$("[B")) : array$B)) {
            return 10;
        }
        if ((class$java$util$Date == null ? (class$java$util$Date = BeanIntrospectorPropertySet.class$("java.util.Date")) : class$java$util$Date).isAssignableFrom(c)) {
            return 7;
        }
        if ((class$java$util$Properties == null ? (class$java$util$Properties = BeanIntrospectorPropertySet.class$("java.util.Properties")) : class$java$util$Properties).isAssignableFrom(c)) {
            return 11;
        }
        return 8;
    }

    public boolean exists(String key) throws PropertyException {
        return this.descriptors.get(key) != null;
    }

    public void init(Map config, Map args) {
        Object bean = args.get("bean");
        this.setBean(bean);
    }

    public void remove(String key) throws PropertyException {
        throw new PropertyImplementationException("Remove not supported in BeanIntrospectorPropertySet, use setXXX(null) instead");
    }

    protected void setImpl(int type, String key, Object value) throws PropertyException {
        if (this.getType(key) != type) {
            throw new InvalidPropertyTypeException(key + " is not of type " + type);
        }
        PropertyDescriptor descriptor = (PropertyDescriptor)this.descriptors.get(key);
        try {
            Object result = descriptor.getWriteMethod().invoke(this.bean, value);
        }
        catch (NullPointerException ex) {
            throw new PropertyImplementationException("Property " + key + " is read-only");
        }
        catch (Exception ex) {
            throw new PropertyImplementationException("Cannot invoke write method for key " + key, ex);
        }
    }

    protected Object get(int type, String key) throws PropertyException {
        if (this.getType(key) != type) {
            throw new InvalidPropertyTypeException(key + " is not of type " + type);
        }
        PropertyDescriptor descriptor = (PropertyDescriptor)this.descriptors.get(key);
        try {
            Object result = descriptor.getReadMethod().invoke(this.bean, new Object[0]);
            return result;
        }
        catch (NullPointerException ex) {
            throw new PropertyImplementationException("Property " + key + " is write-only");
        }
        catch (Exception ex) {
            throw new PropertyImplementationException("Cannot invoke read method for key " + key, ex);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

