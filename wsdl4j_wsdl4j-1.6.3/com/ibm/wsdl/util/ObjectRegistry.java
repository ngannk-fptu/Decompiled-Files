/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.util;

import java.util.Hashtable;
import java.util.Map;

public class ObjectRegistry {
    Hashtable reg = new Hashtable();
    ObjectRegistry parent = null;

    public ObjectRegistry() {
    }

    public ObjectRegistry(Map initialValues) {
        if (initialValues != null) {
            for (String name : initialValues.keySet()) {
                this.register(name, initialValues.get(name));
            }
        }
    }

    public ObjectRegistry(ObjectRegistry parent) {
        this.parent = parent;
    }

    public void register(String name, Object obj) {
        this.reg.put(name, obj);
    }

    public void unregister(String name) {
        this.reg.remove(name);
    }

    public Object lookup(String name) throws IllegalArgumentException {
        Object obj = this.reg.get(name);
        if (obj == null && this.parent != null) {
            obj = this.parent.lookup(name);
        }
        if (obj == null) {
            throw new IllegalArgumentException("object '" + name + "' not in registry");
        }
        return obj;
    }
}

