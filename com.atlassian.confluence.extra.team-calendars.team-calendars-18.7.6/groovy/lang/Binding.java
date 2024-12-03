/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Binding
extends GroovyObjectSupport {
    private Map variables;

    public Binding() {
    }

    public Binding(Map variables) {
        this.variables = variables;
    }

    public Binding(String[] args) {
        this();
        this.setVariable("args", args);
    }

    public Object getVariable(String name) {
        if (this.variables == null) {
            throw new MissingPropertyException(name, this.getClass());
        }
        Object result = this.variables.get(name);
        if (result == null && !this.variables.containsKey(name)) {
            throw new MissingPropertyException(name, this.getClass());
        }
        return result;
    }

    public void setVariable(String name, Object value) {
        if (this.variables == null) {
            this.variables = new LinkedHashMap();
        }
        this.variables.put(name, value);
    }

    public boolean hasVariable(String name) {
        return this.variables != null && this.variables.containsKey(name);
    }

    public Map getVariables() {
        if (this.variables == null) {
            this.variables = new LinkedHashMap();
        }
        return this.variables;
    }

    @Override
    public Object getProperty(String property) {
        try {
            return super.getProperty(property);
        }
        catch (MissingPropertyException e) {
            return this.getVariable(property);
        }
    }

    @Override
    public void setProperty(String property, Object newValue) {
        try {
            super.setProperty(property, newValue);
        }
        catch (MissingPropertyException e) {
            this.setVariable(property, newValue);
        }
    }
}

