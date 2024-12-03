/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.confluence.api.model.reference.Reference;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

final class EmptyReference<T>
extends Reference<T> {
    private final Class<T> referentClass;

    EmptyReference(Class<T> referentClass) {
        super(true);
        this.referentClass = referentClass;
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean isExpanded() {
        return true;
    }

    @Override
    public Map<Object, Object> getIdProperties() {
        return Collections.emptyMap();
    }

    @Override
    public Class<? extends T> referentClass() {
        return this.referentClass;
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.emptyIterator();
    }

    public String toString() {
        return "EmptyReference{referentClass=" + this.referentClass + '}';
    }
}

