/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.util.Map;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.MutableDynaClass;

public class LazyDynaMap
extends LazyDynaBean
implements MutableDynaClass {
    protected String name;
    protected boolean restricted;
    protected boolean returnNull = false;

    public LazyDynaMap() {
        this(null, (Map<String, Object>)null);
    }

    public LazyDynaMap(String name) {
        this(name, (Map<String, Object>)null);
    }

    public LazyDynaMap(Map<String, Object> values) {
        this(null, values);
    }

    public LazyDynaMap(String name, Map<String, Object> values) {
        this.name = name == null ? "LazyDynaMap" : name;
        this.values = values == null ? this.newMap() : values;
        this.dynaClass = this;
    }

    public LazyDynaMap(DynaProperty[] properties) {
        this(null, properties);
    }

    public LazyDynaMap(String name, DynaProperty[] properties) {
        this(name, (Map<String, Object>)null);
        if (properties != null) {
            for (DynaProperty propertie : properties) {
                this.add(propertie);
            }
        }
    }

    public LazyDynaMap(DynaClass dynaClass) {
        this(dynaClass.getName(), dynaClass.getDynaProperties());
    }

    public void setMap(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public Map<String, Object> getMap() {
        return this.values;
    }

    @Override
    public void set(String name, Object value) {
        if (this.isRestricted() && !this.values.containsKey(name)) {
            throw new IllegalArgumentException("Invalid property name '" + name + "' (DynaClass is restricted)");
        }
        this.values.put(name, value);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DynaProperty getDynaProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        if (!this.values.containsKey(name) && this.isReturnNull()) {
            return null;
        }
        Object value = this.values.get(name);
        if (value == null) {
            return new DynaProperty(name);
        }
        return new DynaProperty(name, value.getClass());
    }

    @Override
    public DynaProperty[] getDynaProperties() {
        int i = 0;
        DynaProperty[] properties = new DynaProperty[this.values.size()];
        for (Map.Entry e : this.values.entrySet()) {
            String name = (String)e.getKey();
            Object value = this.values.get(name);
            properties[i++] = new DynaProperty(name, value == null ? null : value.getClass());
        }
        return properties;
    }

    @Override
    public DynaBean newInstance() {
        Map newMap = null;
        try {
            Map temp;
            newMap = temp = (Map)this.getMap().getClass().newInstance();
        }
        catch (Exception ex) {
            newMap = this.newMap();
        }
        LazyDynaMap lazyMap = new LazyDynaMap(newMap);
        DynaProperty[] properties = this.getDynaProperties();
        if (properties != null) {
            for (DynaProperty propertie : properties) {
                lazyMap.add(propertie);
            }
        }
        return lazyMap;
    }

    @Override
    public boolean isRestricted() {
        return this.restricted;
    }

    @Override
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    @Override
    public void add(String name) {
        this.add(name, null);
    }

    @Override
    public void add(String name, Class<?> type) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        if (this.isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No new properties can be added.");
        }
        Object value = this.values.get(name);
        if (value == null) {
            this.values.put(name, type == null ? null : this.createProperty(name, type));
        }
    }

    @Override
    public void add(String name, Class<?> type, boolean readable, boolean writeable) {
        throw new UnsupportedOperationException("readable/writable properties not supported");
    }

    protected void add(DynaProperty property) {
        this.add(property.getName(), property.getType());
    }

    @Override
    public void remove(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        if (this.isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No properties can be removed.");
        }
        if (this.values.containsKey(name)) {
            this.values.remove(name);
        }
    }

    public boolean isReturnNull() {
        return this.returnNull;
    }

    public void setReturnNull(boolean returnNull) {
        this.returnNull = returnNull;
    }

    @Override
    protected boolean isDynaProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        return this.values.containsKey(name);
    }
}

