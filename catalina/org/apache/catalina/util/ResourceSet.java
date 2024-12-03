/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.util;

import java.util.Collection;
import java.util.HashSet;
import org.apache.tomcat.util.res.StringManager;

public final class ResourceSet<T>
extends HashSet<T> {
    private static final long serialVersionUID = 1L;
    private boolean locked = false;
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.util");

    public ResourceSet() {
    }

    public ResourceSet(int initialCapacity) {
        super(initialCapacity);
    }

    public ResourceSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ResourceSet(Collection<T> coll) {
        super(coll);
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean add(T o) {
        if (this.locked) {
            throw new IllegalStateException(sm.getString("resourceSet.locked"));
        }
        return super.add(o);
    }

    @Override
    public void clear() {
        if (this.locked) {
            throw new IllegalStateException(sm.getString("resourceSet.locked"));
        }
        super.clear();
    }

    @Override
    public boolean remove(Object o) {
        if (this.locked) {
            throw new IllegalStateException(sm.getString("resourceSet.locked"));
        }
        return super.remove(o);
    }
}

