/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api;

import com.oracle.webservices.api.message.BasePropertySet;
import java.util.Map;
import java.util.Set;

public abstract class PropertySet
extends BasePropertySet {
    protected static PropertyMap parse(Class clazz) {
        BasePropertySet.PropertyMap pm = BasePropertySet.parse(clazz);
        PropertyMap map = new PropertyMap();
        map.putAll(pm);
        return map;
    }

    @Override
    public Object get(Object key) {
        BasePropertySet.Accessor sp = (BasePropertySet.Accessor)this.getPropertyMap().get(key);
        if (sp != null) {
            return sp.get(this);
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }

    @Override
    public Object put(String key, Object value) {
        BasePropertySet.Accessor sp = (BasePropertySet.Accessor)this.getPropertyMap().get(key);
        if (sp != null) {
            Object old = sp.get(this);
            sp.set(this, value);
            return old;
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }

    @Override
    public boolean supports(Object key) {
        return this.getPropertyMap().containsKey(key);
    }

    @Override
    public Object remove(Object key) {
        BasePropertySet.Accessor sp = (BasePropertySet.Accessor)this.getPropertyMap().get(key);
        if (sp != null) {
            Object old = sp.get(this);
            sp.set(this, null);
            return old;
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }

    @Override
    protected void createEntrySet(Set<Map.Entry<String, Object>> core) {
        for (final Map.Entry e : this.getPropertyMap().entrySet()) {
            core.add(new Map.Entry<String, Object>(){

                @Override
                public String getKey() {
                    return (String)e.getKey();
                }

                @Override
                public Object getValue() {
                    return ((BasePropertySet.Accessor)e.getValue()).get(PropertySet.this);
                }

                @Override
                public Object setValue(Object value) {
                    BasePropertySet.Accessor acc = (BasePropertySet.Accessor)e.getValue();
                    Object old = acc.get(PropertySet.this);
                    acc.set(PropertySet.this, value);
                    return old;
                }
            });
        }
    }

    @Override
    protected abstract BasePropertySet.PropertyMap getPropertyMap();

    protected static class PropertyMap
    extends BasePropertySet.PropertyMap {
        protected PropertyMap() {
        }
    }
}

