/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class Annotations
implements Serializable,
Iterable<Annotation> {
    private final Annotation[] NO_ANNOTATIONS = new Annotation[0];
    protected LinkedHashMap<Class<? extends Annotation>, Annotation> _annotations;

    public void add(Annotation override) {
        if (this._annotations == null) {
            this._annotations = new LinkedHashMap();
        }
        this._annotations.put(override.annotationType(), override);
    }

    public void addAll(Annotations overrides) {
        if (this._annotations == null) {
            this._annotations = new LinkedHashMap();
        }
        for (Annotation override : overrides._annotations.values()) {
            this._annotations.put(override.annotationType(), override);
        }
    }

    public void addAsDefault(Annotation defValue) {
        Class<? extends Annotation> type = defValue.annotationType();
        if (this._annotations == null) {
            this._annotations = new LinkedHashMap();
            this._annotations.put(type, defValue);
        } else if (!this._annotations.containsKey(type)) {
            this._annotations.put(type, defValue);
        }
    }

    @Override
    public Iterator<Annotation> iterator() {
        if (this._annotations == null) {
            this._annotations = new LinkedHashMap();
        }
        return this._annotations.values().iterator();
    }

    public int size() {
        return this._annotations == null ? 0 : this._annotations.size();
    }

    public <A extends Annotation> A get(Class<A> cls) {
        if (this._annotations == null) {
            return null;
        }
        return (A)this._annotations.get(cls);
    }

    public Annotation[] asArray() {
        if (this._annotations == null || this._annotations.isEmpty()) {
            return this.NO_ANNOTATIONS;
        }
        return this._annotations.values().toArray(new Annotation[this._annotations.size()]);
    }

    public List<Annotation> asList() {
        if (this._annotations == null || this._annotations.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Annotation> l = new ArrayList<Annotation>(this._annotations.size());
        l.addAll(this._annotations.values());
        return l;
    }

    public String toString() {
        if (this._annotations == null) {
            return "[null]";
        }
        return this._annotations.toString();
    }
}

