/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ContextTransaction
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, Object> properties = new HashMap<String, Object>();

    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public void removeProperty(String name) {
        this.properties.remove(name);
    }

    public Iterator<String> listProperties() {
        return this.properties.keySet().iterator();
    }

    public String toString() {
        return "Transaction[]";
    }
}

