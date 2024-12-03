/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.MutableDynaClass;

public class LazyDynaClass
extends BasicDynaClass
implements MutableDynaClass {
    protected boolean restricted;
    protected boolean returnNull = false;

    public LazyDynaClass() {
        this(null, (DynaProperty[])null);
    }

    public LazyDynaClass(String name) {
        this(name, (DynaProperty[])null);
    }

    public LazyDynaClass(String name, Class<?> dynaBeanClass) {
        this(name, dynaBeanClass, null);
    }

    public LazyDynaClass(String name, DynaProperty[] properties) {
        this(name, LazyDynaBean.class, properties);
    }

    public LazyDynaClass(String name, Class<?> dynaBeanClass, DynaProperty[] properties) {
        super(name, dynaBeanClass, properties);
    }

    @Override
    public boolean isRestricted() {
        return this.restricted;
    }

    @Override
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public boolean isReturnNull() {
        return this.returnNull;
    }

    public void setReturnNull(boolean returnNull) {
        this.returnNull = returnNull;
    }

    @Override
    public void add(String name) {
        this.add(new DynaProperty(name));
    }

    @Override
    public void add(String name, Class<?> type) {
        if (type == null) {
            this.add(name);
        } else {
            this.add(new DynaProperty(name, type));
        }
    }

    @Override
    public void add(String name, Class<?> type, boolean readable, boolean writeable) {
        throw new UnsupportedOperationException("readable/writable properties not supported");
    }

    protected void add(DynaProperty property) {
        if (property.getName() == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        if (this.isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No new properties can be added.");
        }
        if (this.propertiesMap.get(property.getName()) != null) {
            return;
        }
        DynaProperty[] oldProperties = this.getDynaProperties();
        DynaProperty[] newProperties = new DynaProperty[oldProperties.length + 1];
        System.arraycopy(oldProperties, 0, newProperties, 0, oldProperties.length);
        newProperties[oldProperties.length] = property;
        this.setProperties(newProperties);
    }

    @Override
    public void remove(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        if (this.isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No properties can be removed.");
        }
        if (this.propertiesMap.get(name) == null) {
            return;
        }
        DynaProperty[] oldProperties = this.getDynaProperties();
        DynaProperty[] newProperties = new DynaProperty[oldProperties.length - 1];
        int j = 0;
        for (int i = 0; i < oldProperties.length; ++i) {
            if (name.equals(oldProperties[i].getName())) continue;
            newProperties[j] = oldProperties[i];
            ++j;
        }
        this.setProperties(newProperties);
    }

    @Override
    public DynaProperty getDynaProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        DynaProperty dynaProperty = (DynaProperty)this.propertiesMap.get(name);
        if (dynaProperty == null && !this.isReturnNull() && !this.isRestricted()) {
            dynaProperty = new DynaProperty(name);
        }
        return dynaProperty;
    }

    public boolean isDynaProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        return this.propertiesMap.get(name) != null;
    }
}

