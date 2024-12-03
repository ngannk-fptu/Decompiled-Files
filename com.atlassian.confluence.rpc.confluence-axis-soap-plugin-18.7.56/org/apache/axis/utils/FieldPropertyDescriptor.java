/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.Messages;

public class FieldPropertyDescriptor
extends BeanPropertyDescriptor {
    private Field field = null;

    public FieldPropertyDescriptor(String _name, Field _field) {
        this.field = _field;
        try {
            this.myPD = new PropertyDescriptor(_name, null, null);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (_field == null || _name == null) {
            throw new IllegalArgumentException(Messages.getMessage(_field == null ? "badField00" : "badProp03"));
        }
    }

    public String getName() {
        return this.field.getName();
    }

    public boolean isReadable() {
        return true;
    }

    public boolean isWriteable() {
        return true;
    }

    public boolean isIndexed() {
        return this.field.getType().getComponentType() != null;
    }

    public Object get(Object obj) throws InvocationTargetException, IllegalAccessException {
        return this.field.get(obj);
    }

    public void set(Object obj, Object newValue) throws InvocationTargetException, IllegalAccessException {
        this.field.set(obj, newValue);
    }

    public Object get(Object obj, int i) throws InvocationTargetException, IllegalAccessException {
        if (!this.isIndexed()) {
            throw new IllegalAccessException("Not an indexed property");
        }
        Object array = this.field.get(obj);
        return Array.get(array, i);
    }

    public void set(Object obj, int i, Object newValue) throws InvocationTargetException, IllegalAccessException {
        if (!this.isIndexed()) {
            throw new IllegalAccessException("Not an indexed field!");
        }
        Class<?> componentType = this.field.getType().getComponentType();
        this.growArrayToSize(obj, componentType, i);
        Array.set(this.get(obj), i, newValue);
    }

    public Class getType() {
        if (this.isIndexed()) {
            return this.field.getType().getComponentType();
        }
        return this.field.getType();
    }

    public Class getActualType() {
        return this.field.getType();
    }

    public Field getField() {
        return this.field;
    }
}

