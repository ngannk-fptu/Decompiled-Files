/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.util.HashMap;
import java.util.Map;

public class ClassExtender {
    private Map variables;
    private Map methods;

    public synchronized Object get(String name) {
        if (this.variables != null) {
            return this.variables.get(name);
        }
        return null;
    }

    public synchronized void set(String name, Object value) {
        if (this.variables == null) {
            this.variables = this.createMap();
        }
        this.variables.put(name, value);
    }

    public synchronized void remove(String name) {
        if (this.variables != null) {
            this.variables.remove(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void call(String name, Object params) {
        Closure closure = null;
        ClassExtender classExtender = this;
        synchronized (classExtender) {
            if (this.methods != null) {
                closure = (Closure)this.methods.get(name);
            }
        }
        if (closure != null) {
            closure.call(params);
        }
    }

    public synchronized void addMethod(String name, Closure closure) {
        if (this.methods == null) {
            this.methods = this.createMap();
        }
        this.methods.put(name, this.methods);
    }

    public synchronized void removeMethod(String name) {
        if (this.methods != null) {
            this.methods.remove(name);
        }
    }

    protected Map createMap() {
        return new HashMap();
    }
}

