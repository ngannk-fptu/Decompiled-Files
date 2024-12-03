/*
 * Decompiled with CFR 0.152.
 */
package antlr.collections.impl;

import antlr.collections.impl.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

public class IndexedVector {
    protected Vector elements;
    protected Hashtable index;

    public IndexedVector() {
        this.elements = new Vector(10);
        this.index = new Hashtable(10);
    }

    public IndexedVector(int n) {
        this.elements = new Vector(n);
        this.index = new Hashtable(n);
    }

    public synchronized void appendElement(Object object, Object object2) {
        this.elements.appendElement(object2);
        this.index.put(object, object2);
    }

    public Object elementAt(int n) {
        return this.elements.elementAt(n);
    }

    public Enumeration elements() {
        return this.elements.elements();
    }

    public Object getElement(Object object) {
        Object v = this.index.get(object);
        return v;
    }

    public synchronized boolean removeElement(Object object) {
        Object v = this.index.get(object);
        if (v == null) {
            return false;
        }
        this.index.remove(object);
        this.elements.removeElement(v);
        return false;
    }

    public int size() {
        return this.elements.size();
    }
}

