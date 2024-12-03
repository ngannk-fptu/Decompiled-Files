/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.WrapDynaClass;

public class WrapDynaBean
implements DynaBean,
Serializable {
    protected transient WrapDynaClass dynaClass = null;
    protected Object instance = null;

    public WrapDynaBean(Object instance) {
        this(instance, null);
    }

    public WrapDynaBean(Object instance, WrapDynaClass cls) {
        this.instance = instance;
        this.dynaClass = cls != null ? cls : (WrapDynaClass)this.getDynaClass();
    }

    @Override
    public boolean contains(String name, String key) {
        throw new UnsupportedOperationException("WrapDynaBean does not support contains()");
    }

    @Override
    public Object get(String name) {
        Object value = null;
        try {
            value = this.getPropertyUtils().getSimpleProperty(this.instance, name);
        }
        catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error reading property '" + name + "' nested exception - " + cause);
        }
        catch (Throwable t) {
            throw new IllegalArgumentException("Error reading property '" + name + "', exception - " + t);
        }
        return value;
    }

    @Override
    public Object get(String name, int index) {
        Object value = null;
        try {
            value = this.getPropertyUtils().getIndexedProperty(this.instance, name, index);
        }
        catch (IndexOutOfBoundsException e) {
            throw e;
        }
        catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error reading indexed property '" + name + "' nested exception - " + cause);
        }
        catch (Throwable t) {
            throw new IllegalArgumentException("Error reading indexed property '" + name + "', exception - " + t);
        }
        return value;
    }

    @Override
    public Object get(String name, String key) {
        Object value = null;
        try {
            value = this.getPropertyUtils().getMappedProperty(this.instance, name, key);
        }
        catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error reading mapped property '" + name + "' nested exception - " + cause);
        }
        catch (Throwable t) {
            throw new IllegalArgumentException("Error reading mapped property '" + name + "', exception - " + t);
        }
        return value;
    }

    @Override
    public DynaClass getDynaClass() {
        if (this.dynaClass == null) {
            this.dynaClass = WrapDynaClass.createDynaClass(this.instance.getClass());
        }
        return this.dynaClass;
    }

    @Override
    public void remove(String name, String key) {
        throw new UnsupportedOperationException("WrapDynaBean does not support remove()");
    }

    @Override
    public void set(String name, Object value) {
        try {
            this.getPropertyUtils().setSimpleProperty(this.instance, name, value);
        }
        catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error setting property '" + name + "' nested exception -" + cause);
        }
        catch (Throwable t) {
            throw new IllegalArgumentException("Error setting property '" + name + "', exception - " + t);
        }
    }

    @Override
    public void set(String name, int index, Object value) {
        try {
            this.getPropertyUtils().setIndexedProperty(this.instance, name, index, value);
        }
        catch (IndexOutOfBoundsException e) {
            throw e;
        }
        catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error setting indexed property '" + name + "' nested exception - " + cause);
        }
        catch (Throwable t) {
            throw new IllegalArgumentException("Error setting indexed property '" + name + "', exception - " + t);
        }
    }

    @Override
    public void set(String name, String key, Object value) {
        try {
            this.getPropertyUtils().setMappedProperty(this.instance, name, key, value);
        }
        catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error setting mapped property '" + name + "' nested exception - " + cause);
        }
        catch (Throwable t) {
            throw new IllegalArgumentException("Error setting mapped property '" + name + "', exception - " + t);
        }
    }

    public Object getInstance() {
        return this.instance;
    }

    protected DynaProperty getDynaProperty(String name) {
        DynaProperty descriptor = this.getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new IllegalArgumentException("Invalid property name '" + name + "'");
        }
        return descriptor;
    }

    private PropertyUtilsBean getPropertyUtils() {
        PropertyUtilsBean propUtils = null;
        if (this.dynaClass != null) {
            propUtils = this.dynaClass.getPropertyUtilsBean();
        }
        return propUtils != null ? propUtils : PropertyUtilsBean.getInstance();
    }
}

