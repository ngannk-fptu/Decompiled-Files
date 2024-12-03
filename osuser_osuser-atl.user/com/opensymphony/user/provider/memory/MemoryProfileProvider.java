/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 */
package com.opensymphony.user.provider.memory;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.ProfileProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MemoryProfileProvider
implements ProfileProvider {
    public static Map propertySets;

    public PropertySet getPropertySet(String name) {
        if (!propertySets.containsKey(name)) {
            return null;
        }
        return (PropertySet)propertySets.get(name);
    }

    public boolean create(String name) {
        if (propertySets.containsKey(name)) {
            return false;
        }
        PropertySet propertySet = PropertySetManager.getInstance((String)"memory", null);
        propertySets.put(name, propertySet);
        return true;
    }

    public void flushCaches() {
    }

    public boolean handles(String name) {
        return propertySets.containsKey(name);
    }

    public boolean init(Properties properties) {
        propertySets = new HashMap();
        return true;
    }

    public List list() {
        return null;
    }

    public boolean load(String name, Entity.Accessor accessor) {
        return true;
    }

    public boolean remove(String name) {
        return propertySets.remove(name) != null;
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return true;
    }
}

