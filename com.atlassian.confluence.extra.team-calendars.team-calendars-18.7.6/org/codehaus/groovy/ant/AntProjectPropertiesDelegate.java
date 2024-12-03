/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.Project
 */
package org.codehaus.groovy.ant;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.tools.ant.Project;

public class AntProjectPropertiesDelegate
extends Hashtable {
    private Project project;

    public AntProjectPropertiesDelegate(Project project) {
        this.project = project;
    }

    @Override
    public synchronized int hashCode() {
        return this.project.getProperties().hashCode();
    }

    @Override
    public synchronized int size() {
        return this.project.getProperties().size();
    }

    @Override
    public synchronized void clear() {
        throw new UnsupportedOperationException("Impossible to clear the project properties.");
    }

    @Override
    public synchronized boolean isEmpty() {
        return this.project.getProperties().isEmpty();
    }

    @Override
    public synchronized Object clone() {
        return this.project.getProperties().clone();
    }

    @Override
    public synchronized boolean contains(Object value) {
        return this.project.getProperties().contains(value);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return this.project.getProperties().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.project.getProperties().containsValue(value);
    }

    @Override
    public synchronized boolean equals(Object o) {
        return this.project.getProperties().equals(o);
    }

    @Override
    public synchronized String toString() {
        return this.project.getProperties().toString();
    }

    @Override
    public Collection values() {
        return this.project.getProperties().values();
    }

    @Override
    public synchronized Enumeration elements() {
        return this.project.getProperties().elements();
    }

    @Override
    public synchronized Enumeration keys() {
        return this.project.getProperties().keys();
    }

    public AntProjectPropertiesDelegate(Map t) {
        super(t);
    }

    @Override
    public synchronized void putAll(Map t) {
        Iterator iterator = t.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry e;
            Map.Entry entry = e = iterator.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set entrySet() {
        return this.project.getProperties().entrySet();
    }

    @Override
    public Set keySet() {
        return this.project.getProperties().keySet();
    }

    @Override
    public synchronized Object get(Object key) {
        return this.project.getProperties().get(key);
    }

    @Override
    public synchronized Object remove(Object key) {
        throw new UnsupportedOperationException("Impossible to remove a property from the project properties.");
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        Object oldValue = null;
        if (this.containsKey(key)) {
            oldValue = this.get(key);
        }
        this.project.setProperty(key.toString(), value.toString());
        return oldValue;
    }
}

