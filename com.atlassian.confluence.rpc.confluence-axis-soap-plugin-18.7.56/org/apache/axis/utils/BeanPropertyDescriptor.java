/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class BeanPropertyDescriptor {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$BeanPropertyDescriptor == null ? (class$org$apache$axis$utils$BeanPropertyDescriptor = BeanPropertyDescriptor.class$("org.apache.axis.utils.BeanPropertyDescriptor")) : class$org$apache$axis$utils$BeanPropertyDescriptor).getName());
    protected PropertyDescriptor myPD = null;
    protected static final Object[] noArgs = new Object[0];
    static /* synthetic */ Class class$org$apache$axis$utils$BeanPropertyDescriptor;

    public BeanPropertyDescriptor(PropertyDescriptor pd) {
        this.myPD = pd;
    }

    protected BeanPropertyDescriptor() {
    }

    public String getName() {
        return this.myPD.getName();
    }

    public boolean isReadable() {
        return this.myPD.getReadMethod() != null;
    }

    public boolean isWriteable() {
        return this.myPD.getWriteMethod() != null;
    }

    public boolean isIndexed() {
        return this.myPD instanceof IndexedPropertyDescriptor;
    }

    public boolean isIndexedOrArray() {
        return this.isIndexed() || this.isArray();
    }

    public boolean isArray() {
        return this.myPD.getPropertyType() != null && this.myPD.getPropertyType().isArray();
    }

    public Object get(Object obj) throws InvocationTargetException, IllegalAccessException {
        Method readMethod = this.myPD.getReadMethod();
        if (readMethod != null) {
            return readMethod.invoke(obj, noArgs);
        }
        throw new IllegalAccessException(Messages.getMessage("badGetter00"));
    }

    public void set(Object obj, Object newValue) throws InvocationTargetException, IllegalAccessException {
        Method writeMethod = this.myPD.getWriteMethod();
        if (writeMethod == null) {
            throw new IllegalAccessException(Messages.getMessage("badSetter00"));
        }
        writeMethod.invoke(obj, newValue);
    }

    public Object get(Object obj, int i) throws InvocationTargetException, IllegalAccessException {
        if (!this.isIndexed()) {
            return Array.get(this.get(obj), i);
        }
        IndexedPropertyDescriptor id = (IndexedPropertyDescriptor)this.myPD;
        return id.getIndexedReadMethod().invoke(obj, new Integer(i));
    }

    public void set(Object obj, int i, Object newValue) throws InvocationTargetException, IllegalAccessException {
        if (this.isIndexed()) {
            IndexedPropertyDescriptor id = (IndexedPropertyDescriptor)this.myPD;
            this.growArrayToSize(obj, id.getIndexedPropertyType(), i);
            id.getIndexedWriteMethod().invoke(obj, new Integer(i), newValue);
        } else {
            this.growArrayToSize(obj, this.myPD.getPropertyType().getComponentType(), i);
            Array.set(this.get(obj), i, newValue);
        }
    }

    protected void growArrayToSize(Object obj, Class componentType, int i) throws InvocationTargetException, IllegalAccessException {
        Object array = this.get(obj);
        if (array == null || Array.getLength(array) <= i) {
            Object newArray = Array.newInstance(componentType, i + 1);
            if (array != null) {
                System.arraycopy(array, 0, newArray, 0, Array.getLength(array));
            }
            this.set(obj, newArray);
        }
    }

    public Class getType() {
        if (this.isIndexed()) {
            return ((IndexedPropertyDescriptor)this.myPD).getIndexedPropertyType();
        }
        return this.myPD.getPropertyType();
    }

    public Class getActualType() {
        return this.myPD.getPropertyType();
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

