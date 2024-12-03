/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

public class BasicDynaClass
implements DynaClass,
Serializable {
    protected transient Constructor<?> constructor = null;
    protected static Class<?>[] constructorTypes = new Class[]{DynaClass.class};
    protected Object[] constructorValues = new Object[]{this};
    protected Class<?> dynaBeanClass = BasicDynaBean.class;
    protected String name = this.getClass().getName();
    protected DynaProperty[] properties = new DynaProperty[0];
    protected HashMap<String, DynaProperty> propertiesMap = new HashMap();

    public BasicDynaClass() {
        this(null, null, null);
    }

    public BasicDynaClass(String name, Class<?> dynaBeanClass) {
        this(name, dynaBeanClass, null);
    }

    public BasicDynaClass(String name, Class<?> dynaBeanClass, DynaProperty[] properties) {
        if (name != null) {
            this.name = name;
        }
        if (dynaBeanClass == null) {
            dynaBeanClass = BasicDynaBean.class;
        }
        this.setDynaBeanClass(dynaBeanClass);
        if (properties != null) {
            this.setProperties(properties);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DynaProperty getDynaProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        return this.propertiesMap.get(name);
    }

    @Override
    public DynaProperty[] getDynaProperties() {
        return this.properties;
    }

    @Override
    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        try {
            if (this.constructor == null) {
                this.setDynaBeanClass(this.dynaBeanClass);
            }
            return (DynaBean)this.constructor.newInstance(this.constructorValues);
        }
        catch (InvocationTargetException e) {
            throw new InstantiationException(e.getTargetException().getMessage());
        }
    }

    public Class<?> getDynaBeanClass() {
        return this.dynaBeanClass;
    }

    protected void setDynaBeanClass(Class<?> dynaBeanClass) {
        if (dynaBeanClass.isInterface()) {
            throw new IllegalArgumentException("Class " + dynaBeanClass.getName() + " is an interface, not a class");
        }
        if (!DynaBean.class.isAssignableFrom(dynaBeanClass)) {
            throw new IllegalArgumentException("Class " + dynaBeanClass.getName() + " does not implement DynaBean");
        }
        try {
            this.constructor = dynaBeanClass.getConstructor(constructorTypes);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + dynaBeanClass.getName() + " does not have an appropriate constructor");
        }
        this.dynaBeanClass = dynaBeanClass;
    }

    protected void setProperties(DynaProperty[] properties) {
        this.properties = properties;
        this.propertiesMap.clear();
        for (DynaProperty propertie : properties) {
            this.propertiesMap.put(propertie.getName(), propertie);
        }
    }
}

