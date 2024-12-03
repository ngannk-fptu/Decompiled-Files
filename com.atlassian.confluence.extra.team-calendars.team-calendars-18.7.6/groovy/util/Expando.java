/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaExpandoProperty;
import groovy.lang.MissingPropertyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Expando
extends GroovyObjectSupport {
    private Map expandoProperties;

    public Expando() {
    }

    public Expando(Map expandoProperties) {
        this.expandoProperties = expandoProperties;
    }

    public Map getProperties() {
        if (this.expandoProperties == null) {
            this.expandoProperties = this.createMap();
        }
        return this.expandoProperties;
    }

    public List getMetaPropertyValues() {
        ArrayList<MetaExpandoProperty> ret = new ArrayList<MetaExpandoProperty>();
        Iterator iterator = this.getProperties().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry entry = o = iterator.next();
            ret.add(new MetaExpandoProperty(entry));
        }
        return ret;
    }

    @Override
    public Object getProperty(String property) {
        Object result = this.getProperties().get(property);
        if (result != null) {
            return result;
        }
        try {
            return super.getProperty(property);
        }
        catch (MissingPropertyException missingPropertyException) {
            return null;
        }
    }

    @Override
    public void setProperty(String property, Object newValue) {
        this.getProperties().put(property, newValue);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return super.invokeMethod(name, args);
        }
        catch (GroovyRuntimeException e) {
            Object value = this.getProperty(name);
            if (value instanceof Closure) {
                Closure closure = (Closure)value;
                closure = (Closure)closure.clone();
                closure.setDelegate(this);
                return closure.call((Object[])args);
            }
            throw e;
        }
    }

    public String toString() {
        Object method = this.getProperties().get("toString");
        if (method != null && method instanceof Closure) {
            Closure closure = (Closure)method;
            closure.setDelegate(this);
            return closure.call().toString();
        }
        return this.expandoProperties.toString();
    }

    public boolean equals(Object obj) {
        Object method = this.getProperties().get("equals");
        if (method != null && method instanceof Closure) {
            Closure closure = (Closure)method;
            closure.setDelegate(this);
            Boolean ret = (Boolean)closure.call(obj);
            return ret;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        Object method = this.getProperties().get("hashCode");
        if (method != null && method instanceof Closure) {
            Closure closure = (Closure)method;
            closure.setDelegate(this);
            Integer ret = (Integer)closure.call();
            return ret;
        }
        return super.hashCode();
    }

    protected Map createMap() {
        return new HashMap();
    }
}

